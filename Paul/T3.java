package mill;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 02.06.2017.
 */
public class T3 implements Board {

    int[] board = new int[9];
    int turn = +1;
    int depth = 0;
    T3 parent = null;

    @Override
    public Board makeMove(Moveable moveable) {
        T3 child = (T3) copy();
        child.board[moveable.getTo()] = turn;
        child.turn = -turn;
        child.depth = depth + 1;
        child.parent = this;
        return child;
    }

    @Override
    public Board makeMove(Moveable... moveable) {
        T3 res = (T3) copy();
        for (Moveable move : moveable) {
            res = (T3) res.makeMove(move);
        }
        return res;
    }

    @Override
    public Board undoMove() {
        return parent;
    }

    @Override
    public Moveable getMove(Board child) throws IllegalArgumentException {
        if (!(child instanceof T3)) throw new IllegalArgumentException("input has to be of Type T3");
        Moveable res = new Move();
        T3 t3 = (T3) child;
        res.setTo(IntStream.range(0, 9)
                .filter(i -> board[i] == 0)
                .filter(i -> t3.board[i] != 0)
                .findAny()
                .orElseThrow(IllegalArgumentException::new)
        );
        return res;
    }

    @Override
    public int getTurn() {
        return turn;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public Stream<Board> streamChilds() {
        return IntStream.range(0, 9)
                .parallel()
                .filter(pos -> board[pos] == 0)
                .mapToObj(Move::new)
                .map(this::makeMove);
        //.parallel();
    }

    @Override
    public boolean isWin() {
        final int[][] rows = {
                {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
                {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
                {0, 4, 8}, {2, 4, 6}
        };
        return depth >= 5 && IntStream.range(0, 8)
                .map(row -> Arrays.stream(rows[row])
                        .map(pos -> board[pos])
                        .sum()
                )
                .map(Math::abs)
                .filter(r -> r == 3)
                .findAny()
                .isPresent();
    }

    @Override
    public boolean isDraw() {
        return depth == 9;
    }

    @Override
    public boolean isValidMove(Moveable moveable) {
        return moveable.getTo() < 9
                && moveable.getTo() >= 0
                && board[moveable.getTo()] == 0
                && moveable.getFrom() == -1
                && moveable.getRemove() == -1;
    }

    T3 rotate() {// 90Degrees left
        final int[] pattern = new int[]{
                2, 5, 8, 1, 4, 7, 0, 3, 6
        };
        int[] newboard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        T3 clone = (T3) copy();
        clone.board = newboard;
        return clone;
    }

    T3 mirrorTopDown() {
        final int[] pattern = new int[]{
                2, 1, 0, 5, 4, 3, 8, 7, 6
        };
        int[] newboard = IntStream.range(0, 9).map(i -> board[pattern[i]]).toArray();
        T3 clone = (T3) copy();
        clone.board = newboard;
        return clone;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public IntStream getIDsOfGroup() {
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

    public Board copy() {
        T3 copy = new T3();
        copy.board = Arrays.copyOf(board, 9);
        copy.depth = depth;
        copy.parent = parent;
        copy.turn = turn;
        return copy;
    }

    @Override
    public Board save(String saveFile) {
        T3 child = this;
        try (BufferedWriter out = Files.newBufferedWriter(Paths.get(saveFile), StandardCharsets.UTF_8)) {
            StringBuilder stringBuilder = new StringBuilder();
            while (child.parent != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(child.parent.getMove(child).getTo());
                sb.append(";");
                child = child.parent;
                stringBuilder = sb.append(stringBuilder);
            }
            out.write(stringBuilder.toString());
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
        Pattern pattern = Pattern.compile("\\s*->\\s*(\\d)\\s*");
        T3 load = new T3();
        try {
            List<Moveable> lst = Files.lines(Paths.get(loadFile), StandardCharsets.UTF_8)
                    .filter(s -> !s.equals(""))
                    .map(pattern::matcher)
                    .map(matcher -> {
                        if (!matcher.matches()) throw new IllegalArgumentException("File was corrupted");
                        return new Move(Integer.parseInt(matcher.group(1)));
                    })
                    .collect(Collectors.toList());
            for (Moveable moveable : lst) {
                if (!(load.isValidMove(moveable))) throw new IllegalArgumentException("File was corrupted");
                load = (T3) load.makeMove(moveable);
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

    @Override
    public String toString() {
        char[] repr = {'O', '.', 'X'};
        return IntStream.rangeClosed(0, 2)
                .mapToObj(row -> IntStream.rangeClosed(row * 3, row * 3 + 2)
                        .boxed()
                        .map(n -> board[n])
                        .map(n -> repr[n + 1])
                        .map(n -> Character.toString(n))
                        .collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
    }

    List<Moveable> getHistory() {
        LinkedList<Moveable> lst = new LinkedList<>();
        T3 tmp = (T3) copy();
        while (tmp.parent != null) {
            lst.addFirst(tmp.parent.getMove(tmp));
        }
        return lst;
    }
}