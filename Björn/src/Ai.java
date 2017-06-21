package Björn.src;

import java.util.*;
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
    private ImmutableBoard bestMove = null;
    private int startDepth = 0;


    //evaluiert den besten Wert für ein übergebenesBoard mit dem AlphaBetaAlgorithmus
    public ImmutableBoard evaluateAlphaBeta(ImmutableBoard board, int depth) {
        startDepth = board.getHistory().size();
        alphaBeta(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMove;
    }

    public int alphaBetaAsStream() {
        return 0;
    }

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
            return val;
        }//Gewinnfall
        if (board.isDraw()) {
            int val = 0;
            return val;
        }//Unentschieden
        if (depth == 0) {
            int val = evaluateBoard(board);
            return val;
        }//gewünschte Tiefe wurde erreicht

        int bestVal = Integer.MIN_VALUE;
        List<ImmutableBoard> listOfMoves = (List<ImmutableBoard>) board.childs().collect(Collectors.toList());
        for (ImmutableBoard entry : listOfMoves) {
            board = entry;
            int val = -alphaBeta(board, depth - 1, -beta, -alpha);
            board = board.parent();
            if (val > bestVal) {
                bestVal = val;
                bestMove = board;
            }
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

    public int playRandomly(ImmutableBoard board, boolean turn) {
        Random r = ThreadLocalRandom.current();
        while (!board.isDraw()) {
            if (board.isWin()) {
                return (board.isBeginnersTurn() == turn) ? 1 : -1;
            }
            List<ImmutableBoard> container = (List<ImmutableBoard>) board.childs().collect(Collectors.toList());
            board = container.get(r.nextInt(container.size()));

        }
        return 0;
    }//playRandomly

    public int[] simulatePlays(ImmutableBoard board, int number) {
        int[] ints = IntStream
                .range(0, number)
                .parallel()
                .map(i -> playRandomly(board, board.isBeginnersTurn()))
                .toArray();
        int wins = (int) Arrays.stream(ints)
                .filter(i -> i == 1)
                .count();
        int losses = (int) Arrays.stream(ints)
                .filter(i -> i == -1)
                .count();
        return new int[]{wins, losses};
    }//simulatePlays


    public int evaluateBoard(ImmutableBoard board) {
        int bestVal = 0;
        List<ImmutableBoard> listMoves = (List<ImmutableBoard>) board.childs().collect(Collectors.toList());
        OptionalInt k=listMoves
                .stream()
                .mapToInt(nextBoard -> {
                    int[] con = simulatePlays(board, 5);
                    return con[0]-con[1];
                })
                .max();
        return k.getAsInt();
    }//evaluateBoard

    /*
    for (ImmutableBoard nextBoard : listMoves) {
            board = nextBoard;
            int[] con = simulatePlays(board, 5);
            if ((con[0] - con[1]) > bestVal) {
                bestVal = (con[0] - con[1]);
            }
            board = board.parent();
        }

     */
}//class
