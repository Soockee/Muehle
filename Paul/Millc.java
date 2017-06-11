
package mill;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 03.06.2017.
 */
public class Millc implements Board {

    private int[] board = new int[24];
    private int turn = +1;
    private int depth = 0;
    private int movesWithoutRemove = 0;
    private Millc parent = null;
    private int[] stones = new int[]{0, 0}; // +1, -1

    public Millc undoMove() {
        return parent;
    }

    public Millc makeMove(Moveable moveable) {
        if (depth < 18) return makeMovePlace(moveable);
        return makeMovePhaseMoveAndJump(moveable);
    }

    Millc makeMovePlace(Moveable moveable) {
        Millc child = new Millc();
        child.board = Arrays.copyOf(board, 24);
        child.board[moveable.getTo()] = turn;
        child.turn = -turn;
        child.parent = this;
        child.depth = depth + 1;
        child.stones = Arrays.copyOf(stones, 2);
        child.stones[turn == 1 ? 0 : 1] += 1;
        if (moveable.getRemove() == -1) {
            child.movesWithoutRemove = movesWithoutRemove + 1;
        } else {
            child.movesWithoutRemove = 0;
            child.board[moveable.getRemove()] = 0;
            child.stones[turn == 1 ? 1 : 0] -= 1;
        }
        return child;
    }

    Millc makeMovePhaseMoveAndJump(Moveable moveable) {
        Millc child = new Millc();
        child.board = Arrays.copyOf(board, 24);
        child.board[moveable.getTo()] = turn;
        child.turn = -turn;
        child.depth = depth + 1;
        child.parent = this;
        child.stones = Arrays.copyOf(stones, 2);
        child.board[moveable.getFrom()] = 0;
        child.movesWithoutRemove = movesWithoutRemove + 1;
        if (moveable.getRemove() == -1) {
            child.movesWithoutRemove = movesWithoutRemove + 1;
        } else {
            child.movesWithoutRemove = 0;
            child.board[moveable.getRemove()] = 0;
            child.stones[turn == 1 ? 1 : 0] -= 1;
        }
        return child;
    }

    public Millc removeStone(int toberemoved) {
        Millc res = new Millc();
        res.parent = parent;
        res.depth = depth;
        res.turn = turn;
        res.board = Arrays.copyOf(board, 24);
        res.board[toberemoved] = 0;
        res.stones = Arrays.copyOf(stones, 2);
        res.stones[turn == 1 ? 1 : 0] -= 1;
        return res;
    }

    public Stream<Board> streamChilds() {
        if (depth < 18) return streamChildsPhasePlace();
        else if (stones[turn == 1 ? 0 : 1] > 3)
            return streamChildsPhaseMove();
        return streamChildsPhaseJump();
    }

    Stream<Board> streamChildsPhasePlace() {
        return IntStream.range(0, 24)
                .filter(n -> board[n] == 0)
                .mapToObj(Move::new)
                .map(move -> streamMoves(move, this::makeMovePlace))
                .flatMap(millStream -> millStream);
    }

    Stream<Board> streamChildsPhaseMove() {
        final int[][] moves = {
                {1, 7}, {0, 2, 9}, {1, 3}, {2, 11, 4}, {3, 5}, {4, 13, 6}, {5, 7}, {0, 6, 15},
                {9, 15}, {1, 8, 10, 17}, {9, 11}, {3, 10, 12, 19}, {11, 13}, {5, 12, 14, 21}, {13, 15}, {7, 8, 14, 23},
                {17, 23}, {9, 16, 18}, {17, 19}, {18, 11, 20}, {19, 21}, {13, 20, 22}, {21, 23}, {15, 16, 22}
        };
        return IntStream.range(0, 24)
                .filter(i -> board[i] == turn)
                .mapToObj(i -> Arrays.stream(moves[i])
                        .filter(i1 -> board[i1] == 0)
                        .mapToObj(i1 -> new Move(i, i1))
                )
                .flatMap(moveStream -> moveStream)
                .map(move -> streamMoves(move, this::makeMovePhaseMoveAndJump))
                .flatMap(millStream -> millStream);
    }

    Stream<Board> streamChildsPhaseJump() {
        return IntStream.range(0, 24)
                .filter(from -> board[from] == turn)
                .mapToObj(from -> IntStream.range(0, 24)
                        .filter(to -> board[to] == 0)
                        .mapToObj(to -> new Move(from, to))
                        .map(move -> streamMoves(move, this::makeMovePhaseMoveAndJump))
                        .flatMap(millStream -> millStream)
                ).flatMap(millStream -> millStream);
    }

    Stream<Board> streamMoves(Moveable moveable) {
        return streamMoves(moveable, this::makeMove);
    }

