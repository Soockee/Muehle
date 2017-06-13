package Björn;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Created by xXThermalXx on 12.06.2017.
 */
public interface ImmutableBoard {

    ImmutableBoard makeMove(Move move);
    default void makeMove(Move... moves) {
        for(Move move : moves) makeMove(move);
    }
    ImmutableBoard undoMove();
    Stream<ImmutableBoard> moves();
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


}//Inteface
