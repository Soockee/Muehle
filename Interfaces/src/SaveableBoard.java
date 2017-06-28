import java.util.List;

/**
 * Created by Paul Krappatsch on 28.06.2017.
 */

public interface SaveableBoard<Move> {
    List<Move> getHistory(); // last move in list = recent move
    boolean isFlipped();
}

