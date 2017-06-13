package mill.src;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch, Björn Franke and Simon Stockhause
 * Implementation of the game "Mill"
 */
public class Mill implements Board {

    int[] board = new int[24];
    int turn = +1;
    int depth = 0;
    int moveswithoutremoving = 0;
    Mill parent = null;
    int phase = 1; // 1 => setzen 2 => beide ziehen 3 =>  erster springt, zweiter zieht 4=> erster zieht, zweiter spirngt 5=> beide springen

    public Mill undoMove() {
        return parent;
    }

    public Mill makeMove(Movable movable) {
        Mill res;
        if (phase == 1) res = makeMovePhasePlace(movable);
        else res = makeMovePhaseMoveAndJump(movable);
        if (movable.getRemove() != -1) return res.removeStone(movable.getRemove());
        return res;
    }

    Mill makeMovePhasePlace(Movable movable) {
        Mill child = new Mill();
        child.board = Arrays.copyOf(board, 24);
        child.board[movable.getTo()] = turn;
        child.turn = -turn;
        child.depth = depth + 1;
        child.parent = this;
        child.phase = phase;
        child.moveswithoutremoving = moveswithoutremoving + 1;
        if (child.depth == 18) child.phase = 2;
        return child;
    }

    Mill makeMovePhaseMoveAndJump(Movable movable) {
        Mill child = new Mill();
        child.board = Arrays.copyOf(board, 24);
        child.board[movable.getTo()] = turn;
        child.turn = -turn;
        child.depth = depth + 1;
        child.parent = this;
        child.phase = phase;
        child.board[movable.getFrom()] = 0;
        child.moveswithoutremoving = moveswithoutremoving + 1;
        return child;
    }

    Mill removeStone(int toBeRemoved) {
        Mill res = new Mill();
        res.board = Arrays.copyOf(board, 24);
        res.parent = parent;
        res.depth = depth;
        res.board[toBeRemoved] = 0;
        res.moveswithoutremoving = 0;
        res.phase = phase;
        res.turn = turn;
        if (res.phase != 5 && res.checkForPhaseJump()) { // phase1 stays at 1
            if (res.phase == 2) res.phase = res.turn == 1 ? 3 : 4;
            else if (res.phase == 3) res.phase = res.turn == 1 ? 3 : 5;
            else if (res.phase == 4) res.phase = res.turn == 1 ? 5 : 4;
        }
        return res;
    }

    boolean checkForPhaseJump() {
        return Arrays.stream(board)
                .filter(n -> n == turn).count() < 4;
    }

    public Stream<Board> streamChilds() {
        if (phase == 1) return streamChildsPhasePlace();
        else if (phase == 2 || phase == 3 && turn == -1 || phase == 4 && turn == +1)
            return streamChildsPhaseMove();
        return streamChildsPhaseJump();
    }

    Stream<Board> streamChildsPhasePlace() {
        return IntStream.range(0, 24)
                .filter(to -> board[to] == 0)
                .mapToObj(Move::new)
                .map(move -> streamOutcomes(move, this::makeMovePhasePlace))
                .flatMap(millStream -> millStream);
    }

    Stream<Board> streamChildsPhaseMove() {
        final int[][] moves = {
                {1, 7}, {0, 2, 9}, {1, 3}, {2, 11, 4}, {3, 5}, {4, 13, 6}, {5, 7}, {0, 6, 15},
                {9, 15}, {1, 8, 10, 17}, {9, 11}, {3, 10, 12, 19}, {11, 13}, {5, 12, 14, 21}, {13, 15}, {7, 8, 14, 23},
                {17, 23}, {9, 16, 18}, {17, 19}, {18, 11, 20}, {19, 21}, {13, 20, 22}, {21, 23}, {15, 16, 22}
        };
        return IntStream.range(0, 24)
                .filter(from -> board[from] == turn)
                .mapToObj(from -> Arrays.stream(moves[from])
                        .filter(to -> board[to] == 0)
                        .mapToObj(to -> new Move(from, to))
                )
                .flatMap(moveStream -> moveStream)
                .map(move -> streamOutcomes(move, this::makeMovePhaseMoveAndJump))
                .flatMap(millStream -> millStream);
    }

    Stream<Board> streamChildsPhaseJump() {
        return IntStream.range(0, 24)
                .filter(from -> board[from] == turn)
                .mapToObj(from -> IntStream.range(0, 24)
                        .filter(to -> board[to] == 0)
                        .mapToObj(to -> new Move(from, to))
                        .map(move -> streamOutcomes(move, this::makeMovePhaseMoveAndJump))
                        .flatMap(millStream -> millStream)
                ).flatMap(millStream -> millStream);
    }

