package mill;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 01.06.2017.
 */
public interface Board {

    /**
     * @param moveable needs to be a valid mill.Moveable
     * @return a new mill.Board after applying moveable
     */
    Board makeMove(Moveable moveable);

    Board makeMove(Moveable... moveable);

    Board undoMove();

    /**
     * @param child
     * @return Moveable that transforms the Argument to the Caller
     * @throws IllegalArgumentException if child has uncompatible type(no possible Moveable transforms a Mill-Board into a T3-Board)
     * or would-be-result isn't a valid move
     */
    Moveable getMove(Board child) throws IllegalArgumentException;

    int getTurn();

    int getDepth();

    Stream<Board> streamChilds();

    boolean isWin();

    /**
     * 50 Moves with out closed Mill or the same mill.Board a third Time in a Game(MIll)
     * all 9 Positions used(T3)
     * @return whether the Game ends in a draw
     */
    boolean isDraw();

    /**
     *
     * @param moveable
     * @return returns true even if moveable contains more data than needed, e.g. a that aims at removing an opponents
     *          stone without closing a mill is still valid if the rest of the move would be possible
     */
    boolean isValidMove(Moveable moveable);

    /**
     * used for finding the technical equal mill.Board
     * @return Stream of hashcodes from rotated/mirrored/swapped Versions of the original
     */
    IntStream getIDsOfGroup();

    Board save(String saveFile);

    Board save();

    Board load(String loadFile);

    Board load();
}
