
import java.io.BufferedWriter;
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
public class T3 implements StreamBoard<Integer>, SaveableGame<T3> {

    private final int[] board;
    private final int turn;
    private final T3 parent;
    private final boolean isFlipped;

    private T3(int[] board, int turn, T3 parent, boolean isFlipped) { // full constructor
        this.board = board;
        this.turn = turn;
        this.parent = parent;
        this.isFlipped = isFlipped;
    }

    T3() { // for initializing new Game
        this.board = new int[9];
        this.turn = +1;
        this.parent = null;
        this.isFlipped = false;
    }

    private T3 buildChild(Integer move) {
        int[] newBoard = Arrays.copyOf(this.board, 9);
        newBoard[move] = turn;
        return new T3(newBoard, -turn, this, isFlipped);
    }

    @Override
    public Optional<T3> makeMove(Integer move) {
        return children().filter(t3 -> t3.getMove().get().equals(move)).findAny(); // always a value present, empty boards can´t be children
    }

    @Override
    public StreamBoard<Integer> parent() {
        return parent;
    }

    IntStream streamMoves() {
        return IntStream.range(0, 9).filter(i -> board[i] == 0);
    }

    @Override
    public List<Integer> getHistory() {
        return Stream.iterate(this, t3 -> t3.parent != null, t3 -> t3.parent)
                .map(T3::getMove)
                .map(Optional::get)
                .collect(LinkedList::new, LinkedList::addFirst, LinkedList::addAll);
    }

    /*@Override
    public List<Integer> history() {
        return Stream.iterate(this, board ->  board.parent() != null, t3 -> (T3) t3.parent())
                .map(StreamBoard::getMove)
                .map(Optional::get)
                .collect(Collectors.toList());
    }//ordered from beginning to most recent Move

   */

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
    public StreamBoard<Integer> flip() {
        return new T3(Arrays.copyOf(board, 24), turn, this.parent, !isFlipped);
    }

    @Override
    public boolean isFlipped() {
        return isFlipped;
    }

    @Override
    public Stream<T3> children() {
        return IntStream.range(0, 9)
                .filter(pos -> board[pos] == 0)
                .mapToObj(this::buildChild);
    }

    @Override
    public Optional<Integer> getMove() {
        if (parent == null) return Optional.empty();
        return IntStream.range(0, 9)
                .filter(pos -> board[pos] != parent.board[pos])
                .boxed()
                .findAny();
    }

    @Override
    public String toString() {
        char[] repr = isFlipped ? new char[]{'X', '.', 'O'} : new char[]{'O', '.', 'X'};
        return IntStream.range(0, 3) // rows
                .mapToObj(row -> IntStream.rangeClosed(row * 3, row * 3 + 2)
                        .map(n -> board[n])
                        .map(n -> repr[n + 1])
                        .mapToObj(n -> Character.toString((char) n))
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }

    public void save(T3 board, String name) throws IOException {
        save(board, Paths.get(name));
    }

    public void save(T3 board, Path path) throws IOException {
        BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        out.write(board.getHistory().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","))
        );
        if (board.isFlipped()) {
            out.write(",f");
        }
        out.write("\n");
        out.close();
    }

    @Override
    public T3 load(String name) throws IOException {
        return load(Paths.get(name));
    }

    @Override
    public boolean isBeginnersTurn() {
        return turn == +1;
    }

    @Override
    public T3 load(Path path) throws IOException {
        T3 load = new T3();
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
            load = load.makeMove(pos).orElseThrow(() -> new IOException("File contains invalid Moves"));
            /*if (load.isValidMove(pos)) {
                load = load.buildChild(pos).get();
            } else throw new IOException("File contains invalid Moves");*/
        }
        return load;
    }

    private boolean isValidMove(Integer pos) {
        return streamMoves().anyMatch(move -> move == pos);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof T3 && getGroup().anyMatch(t3 -> Arrays.equals(t3.board, ((T3) obj).board));
    }

    @Override
    public int hashCode() {
        return getGroup().mapToInt(T3::getID).reduce((i1, i2) -> i1 ^ i2).getAsInt();
    }

    private int getID() {
        return Arrays.hashCode(board);
    }

    private Stream<T3> getGroup() {
        return Stream.of(
                this,
                rotate(),
                rotate().rotate(),
                rotate().rotate().rotate(),
                mirrorTopDown(),
                rotate().mirrorTopDown(),
                rotate().rotate().mirrorTopDown(),
                rotate().rotate().rotate().mirrorTopDown()
        );
    }

    private T3 rotate() {// 90Degrees left
        final int[] pattern = {
                2, 5, 8, 1, 4, 7, 0, 3, 6
        };
        int[] newBoard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        return new T3(newBoard, turn, parent, isFlipped);
    }

    private T3 mirrorTopDown() {
        final int[] pattern = {
                2, 1, 0, 5, 4, 3, 8, 7, 6
        };
        int[] newBoard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        return new T3(newBoard, turn, parent, isFlipped);
    }
}