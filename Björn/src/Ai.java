package Björn.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by xXThermalXx on 13.06.2017.
 * - Der Code muss noch in die andere Ai Klasse eingefügt werden
 */
public class Ai {

    private ConcurrentHashMap<Integer, TableEntry> ttable = new ConcurrentHashMap<>();

    public int alphaBeta(ImmutableBoard board, int depth, int alpha, int beta) {
        int alphaStart = alpha;

        //wennn der Wert schon im hashTable vorliegt
        TableEntry te = ttable.get(board.hashCode());
        if (te != null) {
            if (te.getFlag() == 0) {
                return te.getValue();
            } else if (te.getFlag() == 1) {
                alpha = te.getValue() > alpha ? te.getValue() : alpha;
            } else if (te.getFlag() == 2) {
                beta = te.getValue() < beta ? te.getValue() : beta;
            }
            if (alpha >= beta) {
                return te.getValue();
            }
        }//if TabelEntry exists


        //Evaluierung der Blätter,
        if (board.isWin()) {
            int val = -1000 + board.getHistory().size();
            addDoubles(val, ttable, board);
            return val;
        }//Gewinnfall
        if (board.isDraw()) {
            int val = 0;
            addDoubles(0, ttable, board);
            return val;
        }//Unentschieden
        if (depth == 0) {
            int val = evaluateBoard(board);
            addDoubles(val, ttable, board);
            return val;
        }//gewünschte Tiefe wurde erreicht

        int bestVal = Integer.MIN_VALUE;
        List<ImmutableBoard> listOfMoves = board.moves();
        for (ImmutableBoard entry : listOfMoves) {
            int val = -alphaBeta(entry, depth - 1, -beta, -alpha);
            bestVal = bestVal > val ? bestVal : val;
            alpha = alpha > val ? alpha : val;
            if (alpha >= beta) break;
        }//for


        if (bestVal <= alphaStart) {
            ttable.put(board.hashCode(), new TableEntry(bestVal, 2));
        } else if (bestVal >= beta) {
            ttable.put(board.hashCode(), new TableEntry(bestVal, 1));
        } else {
            ttable.put(board.hashCode(), new TableEntry(bestVal, 0));
        }

        return bestVal;
    }//alphaBeta

    public void addDoubles(int value, ConcurrentHashMap<Integer, TableEntry> ttable, ImmutableBoard board) {

    }

    public int playRandomly(ImmutableBoard board) {
        Random r = ThreadLocalRandom.current();
        while (!board.isDraw()) {
            if (board.isWin()) {
                return (board.getHistory().size() % 2 == 0) ? 1 : -1;
            }
            List<Movable> listOfMoves = board.moves();
            Movable nextMove = listOfMoves.get(r.nextInt(listOfMoves.size()));
            board = board.makeMove(nextMove);
        }
        return 0;
    }//playRandom

    public int[] simulatePlays(ImmutableBoard board, int number) {
        int[] ints = IntStream
                .range(0, number)
                .parallel()
                .map(i -> playRandomly(board))
                .toArray();
        int wins = (int) Arrays.stream(ints).filter(i -> i == 1).count();
        int losses = (int) Arrays.stream(ints).filter(i -> i == -1).count();
        return new int[]{wins, losses};
    }


    int evaluateBoard(ImmutableBoard board) {
        return 0;
    }


}//class
