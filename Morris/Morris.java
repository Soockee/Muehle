package Morris;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */

public class Morris implements ImmutableBoard<MorrisMove> {

    int[] board = new int[24];
    int turn = +1;
    int depth = 0;
    int moveswithoutremoving = 0; // used for detecting draws
    Morris parent = null;
    int phase = 1;
    boolean isFlipped = false;

    @Override
    public ImmutableBoard<MorrisMove> makeMove(MorrisMove morrisMove) {
        if (phase == 1) return makeMovePhasePlace(morrisMove);
        else return makeMovePhaseMoveAndJump(morrisMove);
    }

    Morris makeMovePhasePlace(MorrisMove morrisMove) {
        Morris child = new Morris();
        child.board = Arrays.copyOf(board, 24);
        child.board[morrisMove.getTo()] = turn;
        child.turn = -turn;
        child.isFlipped = isFlipped;
        child.depth = depth + 1;
        child.parent = this;
        if (morrisMove.getRemove() != -1) {
            child.board[morrisMove.getRemove()] = 0;
            child.moveswithoutremoving = 0;
        } else {
            child.moveswithoutremoving = moveswithoutremoving + 1;
        }
        if (child.depth == 18) child.phase = 2;
        else child.phase = phase;
        return child;
    }

    private Morris makeMovePhaseMoveAndJump(MorrisMove morrisMove) {
        Morris child = new Morris();
        child.board = Arrays.copyOf(board, 24);
        child.board[morrisMove.getTo()] = turn;
        child.turn = -turn;
        child.isFlipped = isFlipped;
        child.depth = depth + 1;
        child.parent = this;
        ;
        child.board[morrisMove.getFrom()] = 0;
        if (morrisMove.getRemove() != -1) {
            child.board[morrisMove.getRemove()] = 0;
            child.moveswithoutremoving = 0;
            if (child.phase != 5 && child.checkForPhaseJump()) { // phase1 stays at 1
                if (child.phase == 2) child.phase = child.turn == 1 ? 3 : 4;
                else phase = 5;
                //else if (child.phase == 3) child.phase = child.turn == 1 ? 3 : 5; // else child.phase = 5;
                //else if (child.phase == 4) child.phase = child.turn == 1 ? 5 : 4;
            }
        } else {
            child.phase = phase;
            child.moveswithoutremoving = moveswithoutremoving + 1;
        }
        return child;
    }

    private boolean checkForPhaseJump() {
        return Arrays.stream(board)
                .filter(n -> n == turn)
                .count() < 4;
    }

    @Override
    public ImmutableBoard<MorrisMove> undoMove() {
        return parent;
    }

    Stream<MorrisMove> streamMoves() {
        if (phase == 1) return streamMovesPhasePlace();
        else if (phase == 2 || phase == 3 && turn == -1 || phase == 4 && turn == +1)
            return streamMovesPhaseMove();
        return streamMovesPhaseJump();
    }

    private Stream<MorrisMove> streamMovesPhasePlace() {
        return IntStream.range(0, 24)
                .filter(to -> board[to] == 0)
                .mapToObj(MorrisMove::new)
                .map(this::streamMovesWithRemoves)
                .flatMap(morrisMoveStream -> morrisMoveStream);
    }

    private Stream<MorrisMove> streamMovesPhaseMove() {
        final int[][] moves = {
                {1, 7}, {0, 2, 9}, {1, 3}, {2, 11, 4}, {3, 5}, {4, 13, 6}, {5, 7}, {0, 6, 15},
                {9, 15}, {1, 8, 10, 17}, {9, 11}, {3, 10, 12, 19}, {11, 13}, {5, 12, 14, 21}, {13, 15}, {7, 8, 14, 23},
                {17, 23}, {9, 16, 18}, {17, 19}, {18, 11, 20}, {19, 21}, {13, 20, 22}, {21, 23}, {15, 16, 22}
        };
        return IntStream.range(0, 24)
                .filter(from -> board[from] == turn)
                .mapToObj(from -> Arrays.stream(moves[from])
                        .filter(to -> board[to] == 0)
                        .mapToObj(to -> new MorrisMove(from, to))
                )
                .flatMap(moveStream -> moveStream)
                .map(this::streamMovesWithRemoves)
                .flatMap(morrisMoveStream -> morrisMoveStream);
    }

    private Stream<MorrisMove> streamMovesPhaseJump() {
        return IntStream.range(0, 24)
                .filter(from -> board[from] == turn)
                .mapToObj(from -> IntStream.range(0, 24)
                        .filter(to -> board[to] == 0)
                        .mapToObj(to -> new MorrisMove(from, to))
                )
                .flatMap(morrisMoveStream -> morrisMoveStream)
                .map(this::streamMovesWithRemoves)
                .flatMap(morrisMoveStream -> morrisMoveStream);
    }

