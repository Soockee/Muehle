package Muehle;

import java.nio.file.Path;
import java.util.List;

/**
 * Created by Simon on 09.06.2017.
 */
@SuppressWarnings("unchecked")

public interface MutableBoard<Move> {

    void makeMove(Move move);

    default void makeMove(Move... moves) {

        for(Move move : moves) makeMove(move);

    }

    void undoMove();

    List<Move> moves();

    List<Move> getHistory();


    boolean isWin();

    boolean isDraw();

    default boolean isBeginnersTurn() {

        return getHistory().size() % 2 == 0;

    }


    void flip();

    boolean isFlipped();

    String toString();


    void load(String name);

    void load(Path path);

    void save(String name);

    void save(Path path);

}