    //---- unused method? what is it doin? --
    Stream<Board> streamOutcomes(Movable movable) {
        return streamOutcomes(movable, this::makeMove);
    }

    Stream<Board> streamOutcomes(Movable movable, Function<Movable, Mill> f) {
        Mill res = f.apply(movable);
        if (numberOfClosedMills(res, movable.getTo()) == 0) return Stream.of(res);
        int[] openstone = findOpenStones(res.turn).toArray();
        if (openstone.length == 0) { //if all Stones are in Mills, Mills can be broken
            return Stream.of(res).map(mill ->
                    IntStream.range(0, 24)
                            .filter(i -> i == turn)
                            .mapToObj(mill::removeStone)
            )
                    .flatMap(millStream -> millStream);
        }
        return Stream.of(res).map(mill -> Arrays.stream(openstone)
                .mapToObj(mill::removeStone)
        )
                .flatMap(millStream -> millStream);
    }

    IntStream findOpenStones(int player) {
        final int[][][] mills = {
                {{0, 1, 2,}, {0, 6, 7}},
                {{0, 1, 2}, {1, 9, 17}},
                {{0, 1, 2}, {2, 3, 4}},
                {{2, 3, 4}, {19, 11, 3}},
                {{2, 3, 4}, {6, 5, 4}},
                {{6, 5, 4}, {21, 13, 5}},
                {{6, 5, 4}, {0, 6, 7}},
                {{0, 7, 6}, {7, 15, 23}},

                {{8, 9, 10}, {14, 15, 8}},
                {{8, 9, 10}, {1, 9, 17}},
                {{8, 9, 10}, {10, 11, 12}},
                {{10, 11, 12}, {19, 11, 3}},
                {{10, 11, 12}, {14, 13, 12}},
                {{14, 13, 12}, {21, 13, 5}},
                {{14, 13, 12}, {14, 15, 8}},
                {{14, 15, 8}, {7, 15, 23}},

                {{16, 17, 18}, {16, 23, 22}},
                {{16, 17, 18}, {1, 9, 10}},
                {{16, 17, 18}, {18, 19, 20}},
                {{18, 19, 20}, {19, 11, 3}},
                {{18, 19, 20}, {22, 21, 20}},
                {{22, 21, 20}, {21, 13, 22}},
                {{22, 21, 20}, {16, 23, 22}},
                {{16, 23, 22}, {7, 15, 23}},
        };
        return IntStream.range(0, 24)
                .filter(i -> board[i] == player)
                .filter(i ->
                        Arrays.stream(mills[i])
                                .map((int[] ints) -> (Arrays.stream(ints)
                                                .map(i1 -> board[i1])
                                                .map(Math::abs)
                                                .sum()
                                        )
                                )
                                .filter(integer -> integer == 3)
                                .count() == 0
                );
    }

    long numberOfClosedMills(Mill child, int lastAdd) {
        final int[][][] mills = {
                {{0, 1, 2,}, {0, 6, 7}},
                {{0, 1, 2}, {1, 9, 17}},
                {{0, 1, 2}, {2, 3, 4}},
                {{2, 3, 4}, {19, 11, 3}},
                {{2, 3, 4}, {6, 5, 4}},
                {{6, 5, 4}, {21, 13, 5}},
                {{6, 5, 4}, {0, 6, 7}},
                {{0, 7, 6}, {7, 15, 23}},

                {{8, 9, 10}, {14, 15, 8}},
                {{8, 9, 10}, {1, 9, 17}},
                {{8, 9, 10}, {10, 11, 12}},
                {{10, 11, 12}, {19, 11, 3}},
                {{10, 11, 12}, {14, 13, 12}},
                {{14, 13, 12}, {21, 13, 5}},
                {{14, 13, 12}, {14, 15, 8}},
                {{14, 15, 8}, {7, 15, 23}},

                {{16, 17, 18}, {16, 23, 22}},
                {{16, 17, 18}, {1, 9, 10}},
                {{16, 17, 18}, {18, 19, 20}},
                {{18, 19, 20}, {19, 11, 3}},
                {{18, 19, 20}, {22, 21, 20}},
                {{22, 21, 20}, {21, 13, 22}},
                {{22, 21, 20}, {16, 23, 22}},
                {{16, 23, 22}, {7, 15, 23}}
        };
        return Arrays.stream(mills[lastAdd])
                .filter(ints -> Arrays.stream(ints)
                        .filter(i -> board[i] == child.board[i])
                        .count() == 3)
                .count();
    }

