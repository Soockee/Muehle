import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */

public interface StreamBoard<Move> {

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

    default List<Move> history() {
        List<Move> revHistory =  Stream.iterate(this, board -> board.parent() != null, StreamBoard::parent)
                .map(StreamBoard::getMove)
                .map(Optional::get)
                .collect(Collectors.toList());
        return IntStream.rangeClosed(1, revHistory.size())
                .mapToObj(value -> revHistory.get(revHistory.size() - value))
                .collect(Collectors.toList());
    }//ordered from beginning to most recent Move

    boolean isWin();

    boolean isDraw();

    default boolean isBeginnersTurn() {
        return history().size() % 2 == 0; // getHistoryNew().count() & 1 == 0
    }

    StreamBoard<Move> flip();

    boolean isFlipped();

    String toString();
}
