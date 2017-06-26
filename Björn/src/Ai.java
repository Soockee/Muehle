package Björn.src;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by xXThermalXx on 13.06.2017.
 */
public class Ai {

    private ConcurrentHashMap<ImmutableBoard, TableEntry> ttable = new ConcurrentHashMap<>();
    private HashMap<Integer, ImmutableBoard> bestMoves = new HashMap<>();

    public void evaluateBestBoard(ImmutableBoard board, int depth, int seconds) {
        long end = System.currentTimeMillis() + (seconds*1000);
        IntStream
                .range(0, depth)
                .forEach(i -> {
                    while (System.currentTimeMillis()<end){
                        bestMoves.put(i,iterativeDepthSearch(board,i));
                    }//while the algorithm has time to calculate
                });
    }

    public ImmutableBoard iterativeDepthSearch(ImmutableBoard board, int depth)  {
        ImmutableBoard bestBoard = null;
        try {
            bestBoard = (ImmutableBoard) board
                    .childs()
                    .max(Comparator.comparingInt(item -> -alphaBeta((ImmutableBoard) item, depth, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                    .orElseThrow(Error::new);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return bestBoard;
    }//evaluateAlphaBeta

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
            }
            alpha = alpha > val ? alpha : val;
            if (alpha >= beta) break;
        }//for


        if (bestVal <= alphaStart) {
            ttable.put(board, new TableEntry(bestVal, 2));
        } else if (bestVal >= beta) {
            ttable.put(board, new TableEntry(bestVal, 1));
        } else {
            ttable.put(board, new TableEntry(bestVal, 0));
        }

        return bestVal;
    }//alphaBeta

    public int playRandomly(ImmutableBoard board, boolean turn) {
        if (board.isWin()) {
            return (board.isBeginnersTurn() == turn) ? 1 : -1;
        }
        Random r = ThreadLocalRandom.current();
        while (!board.isDraw()) {
            List<ImmutableBoard> container = (List<ImmutableBoard>) board.childs().collect(Collectors.toList());
            board = container.get(r.nextInt(container.size()));
            if (board.isWin()) {
                return (board.isBeginnersTurn() == turn) ? 1 : -1;
            }
        }
        return 0;
    }//playRandomly

    public int[] simulatePlays(ImmutableBoard board, int number) {
        return IntStream
                .range(0, number)
                .parallel()
                .map(i -> playRandomly(board, board.isBeginnersTurn()))
                .collect(
                        () -> new int[3],
                        (int[] r, int i) -> r[i + 1] += 1,
                        (int[] ints, int[] ints2) -> {
                            ints[0] = ints2[0] = ints[0] + ints2[0];
                            ints[1] = ints2[1] = ints[1] + ints2[1];
                            ints[2] = ints2[2] = ints[2] + ints2[2];
                        }
                );
    }//simulatePlays


    public int evaluateBoard(ImmutableBoard board) {
        int[] val = simulatePlays(board, 10);
        return val[2] - val[0];
    }//evaluateBoard

    public HashMap<Integer, ImmutableBoard> getBestMoves(){
        return bestMoves;
    }
}//class
