

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 13.06.2017.
 */
public class T3 implements ImmutableBoard<Integer>, SaveableGame<T3> {

    private final int[] board;
    private final int turn;
    private final T3 previous;
    private final boolean isFlipped;
    public static void main(String[] args) {
        T3 board = new T3();
        board.streamMoves().forEach(System.out::println);
        board = (T3) board.makeMove(8, 5, 1);
        board.streamMoves().forEach(System.out::println);
        board = (T3) board.flip();
        System.out.println(board.getHistory());
        System.out.println(board);
        System.out.println(board.isFlipped);
        board.save(board, "save.txt");
        System.out.println(new T3().load("save.txt"));
        System.out.println(new T3().load("save.txt").isFlipped);
    }

    private T3(int[] board, int turn, T3 previous, boolean isFlipped) { // full constructor
        this.board = board;
        this.turn = turn;
        this.previous = previous;
        this.isFlipped = isFlipped;
    }

    T3() { // for initializing new Game
        this.board = new int[9];
        this.turn = +1;
        this.previous = null;
        this.isFlipped = false;
    }

    @Override
    public ImmutableBoard<Integer> makeMove(Integer move) {
        int[] newBoard = Arrays.copyOf(this.board, 9);
        newBoard[move] = turn;
        return new T3(newBoard, -turn, this, isFlipped);
    }

    @Override
    public ImmutableBoard<Integer> undoMove() {
        return previous;
    }

    @Override
    public List<Integer> moves() {
        return streamMoves()
                .boxed()
                .collect(Collectors.toList());
    }

    IntStream streamMoves() {
        return IntStream.range(0, 8).filter(i -> i == 0);
    }

    @Override
    public List<Integer> getHistory() {
        LinkedList<Integer> history = new LinkedList<>();
        Stream.iterate(this, t3 -> t3.previous != null, t3 -> t3.previous)
                .sequential()
                .map(t3 -> t3.previous.getMove(t3))
                .forEachOrdered(history::addFirst);
        return history;
    }

    @Override
    public boolean isWin() {
        final int[][] rows = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        return IntStream.range(0, 8).
                map(row -> Arrays.stream(rows[row])
                        .map(pos -> board[pos])
                        .sum()
                ).map(Math::abs)
                .filter(r -> r == 3)
                .findAny()
                .isPresent();
    }

    @Override
    public boolean isDraw() {
        return streamMoves().count() == 0;
    }

    @Override
    public ImmutableBoard<Integer> flip() {
        return new T3(Arrays.copyOf(board, 24), turn, this.previous, !isFlipped);
    }

    @Override
    public boolean isFlipped() {
        return isFlipped;
    }

    Stream<ImmutableBoard<Integer>> getChilds() {
        return IntStream.range(0, 9)
                .filter(pos -> board[pos] == 0)
                .mapToObj(this::makeMove);
        //.parallel();
    }

    int getMove(T3 child) {
        return IntStream.range(0, 9)
                .filter(pos -> board[pos] != child.board[pos])
                .findAny()
                .getAsInt();
    }

    @Override
    public String toString() {
        char[] repr = isFlipped ? new char[]{'X', '.', 'O'} : new char[]{'O', '.', 'X'};
        return IntStream.range(0, 3)
                .mapToObj(row -> IntStream.rangeClosed(row * 3, row * 3 + 2)
                        .boxed()
                        .map(n -> board[n])
                        .map(n -> repr[n + 1])
                        .map(n -> Character.toString(n))
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }

    @Override
    public T3 load(String name) {
        return load(Paths.get(name));
    }

    @Override
    public T3 load(Path path) {
        T3 load = new T3();
        try {
            LinkedList<String> moves = Files.lines(path, StandardCharsets.UTF_8)
                    .map(s -> s.split(","))
                    .flatMap(Arrays::stream)
                    .map(String::trim)
                    .collect(Collectors.toCollection(LinkedList::new));
            if (moves.getLast().toLowerCase().equals("f")) {
                moves.removeLast();
                load = (T3) load.flip();
            }
            for (Integer pos : moves.stream()
                    .map(Integer::parseInt)
                    .collect(Collectors.toList())) {
                load = (T3) load.makeMove(pos);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return load;
    }

    @Override
    public int hashCode() {
        /*for(int pos : board) {

        }*/
        return toString().hashCode();
    }

    IntStream getIDsofGroup() {
        return IntStream.of(
                hashCode(),
                rotate().hashCode(),
                rotate().rotate().hashCode(),
                rotate().rotate().rotate().hashCode(),
                mirrorTopDown().hashCode(),
                rotate().mirrorTopDown().hashCode(),
                rotate().rotate().mirrorTopDown().hashCode(),
                rotate().rotate().rotate().mirrorTopDown().hashCode()
        );
    }

    T3 rotate() {// 90Degrees left
        final int[] pattern = new int[]{
                2, 5, 8, 1, 4, 7, 0, 3, 6
        };
        int[] newBoard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        return new T3(newBoard, turn, previous, isFlipped);
    }

    T3 mirrorTopDown() {
        final int[] pattern = new int[]{
                2, 1, 0, 5, 4, 3, 8, 7, 6
        };
        int[] newBoard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        return new T3(newBoard, turn, previous, isFlipped);
    }

}
