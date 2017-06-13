package mill.src;
/**
*   Edit 11.06 by Simon
 */
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 01.06.2017.
 */
public interface Board {

    Board makeMove(Movable moveable);

    Board makeMove(Movable... moveable);

    Board undoMove();

    Movable getMove(Board child) throws IllegalArgumentException;

    int getTurn();

    int getDepth();

    Stream<Board> streamChilds();

    boolean isWin();

    boolean isDraw();

    boolean isValidMove(Movable moveable);

    IntStream getIDsOfGroup();

    Board save(String saveFile);

    Board save();

    Board load(String loadFile);

    Board load();
}

