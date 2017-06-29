import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */

public interface StreamBoard<Move> extends SaveableBoard<Move> {

    default Optional<? extends StreamBoard<Move>> makeMove(Move move) {// may be changed to Optional later
        return children()
                .filter(board -> board.getMove().equals(move))
                .findAny();
    }

    default Optional<? extends StreamBoard<Move>> makeMove(Move... moves) {
        Optional<? extends StreamBoard<Move>> res = Optional.of(this);
        for (Move move : moves) {
            if (!res.isPresent()) return Optional.empty();
            else res = res.get().makeMove(move);
        }
        return res;
    }

    Optional<Move> getMove();//returns Move from parentBoard to this instance

    StreamBoard<Move> parent(); // returns parent board, one Move behind

    Stream<? extends StreamBoard<Move>> children(); //target boards

    boolean isWin();

    boolean isDraw();

    default boolean isBeginnersTurn() {
        return getHistory().size() % 2 == 0; // getHistoryNew().count() & 1 == 0
    }

    StreamBoard<Move> flip();

    boolean isFlipped();

    String toString();
}
