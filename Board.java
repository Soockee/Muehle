package UnfinishedVersions.V0_1;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.IntStream;

/**
 * Created by Simon on 05.06.2017.
 */
public class Board implements Boardable{

    private final int size = 24;
    private final int[] pattern = {
            0b000_000_000_000_000_000_000_001,
            0b000_000_000_000_000_000_000_010,
            0b000_000_000_000_000_000_000_100,

            0b000_000_000_000_000_000_001_000,
            0b000_000_000_000_000_000_010_000,
            0b000_000_000_000_000_000_100_000,

            0b000_000_000_000_000_001_000_000,
            0b000_000_000_000_000_010_000_000,
            0b000_000_000_000_000_100_000_000,

            0b000_000_000_000_001_000_000_000,
            0b000_000_000_000_010_000_000_000,
            0b000_000_000_000_100_000_000_000,

            0b000_000_000_001_000_000_000_000,
            0b000_000_000_010_000_000_000_000,
            0b000_000_000_100_000_000_000_000,

            0b000_000_001_000_000_000_000_000,
            0b000_000_010_000_000_000_000_000,
            0b000_000_100_000_000_000_000_000,

            0b000_001_000_000_000_000_000_000,
            0b000_010_000_000_000_000_000_000,
            0b000_100_000_000_000_000_000_000,

            0b001_000_000_000_000_000_000_000,
            0b010_000_000_000_000_000_000_000,
            0b100_000_000_000_000_000_000_000,

    };
    private int[] boards;

    public Board(){
        boards = new int[2];
        boards[0] = 0b000_000_000_000_000_000_000_000;
        boards[1] = 0b000_000_000_000_000_000_000_000;
    }

    public Board(Board parent, int turn, int move){
        boards = new int[2];
        boards = Arrays.copyOf(parent.getBoards(), size);
        setStone(move, turn);
    }

    public List<Board> children(int turn) {
        IntStream emptyFields =  IntStream.range(0, 23)
                                .filter(i -> (((~(boards[0]) | (boards[1]) >> i) & 1) == 1));
        ArrayList<Board> children = new ArrayList<>();
        emptyFields.forEach( i -> children.add(new Board(this, turn, i)));
        return children;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public int[] getBoards() {
        return boards;
    }
    public void setStone(int move, int turn){
        boards[turn] |= pattern[move];
    }
}