    private Stream<MorrisMove> streamMovesWithRemoves(MorrisMove morrisMove) {
        if (numberOfClosedPotentialMills(morrisMove.getTo()) > 0) {
            int[] openstone = findOpenStones(-turn).toArray();
            if (openstone.length == 0) { //if all Stones are in Mills, Mills can be broken
                return IntStream.range(0, 24)
                        .filter(i -> i == -turn)
                        .mapToObj(i -> new MorrisMove(morrisMove.getFrom(), morrisMove.getTo(), i));
            }
            return Arrays.stream(openstone)
                    .mapToObj(i -> new MorrisMove(morrisMove.getFrom(), morrisMove.getTo(), i));
        }
        return Stream.of(morrisMove);
    }

    long numberOfClosedPotentialMills(int potAdd) {
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
                {{16, 17, 18}, {1, 9, 17}},
                {{16, 17, 18}, {18, 19, 20}},
                {{18, 19, 20}, {19, 11, 3}},
                {{18, 19, 20}, {22, 21, 20}},
                {{22, 21, 20}, {21, 13, 22}},
                {{22, 21, 20}, {16, 23, 22}},
                {{16, 23, 22}, {7, 15, 23}}
        };
        return Arrays.stream(mills[potAdd])
                .map(ints -> Arrays.stream(ints)
                        .map(i -> board[i])
                        .filter(i -> i == turn)
                        .map(Math::abs)
                        .sum()
                )
                .filter(i -> i == 2)
                .count();
    }

    private IntStream findOpenStones(int player) {
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

    @Override
    public List<MorrisMove> moves() {
        return streamMoves().collect(Collectors.toList());
    }

    public MorrisMove getMove(Morris child) {
        MorrisMove res = new MorrisMove();
        res.setFrom(IntStream.range(0, 24)
                .filter(i -> board[i] == turn)
                .filter(i -> child.board[i] == 0)
                .findAny()
                .orElse(-1)
        );
        res.setTo(IntStream.range(0, 24)
                .filter(i -> board[i] == 0)
                .filter(i -> child.board[i] == turn)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("no MorrisMove possible"))
        );
        res.setRemove(IntStream.range(0, 24)
                .filter(i -> board[i] == -turn)
                .filter(i -> child.board[i] == 0)
                .findAny()
                .orElse(-1)
        );
        return res;
    }

    @Override
    public List<MorrisMove> getHistory() {
        LinkedList<MorrisMove> history = new LinkedList<>();
        Morris child = this;
        while (child.parent != null) {
            history.addFirst(child.parent.getMove(child));
            child = child.parent;
        }
        return history;
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
    public boolean isWin() { //return true after winning game, player -turn wins
        return phase != 1 && (streamMoves().count() == 0 ||
                Arrays.stream(board)
                        .filter(n -> turn == n)
                        .count() < 3);
    }

    @Override
    public boolean isDraw() {
        return moveswithoutremoving >= 50;
    }

    @Override
    public ImmutableBoard<MorrisMove> flip() {
        Morris res = new Morris();
        res.parent = parent;
        res.turn = -turn;
        res.depth = depth;
        res.isFlipped = !isFlipped;
        res.moveswithoutremoving = moveswithoutremoving;
        res.board = Arrays.stream(board).map(i -> -i).toArray();
        return res;
    }

    //\s*(?:Turn\s*\d*\s*:)?\s*(\d+)?\s*->\s*(\d+)\s*(?::\s*(\d+))?\s* regex for Loading
    @Override
    public ImmutableBoard<MorrisMove> load(String name) {
        return load(Paths.get(name));
    }

    @Override
    public ImmutableBoard<MorrisMove> load(Path path) {
        final Pattern pattern = Pattern.compile("\\s*(?:Turn\\s*\\d*:?\\s*:)?\\s*(\\d+)?\\s*->\\s*(\\d+)\\s*(?::\\s*(\\d+))?\\s*");
        ImmutableBoard<MorrisMove> morris = new Morris();
        try {
            List<MorrisMove> moves =  Files.lines(path, StandardCharsets.UTF_8)
                    .filter(s -> !s.isEmpty())
                    .map(pattern::matcher)
                    .map(matcher -> {
                        if (!matcher.matches()) throw new IllegalArgumentException("Save was corrupted");
                        return new MorrisMove(matcher.group(1) == null ? -1 : Integer.parseInt(matcher.group(1)),
                                Integer.parseInt(matcher.group(2)),
                                matcher.group(3) == null ? -1 : Integer.parseInt(matcher.group(3)));
                    })
                    .collect(Collectors.toList());
            for(MorrisMove move : moves) {
                morris = morris.makeMove(move);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return morris;
    }

    @Override
    public ImmutableBoard<MorrisMove> save(String name) {
        return save(Paths.get(name));
    }

    @Override
    public ImmutableBoard<MorrisMove> save(Path path) {
        try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            int turnCounter = 1;
            out.write("isFlipped: " + String.valueOf(isFlipped) + "\n");
            for (MorrisMove move : getHistory()) {
                out.write(String.format("Turn %3d:", turnCounter++));
                if (move.getFrom() != -1) {
                    out.write(String.format("%3d", move.getFrom()));
                } else out.write("   ");
                out.write(String.format(" -> %3d", move.getTo()));
                if (move.getRemove() != -1) {
                    out.write(String.format(" : %3d", move.getRemove()));
                }
                out.write("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this; // should maybe be a copy
    }
}
