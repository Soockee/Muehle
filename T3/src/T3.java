
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
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
    public Optional<ImmutableBoard<Integer>> makeMoveNew(Integer move) {
        int[] newBoard = Arrays.copyOf(this.board, 9);
        newBoard[move] = turn;
        return Optional.of(new T3(newBoard, -turn, this, isFlipped));
    }

    @Override
    public ImmutableBoard<Integer> makeMove(Integer move) {
        int[] newBoard = Arrays.copyOf(this.board, 9);
        newBoard[move] = turn;
        return new T3(newBoard, -turn, this, isFlipped);
    }

    @Override
    public ImmutableBoard<Integer> parent() {
        return previous;
    }

    IntStream streamMoves() {
        return IntStream.range(0, 9).filter(i -> board[i] == 0);
    }

    @Override
    public Stream<ImmutableBoard<Integer>> getHistoryNew() {
        T3[] historyReversed =  Stream.iterate(this, t3 -> t3.parent() != null, t3 -> t3.previous)
         .toArray(T3[]::new);
        return IntStream.rangeClosed(1,historyReversed.length)
                .mapToObj(i -> historyReversed[historyReversed.length - i]);
    }

    @Override
    public List<Integer> getHistory() {
        LinkedList<Integer> history = new LinkedList<>();
        Stream.iterate(this, t3 -> t3.previous != null, t3 -> t3.previous)
                .filter(t3 ->t3.previous != null)
                .map(T3::getMove)
                .map(Optional::get)
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

    public Stream<ImmutableBoard<Integer>> childs() {
        return IntStream.range(0, 9)
                .filter(pos -> board[pos] == 0)
                .mapToObj(this::makeMoveNew)
                .map(Optional::get);
    }

    @Override
    public List<Integer> moves() {
        return streamMoves().boxed().collect(Collectors.toList());
    }

    @Override
    public Optional<Integer> getMove() {
        if(previous == null) return Optional.empty();
        return IntStream.range(0, 9)
                .filter(pos -> board[pos] != previous.board[pos])
                .boxed()
                .findAny();
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
                if(load.streamMoves().anyMatch(i -> i == pos)) {
                    load = (T3) load.makeMove(pos);
                } else throw new IllegalArgumentException("File contains invalid Move");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return load;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof T3) {
            T3 other = (T3) obj;
            return other.hashCode() == hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getIDsofGroup().toArray());
    }

    int getID() {
        return Arrays.hashCode(board);
    }

    IntStream getIDsofGroup() {
        return IntStream.of(
                getID(),
                rotate().getID(),
                rotate().rotate().getID(),
                rotate().rotate().rotate().getID(),
                mirrorTopDown().getID(),
                rotate().mirrorTopDown().getID(),
                rotate().rotate().mirrorTopDown().getID(),
                rotate().rotate().rotate().mirrorTopDown().getID()
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