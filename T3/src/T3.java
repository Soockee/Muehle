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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 13.06.2017.
 */
public class T3 implements ImmutableBoard<Integer> {

    int[] board = new int[9];
    int turn = +1;
    T3 parent = null;
    boolean isFlipped = false;

    public static void main(String[] args) {
        ImmutableBoard<Integer> board = new T3();
        board = board.makeMove(8, 5, 1);
        System.out.println(board.getHistory());
        System.out.println(board);
        board = board.flip();
        board.save("save.txt");
        System.out.println(board);
        System.out.println(new T3().load("save.txt"));
        System.out.println(new T3().load("save.txt").isFlipped());
    }


    @Override
    public ImmutableBoard<Integer> makeMove(Integer move) {
        T3 child = new T3();
        child.board = Arrays.copyOf(board, 9);
        child.board[move] = turn;
        child.turn = -turn;
        child.parent = this;
        child.isFlipped = isFlipped;
        return child;
    }

    @Override
    public ImmutableBoard<Integer> undoMove() {
        return parent;
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
        LinkedList<Integer> history = new LinkedList<Integer>();
        T3 tmp = this;
        while (tmp.parent != null) {
            history.addFirst(tmp.parent.getMove(tmp));
            tmp = tmp.parent;
        }
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
        T3 res = new T3();
        res.board = Arrays.copyOf(board, 24);
        res.parent = parent;
        res.isFlipped = !isFlipped;
        return res;
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
    public ImmutableBoard<Integer> load(String name) {
        return load(Paths.get(name));
    }

    @Override
    public ImmutableBoard<Integer> load(Path path) {
        ImmutableBoard<Integer> load = new T3();
        Pattern validMoves = Pattern.compile("\\s*(?:->)?\\s*(\\d)\\s*");
        try {
            int skip;
            Matcher firstLine = Files.lines(path, StandardCharsets.UTF_8)
                    .filter(s -> !s.isEmpty())
                    .findFirst()
                    .map(s -> Pattern.compile("\\s*((?:X\\s*vs.\\s*O)|(?:O\\s*vs.\\s*X))\\s*").matcher(s))
                    .get();
            if (firstLine.matches()) {
                if (firstLine.group(1).matches("O\\s*vs.\\s*X")) {
                    load = load.flip();
                }
                skip = 1;
            } else skip = 0;
            int[] moves = Files.lines(path, StandardCharsets.UTF_8)
                    .filter(s -> !s.isEmpty())
                    .skip(skip)
                    .map(validMoves::matcher)
                    .map(matcher -> {
                        if (matcher.matches())
                            return Integer.parseInt(matcher.group(1));
                        else throw new IllegalArgumentException("File was corrupted");
                    })
                    .mapToInt(Integer::intValue)
                    .toArray();
            for (int move : moves) {
                load = load.makeMove(move);
            }
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return load;
    }

    @Override
    public ImmutableBoard<Integer> save(String name) {
        return save(Paths.get(name));
    }

    @Override
    public ImmutableBoard<Integer> save(Path path) {
        try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            out.write(isFlipped ? "O vs X" : "X vs O");
            out.newLine();
            for (Integer move : getHistory()) {
                out.write("-> " + (move + 1) + "\n");
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return this;
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
        int[] newboard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        T3 clone = new T3();
        clone.board = newboard;
        clone.parent = parent;
        clone.turn = turn;
        clone.isFlipped = isFlipped;
        return clone;
    }

    T3 mirrorTopDown() {
        final int[] pattern = new int[]{
                2, 1, 0, 5, 4, 3, 8, 7, 6
        };
        int[] newboard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        T3 clone = new T3();
        clone.board = newboard;
        clone.parent = parent;
        clone.turn = turn;
        clone.isFlipped = isFlipped;
        return clone;
    }
}
