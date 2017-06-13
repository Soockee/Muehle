package mill;

import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 03.06.2017.
 */
public class BitMill implements Board {

    final static int[] bitpattern = {
            0b0000_0000_0000_0000_0000_0001,
            0b0000_0000_0000_0000_0000_0010,
            0b0000_0000_0000_0000_0000_0100,
            0b0000_0000_0000_0000_0000_1000,
            0b0000_0000_0000_0000_0001_0000,
            0b0000_0000_0000_0000_0010_0000,
            0b0000_0000_0000_0000_0100_0000,
            0b0000_0000_0000_0000_1000_0000,
            0b0000_0000_0000_0001_0000_0000,
            0b0000_0000_0000_0010_0000_0000,
            0b0000_0000_0000_0100_0000_0000,
            0b0000_0000_0000_1000_0000_0000,
            0b0000_0000_0001_0000_0000_0000,
            0b0000_0000_0010_0000_0000_0000,
            0b0000_0000_0100_0000_0000_0000,
            0b0000_0000_1000_0000_0000_0000,
            0b0000_0001_0000_0000_0000_0000,
            0b0000_0010_0000_0000_0000_0000,
            0b0000_0100_0000_0000_0000_0000,
            0b0000_1000_0000_0000_0000_0000,
            0b0001_0000_0000_0000_0000_0000,
            0b0010_0000_0000_0000_0000_0000,
            0b0100_0000_0000_0000_0000_0000,
            0b1000_0000_0000_0000_0000_0000,
    };
    //23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00
    //   23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 >> 1
    //      23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 >> 1
    //          1  0  1  0  1  0  0  0  1  0  1  0  1  0  0  0  1  0  1  0  1

    //                     23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07  >> 7
    //                  23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06  >> 6
    //23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00
    //                      1  0  0  0  0  0  0  0  1  0  0  0  0  0  0  0  1

    //                                                23 22 21 20 19 18 17 16 >> 16
    //                        23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 >> 8
    //23 22 21 20 19 18 17 16 15 14 13 12 11 10 09 08 07 06 05 04 03 02 01 00
    //                                                 1  0  1  0  1  0  1  0
    int[] bitFilter = {
            0b0001_0101_0001_0101_0001_0101,
            0b1000_0000_1000_0000_1,
            0b1010_1010
    };
    int[] board = {0, 0};
    int turn = +1; // 0 or 1
    int depth = 0;
    BitMill parent = null;
    int[] stones = new int[]{0, 0};

    @Override
    public Board makeMove(Moveable moveable) {
        BitMill child = new BitMill();
        if (depth < 18) {
            child.turn = -turn;
            child.depth = depth + 1;
            child.parent = this;
            child.stones[turn] += 1;
            child.board[turn] ^= 1 << moveable.getTo();
        } else {
            child.turn = -turn;
            child.depth = depth + 1;
            child.parent = this;
            child.stones[turn] += 1;
            child.board[turn] ^= 1 << moveable.getTo();
            child.board[turn] ^= 1 << moveable.getFrom();
        }
        if (moveable.getRemove() != -1) {
            child.board[child.turn] ^= moveable.getRemove();
        }
        return child;
    }

    @Override
    public Board makeMove(Moveable... moveable) {
        Board res = this;
        for (Moveable move : moveable) {
            res = res.makeMove(move);
        }
        return res;
    }

    @Override
    public Board undoMove() {
        return parent;
    }

    @Override
    public Moveable getMove(Board child) throws IllegalArgumentException {
        return null;
    }

    @Override
    public int getTurn() {
        return turn;
    }

    @Override
    public int getDepth() {
        return depth;
    }

    @Override
    public Stream<Board> streamChilds() {
        return null;
    }

    @Override
    public boolean isWin() {
        int bits = board[(turn + 1) % 2];
        return (bits | bits >> 1 | bits >> 2 | bitpattern[0]) != 0
                || (bits | bits >> 7 | bits >> 6 | bitpattern[1]) != 0
                || (bits | bits >> 16 | bits >> 8 | bitpattern[2]) != 0;
    }

    long numberOfClosedMill(BitMill child, int lastPos) {
        final int[][][] mills = {
                {{0, 1, 2,}, {0, 6, 7}},
                {{0, 1, 2}, {1, 9, 17}},
                {{0, 1, 2}, {2, 3, 4}},
                {{2, 3, 4}, {19, 11, 3}},
                {{2, 3, 4}, {6, 5, 4}},
                {{6, 5, 4}, {21, 13, 5}},
                {{6, 5, 4}, {0, 6, 7}},
                {{0, 7, 6}, {7, 15, 23}},

                {{8, 9, 10}, {14, 15, 8}},
                {{8, 9, 10}, {1, 9, 17}},
                {{8, 9, 10}, {10, 11, 12}},
                {{10, 11, 12}, {19, 11, 3}},
                {{10, 11, 12}, {14, 13, 12}},
                {{14, 13, 12}, {21, 13, 5}},
                {{14, 13, 12}, {14, 15, 8}},
                {{14, 15, 8}, {7, 15, 23}},

                {{16, 17, 18}, {16, 23, 22}},
                {{16, 17, 18}, {1, 9, 10}},
                {{16, 17, 18}, {18, 19, 20}},
                {{18, 19, 20}, {19, 11, 3}},
                {{18, 19, 20}, {22, 21, 20}},
                {{22, 21, 20}, {21, 13, 22}},
                {{22, 21, 20}, {16, 23, 22}},
                {{16, 23, 22}, {7, 15, 23}}
        };
        return 0L;
    }

    @Override
    public boolean isDraw() {
        return false;
    }

    @Override
    public boolean isValidMove(Moveable moveable) {
        return false;
    }

    @Override
    public IntStream getIDsOfGroup() {
        return null;
    }

    @Override
    public Board save(String saveFile) {
        return null;
    }

    @Override
    public Board save() {
        return null;
    }

    @Override
    public Board load(String loadFile) {
        return null;
    }

    @Override
    public Board load() {
        return null;
    }

    public Board copy() {
        return null;
    }
}
