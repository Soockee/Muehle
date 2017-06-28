import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */
public interface ImmutableBoard<Move> {
    /*
     *new interface below
     * https://moodle.thm.de/pluginfile.php/333961/mod_resource/content/1/Notizen.Woche12.html
     */

    default Optional<? extends ImmutableBoard<Move>> makeMoveNew(Move move) {// may be changed to Optional later
        return children()
                .filter(board -> board.getMove().equals(move))
                .findAny();
    }

    default Optional<? extends ImmutableBoard<Move>> makeMoveNew(Move... moves) {
        Optional<? extends ImmutableBoard<Move>> res = Optional.of(this);
        for (Move move : moves) {
            if (!res.isPresent()) return Optional.empty();
            else res = res.get().makeMoveNew(move);
        }
        return res;
    }

    Optional<Move> getMove();//returns Move from parentBoard to this instance

    ImmutableBoard<Move> parent(); // returns parent board, one Move behind

    Stream<? extends ImmutableBoard<Move>> children(); //target boards

    default Stream<? extends ImmutableBoard<Move>> history() {
        List<? extends ImmutableBoard<Move>> reversedHistory = Stream
                .iterate(this, board -> board.parent() != null, ImmutableBoard::parent)
                .collect(Collectors.toList());
        ImmutableBoard<Move>[] b = (ImmutableBoard<Move>[]) Stream
                .iterate(this, board -> board.parent() != null, ImmutableBoard::parent)
                .toArray();
        //return IntStream.rangeClosed(1, b.length).mapToObj(n -> b[b.length -n]);
        return IntStream.rangeClosed(1, reversedHistory.size()).mapToObj(n -> reversedHistory.get(reversedHistory.size() - 1));
    }//ordered from beginning to most recent Move

    /*
     * old interface below
     * https://git.thm.de/dhzb87/p20/blob/master/InterfaceBoard.md
     */

    ImmutableBoard<Move> makeMove(Move move);

    default ImmutableBoard<Move> makeMove(Move... moves) {
        ImmutableBoard<Move> b = this;
        for (Move move : moves) b = b.makeMove(move);
        return b;
    }

    List<Move> getHistory();

    List<Move> moves();

    /*
     * stuff equal in both versions below
     */

    boolean isWin();

    boolean isDraw();

    default boolean isBeginnersTurn() {
        return getHistory().size() % 2 == 0; // getHistoryNew().count() & 1 == 0
    }

    ImmutableBoard<Move> flip();

    boolean isFlipped();

    String toString();
}
