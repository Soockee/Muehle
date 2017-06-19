package Björn.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by xXThermalXx on 13.06.2017.
 * - Der Code muss noch in die andere Ai Klasse eingefügt werden
 */
public class Ai <Move>{

    private ConcurrentHashMap<Integer, TableEntry> ttable = new ConcurrentHashMap<>();
    private ImmutableBoard<Move> bestMove=null;
    private int startDepth=0;


    //evaluiert den besten Wert für ein übergebenesBoard mit dem AlphaBetaAlgorithmus
    public ImmutableBoard evaluateAlphaBeta(ImmutableBoard board, int depth){
        startDepth=board.getHistory().size();
        alphaBeta(board, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
        return bestMove;
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
            //int val = evaluateBoard(board);
            return -3 ;
        }//gewünschte Tiefe wurde erreicht

        int bestVal = Integer.MIN_VALUE;
        List<Move> listOfMoves = board.moves();
        for (Move entry : listOfMoves) {
            board=board.makeMove(entry);
            int val = -alphaBeta(board, depth - 1, -beta, -alpha);
            System.out.println(val+" >"+bestVal);
            if(val>bestVal){
                bestVal=val;
                if(depth==(startDepth-1)){
                    System.out.println("Hallo");
                    bestMove=board;
                }
            }
            board=board.undoMove();
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
            try {
                if (board.isWin()) {
                    return (board.isBeginnersTurn() ) ? 1 : -1;
                }
                board = board.makeMove(board.moves().get(r.nextInt(board.moves().size())));
            }
            catch (Exception e){
                System.out.println(board.toString());
                return -4;
            }
        }
        return 0;
    }//playRandom

    public int[] simulatePlays(ImmutableBoard board, int number) {
        int[] ints = IntStream
                .range(0, number)
                .parallel()
                .map(i -> playRandomly(board))
                .toArray();
        int wins = (int) Arrays.stream(ints)
                .filter(i -> i == 1)
                .count();
        int losses = (int) Arrays.stream(ints)
                .filter(i -> i == -1)
                .count();
        return new int[]{wins, losses};
    }


    public int evaluateBoard(ImmutableBoard board) {
        int bestVal=0;
        List<Move> listMoves=board.moves();
        for (Move nextMove: listMoves){
            board=board.makeMove(nextMove);
            int[] con=simulatePlays(board,10);
            if((con[0]-con[1])>bestVal){
                bestVal =(con[0]-con[1]);
            }
            board=board.undoMove();
        }
        return bestVal;
    }

}//class