    // -- unused method?
    int numberOfMills() {
        final int[][] mills = {
                {0, 1, 2},
                {8, 9, 10},
                {16, 17, 18},
                {7, 15, 23},    // !
                {19, 11, 3},    // !
                {22, 21, 20},
                {14, 13, 12},
                {6, 5, 4},
                {0, 7, 6},      // !
                {8, 15, 14},    // !
                {16, 22, 23},   // !
                {1, 9, 17},     // !
                {21, 13, 5},    // !
                {18, 19, 20},
                {10, 11, 12},
                {2, 3, 4}
        };
        return (int) IntStream.range(0, 16)
                .map(mill -> Arrays.stream(mills[mill])
                        .map(n -> board[n])
                        .sum()
                )
                .map(Math::abs)
                .filter(n -> n == 3)
                .count();
    }

    // unused method?
    List<Movable> moves() {
        return streamChilds()
                .map(this::getMove)
                .collect(Collectors.toList());
    }

    public Movable getMove(Board child) throws IllegalArgumentException {
        Movable res = new Move();
        if (!(child instanceof Mill)) throw new IllegalArgumentException("input hast to be of type Mill");
        Mill mill = (Mill) child;
        res.setFrom(IntStream.range(0, 24)
                .filter(i -> board[i] == turn)
                .filter(i -> mill.board[i] == 0)
                .findAny()
                .orElse(-1)
        );
        res.setTo(IntStream.range(0, 24)
                .filter(i -> board[i] == 0)
                .filter(i -> mill.board[i] == turn)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("no Move possible"))
        );
        res.setRemove(IntStream.range(0, 24)
                .filter(i -> board[i] == -turn)
                .filter(i -> mill.board[i] == 0)
                .findAny()
                .orElse(-1)
        );
        //if (!(isValidMove(res) && makeMove(res).equals(mill)))
        //throw new IllegalArgumentException("no valid Move exists");
        return res;
    }

    public boolean isValidMove(Movable movable) {
        return movable.getFrom() < 24 && movable.getFrom() >= -1 &&
                movable.getTo() < 24 && movable.getTo() >= 0 &&
                movable.getRemove() < 24 && movable.getRemove() >= -1 &&
                streamChilds().anyMatch(mill -> makeMove(movable).equals(mill));
    }

    public int getDepth() {
        return depth;
    }

    public int getTurn() {
        return turn;
    }

    @Override
    public String toString() {
        final int[][] display = {
                {0, -4, -4, -4, -4, -4, 1, -4, -4, -4, -4, -4, 2},
                {-3, -2, -2, -2, -2, -2, -3, -2, -2, -2, -2, -2, -3},
                {-3, -2, 8, -4, -4, -4, 9, -4, -4, -4, 10, -2, -3},
                {-3, -2, -3, -2, -2, -2, -3, -2, -2, -2, -3, -2, -3},
                {-3, -2, -3, -2, 16, -4, 17, -4, 18, -2, -3, -2, -3},
                {7, -4, 15, -4, 23, -2, -2, -2, 19, -4, 11, -4, 3},
                {-3, -2, -3, -2, 22, -4, 21, -4, 20, -2, -3, -2, -3},
                {-3, -2, -3, -2, -2, -2, -3, -2, -2, -2, -3, -2, -3},
                {-3, -2, 14, -4, -4, -4, 13, -4, -4, -4, 12, -2, -3},
                {-3, -2, -2, -2, -2, -2, -3, -2, -2, -2, -2, -2, -3},
                {6, -4, -4, -4, -4, -4, 5, -4, -4, -4, -4, -4, 4}
        };

        char[] repr = {'O', '.', 'X', '-', '|', ' '}; // [3] u [4] = ' ' für Spielfeld ohne Linien
        return IntStream.rangeClosed(0, 10).mapToObj(row -> Arrays.stream(display[row])
                .boxed()
                .map(n -> (n < 0) ? n + 6 : board[n])
                .map(n -> repr[n + 1])
                .map(n -> Character.toString(n))
                .collect(Collectors.joining("  ")) //1-3 Felder Abstand
        ).collect(Collectors.joining("\n", "\n", "")); // prefix "\n" für jShell
    }

    public boolean isWin() { //return true after winning game, player -turn wins
        return phase != 1 && (streamChilds().count() == 0 ||
                Arrays.stream(board)
                        .filter(n -> turn == n)
                        .count() < 3);
    }


    @Override
    public int hashCode() {
        /*return IntStream.range(0, 24)
                .map(i -> (int) Math.pow(3, i) * board[i])
                .sum();*/
        return turn * toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Mill) {
            Mill other = (Mill) obj;
            return other.hashCode() == hashCode();
        }
        return false;
    }

    public IntStream getIDsOfGroup() {
        return IntStream.of(
                hashCode(),
                rotate().hashCode(),
                rotate().rotate().hashCode(),
                rotate().rotate().rotate().hashCode(),
                mirror().hashCode(),
                rotate().mirror().hashCode(),
                rotate().rotate().mirror().hashCode(),
                rotate().rotate().rotate().mirror().hashCode(),
                swapInnerAndOuterRing().hashCode(),
                swapInnerAndOuterRing().rotate().hashCode(),
                swapInnerAndOuterRing().rotate().rotate().hashCode(),
                swapInnerAndOuterRing().rotate().rotate().rotate().hashCode(),
                swapInnerAndOuterRing().mirror().hashCode(),
                swapInnerAndOuterRing().rotate().mirror().hashCode(),
                swapInnerAndOuterRing().rotate().rotate().mirror().hashCode(),
                swapInnerAndOuterRing().rotate().rotate().rotate().mirror().hashCode()
        );
    }

    @Override
    public Board makeMove(Movable... moveable) {
        Mill res = (Mill) copy();
        for (Movable move : moveable)
            res = res.makeMove(move);
        return res;
    }

    @Override
    public boolean isDraw() {
        return moveswithoutremoving >= 50;
    }

    List<Movable> getHistoy() {
        LinkedList<Movable> history = new LinkedList<>();
        Mill tmp = (Mill) copy();
        while (tmp.parent != null) {
            history.addFirst(tmp.parent.getMove(tmp));
            tmp = tmp.parent;
        }
        return history;
    }

    @Override
    public Board save(String saveFile) {
        List<Movable> history = getHistoy();
        int turnCounter = 1;
        try (BufferedWriter out = Files.newBufferedWriter(Paths.get(saveFile), StandardCharsets.UTF_8)) {
            for(Movable move : history) {
                out.write(String.format("Turn%3d: ", turnCounter++));
                out.write(move.getFrom() == -1 ? "   " : String.format("%3d", move.getFrom()));
                out.write(" -> ");
                out.write(String.format("%3d", move.getTo()));
                out.write(move.getRemove() == -1 ? "" : String.format(" : %3d", move.getRemove()));
                out.write("\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return copy();
    }

    @Override
    public Board save() {
        return save("src/saves/save.txt");
    }

    @Override
    public Board load(String loadFile) {
        final Pattern pattern;
        pattern = Pattern.compile(".*(\\d{1,2})?\\s*->\\s*(\\d{1,2})\\s*(\\s*:\\s*(\\d{1,2}]))?\\s*", Pattern.COMMENTS);
        Mill load = new Mill();
        try {
            List<Movable> lst = Files.lines(Paths.get(loadFile), StandardCharsets.UTF_8)
                    .filter(s -> !s.equals(""))
                    .map(pattern::matcher)
                    .map(matcher -> {
                        if (!matcher.find()) throw new IllegalArgumentException("File Format is Off");
                        return new Move(matcher.group(1) == null ? -1 : Integer.parseInt(matcher.group(1))
                                , Integer.parseInt(matcher.group(2)),
                                matcher.group(3) == null ? -1 : Integer.parseInt(matcher.group(3)));
                    })
                    .collect(Collectors.toList());
            for (Movable movable : lst) {
                if (!(load.isValidMove(movable))) {
                    System.out.println(load);
                    System.out.println(movable);
                    throw new IllegalArgumentException("File contains illegal Moves");
                }
                load = load.makeMove(movable);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return load;
    }

    @Override
    public Board load() {
        return load("src/saves/save.txt");
    }

    Mill rotate() {
        final int[] newboard = {6, 7, 0, 1, 2, 3, 4, 5, 14, 15, 8, 9, 10, 11, 12, 13, 22, 23, 16, 17, 18, 19, 20, 21};
        Mill res = new Mill();
        res.phase = phase;
        res.turn = turn;
        res.depth = depth;
        res.board = Arrays.stream(newboard).map(i -> board[i]).toArray();
        return res;
    }

    public Board copy() {
        Mill copy = new Mill();
        copy.board = Arrays.copyOf(board, 24);
        copy.parent = parent;
        copy.phase = phase;
        copy.turn = turn;
        copy.moveswithoutremoving = moveswithoutremoving;
        return copy;
    }

    Mill mirror() {
        final int[] newboard = {2, 1, 0, 7, 6, 5, 4, 3, 10, 9, 8, 15, 14, 13, 12, 11, 18, 17, 16, 23, 22, 21, 20, 19};
        Mill res = new Mill();
        res.phase = phase;
        res.turn = turn;
        res.depth = depth;
        res.board = Arrays.stream(newboard).map(i -> board[i]).toArray();
        return res;
    }

    Mill swapInnerAndOuterRing() {
        final int[] newboard = {16, 17, 18, 19, 20, 21, 22, 23, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
        Mill res = new Mill();
        res.phase = phase;
        res.turn = turn;
        res.depth = depth;
        res.board = Arrays.stream(newboard).map(i -> board[i]).toArray();
        return res;
    }
}