    Stream<Board> streamMoves(Moveable moveable, Function<Moveable, Millc> f) {
        Millc res = f.apply(moveable);
        if (res.numberOfMills() <= numberOfMills()) return Stream.of(res);
        int[] openStone = findOpenStones(res.turn).toArray();
        if (openStone.length == 0) {
            return Stream.of(res).map(mill ->
                    IntStream.range(0, 24) // if all Stones are in Mills, Mills can be broken
                            .filter(i -> i == turn)
                            .mapToObj(mill::removeStone)
            )
                    .flatMap(millStream -> millStream);
        }
        return Stream.of(res).map(mill -> Arrays.stream(openStone)
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
                {{16, 23, 22}, {7, 15, 23}}
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

    int numberOfMills() {
        final int[][] mills = {
                {0, 1, 2},
                {8, 9, 10},
                {16, 17, 18},
                {7, 15, 23},
                {19, 11, 3},
                {22, 21, 20},
                {14, 13, 12},
                {6, 5, 4},
                {0, 7, 6},
                {8, 15, 14},
                {16, 22, 23},
                {1, 9, 17},
                {21, 13, 5},
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

    public Moveable getMove(Board child) throws IllegalArgumentException {
        Moveable res = new Move();
        if (!(child instanceof Millc)) throw new IllegalArgumentException("input hast to be of type Millc");
        Millc mill = (Millc) child;
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
                .orElseThrow(IllegalArgumentException::new)
        );
        res.setRemove(IntStream.range(0, 24)
                .filter(i -> board[i] == -turn)
                .filter(i -> mill.board[i] == 0)
                .findAny()
                .orElse(-1)
        );
        if (!(isValidMove(res) && makeMove(res).equals(mill)))
            throw new IllegalArgumentException("no valid MorrisMove exists");
        return res;
    }

    public boolean isValidMove(Moveable moveable) {
        return moveable.getFrom() < 24 && moveable.getFrom() >= -1 &&
                moveable.getTo() < 24 && moveable.getTo() >= -1 &&
                moveable.getRemove() < 24 && moveable.getRemove() >= -1 &&
                streamChilds().anyMatch(mill -> makeMove(moveable).equals(mill));
    }

    public int getDepth() {
        return depth;
    }

    public int getTurn() {
        return turn;
    }

    public boolean isWin() { //return true after winning game, player -turn wins
        return depth > 18 && (streamChilds().count() == 0 ||
                Arrays.stream(board)
                        .filter(n -> turn == n)
                        .count() < 3);
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

    @Override
    public int hashCode() {
        return turn * toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Millc) {
            Millc other = (Millc) obj;
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
    public Board makeMove(Moveable... moveable) {
        Millc res = (Millc) copy();
        for (Moveable move : moveable)
            res = res.makeMove(move);
        return res;
    }

    @Override
    public boolean isDraw() {
        return movesWithoutRemove >= 50;
    }

    @Override
    public Board save(String saveFile) {
        Millc child = (Millc) copy();
        try (BufferedWriter out = Files.newBufferedWriter(Paths.get(saveFile), StandardCharsets.UTF_8)) {
            while (child.parent != null) {
                Moveable moveable = child.parent.getMove(child);
                out.write(moveable.getFrom());
                out.write(",");
                out.write(moveable.getTo());
                out.write(",");
                out.write(moveable.getRemove());
                out.write(";");
                child = child.parent;
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
        Millc load = new Millc();
        try {
            List<Moveable> lst = Files.lines(Paths.get(loadFile), StandardCharsets.UTF_8)
                    .map(s -> s.split(";"))
                    .flatMap(Arrays::stream)
                    .map(s -> Arrays.stream(s.split(","))
                            .map(Integer::parseInt)
                            .toArray(Integer[]::new)
                    )
                    .map(integers -> new Move(integers[0], integers[1], integers[2]))
                    .collect(Collectors.toList());
            for (Moveable moveable : lst) {
                if (!(load.isValidMove(moveable))) throw new IllegalArgumentException("File was corrupted");
                load = load.makeMove(moveable);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
            System.out.println(ioe.getMessage());
        }
        return load;
    }

    @Override
    public Board load() {
        return load("src/saves/save.txt");
    }


    public Board copy() {
        Millc copy = new Millc();
        copy.board = Arrays.copyOf(board, 24);
        copy.stones = Arrays.copyOf(stones, 2);
        copy.turn = turn;
        copy.movesWithoutRemove = movesWithoutRemove;
        copy.parent = parent;
        copy.depth = depth;
        return copy;
    }

    Millc rotate() {
        final int[] newboard = {6, 7, 0, 1, 2, 3, 4, 5, 14, 15, 8, 9, 10, 11, 12, 13, 22, 23, 16, 17, 18, 19, 20, 21};
        Millc res = new Millc();
        res.turn = turn;
        res.stones = Arrays.copyOf(stones, 2);
        res.depth = depth;
        res.parent = parent;
        res.movesWithoutRemove = movesWithoutRemove;
        res.board = Arrays.stream(newboard).map(i -> board[i]).toArray();
        return res;
    }

    Millc mirror() {
        final int[] newboard = {2, 1, 0, 7, 6, 5, 4, 3, 10, 9, 8, 15, 14, 13, 12, 11, 18, 17, 16, 23, 22, 21, 20, 19};
        Millc res = new Millc();
        res.turn = turn;
        res.parent = parent;
        res.stones = Arrays.copyOf(stones, 2);
        res.depth = depth;
        res.movesWithoutRemove = movesWithoutRemove;
        res.board = Arrays.stream(newboard).map(i -> board[i]).toArray();
        return res;
    }

    Millc swapInnerAndOuterRing() {
        final int[] newboard = {16, 17, 18, 19, 20, 21, 22, 23, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
        Millc res = new Millc();
        res.stones = Arrays.copyOf(stones, 2);
        res.turn = turn;
        res.parent = parent;
        res.depth = depth;
        res.board = Arrays.stream(newboard).map(i -> board[i]).toArray();
        res.movesWithoutRemove = movesWithoutRemove;
        return res;
    }
}