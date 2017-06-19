import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */
public interface ImmutableBoard<Move> {

    default Optional<ImmutableBoard<Move>> makeMoveNew(Move move) {
        return childs()
                .filter(moveImmutableBoard -> moveImmutableBoard.getMove().equals(move))
                .findAny();
    }
    default Optional<ImmutableBoard<Move>> makeMoveNew(Move... moves) {
        ImmutableBoard<Move> res = this;
        for(Move move : moves) {
            res = this.makeMoveNew(move).get();
        }
        return Optional.of(res);
    }
    Optional<Move>  getMove();
    ImmutableBoard<Move> parent();
    Stream<ImmutableBoard<Move>> childs();
    Stream<ImmutableBoard<Move>> getHistoryNew();


    ImmutableBoard<Move> makeMove(Move move);
    default ImmutableBoard<Move> makeMove(Move... moves) {
        ImmutableBoard<Move> b = this;
        for(Move move : moves) b = b.makeMove(move);
        return b;
    }
    List<Move> getHistory();
    List<Move> moves();

    boolean isWin();
    boolean isDraw();
    default boolean isBeginnersTurn() {
        return getHistory().size() % 2 == 0; // getHistoryNew().count() & 1 == 0
    }
    ImmutableBoard<Move> flip();
    boolean isFlipped();
    String toString();
}
