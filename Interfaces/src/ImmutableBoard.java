import java.util.List;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */
public interface ImmutableBoard<Move> {

    ImmutableBoard<Move> makeMove(Move move);

    default ImmutableBoard<Move> makeMove(Move... moves) {
        ImmutableBoard<Move> res = this;
        for(Move move : moves) res = res.makeMove(move);
        return res;
    }

    ImmutableBoard<Move> undoMove();

    List<Move> moves();
    List<Move> getHistory();

    boolean isWin();
    boolean isDraw();

    default boolean isBeginnersTurn() {
        return getHistory().size() % 2 == 0;
    }

    ImmutableBoard<Move> flip();
    boolean isFlipped();
    String toString();
}
