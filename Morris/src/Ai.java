import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by xXThermalXx on 13.06.2017.
 */
public class Ai {

    private ConcurrentHashMap<StreamBoard, TableEntry> ttable;
    private TreeMap<Integer, List<StreamBoard>> heuristic;
    private StreamBoard bestMove;
    private int playCount;

    public Ai(int playCount) {
        this.playCount = playCount;
        ttable = new ConcurrentHashMap<>();
        heuristic = null;
        bestMove = null;
    }

    public void evaluateBestBoard(StreamBoard board, int depth) {
        heuristic = null;
        IntStream
                .range(0, depth)
                .forEach(i -> {
                    bestMove = altIterativeDepthSearch (board, i);
                });

    }//evaluateBestBoard


    public StreamBoard iterativeDepthSearch(StreamBoard board, int depth) {
        ArrayList<StreamBoard> list;

        if (heuristic == null) {
            list = (ArrayList<StreamBoard>) board.children().collect(Collectors.toList());
        } else {
            list = getBoards(heuristic);
        }
        heuristic = new TreeMap<>();
        for (StreamBoard elem : list) {
            int val = -alphaBeta(elem, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);

            ArrayList<StreamBoard> con = (ArrayList<StreamBoard>) heuristic.get(val);
            if (con == null) con = new ArrayList<>();
            con.add(elem);

            heuristic.put(val, con);
        }//for

        return heuristic.get(heuristic.firstKey()).get(0);
    }//new iterativDepthSearch-Method

    public ArrayList<StreamBoard> getBoards(TreeMap<Integer, List<StreamBoard>> map) {

        ArrayList<StreamBoard> list = new ArrayList<>();

        for (Integer idx : map.keySet()) {
            list.addAll(map.get(idx));
        }

        return list;
    }

    public StreamBoard altIterativeDepthSearch(StreamBoard board, int depth) {
        StreamBoard bestBoard = null;
        ttable=new ConcurrentHashMap<>();
        try {
            bestBoard = (StreamBoard) board
                    .children()
                    .max(Comparator.comparingInt(item -> -alphaBeta((StreamBoard) item, depth, Integer.MIN_VALUE, Integer.MAX_VALUE)))
                    .orElseThrow(Error::new);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return bestBoard;
    }//old iterativDepthSearch-Method


    public int alphaBeta(StreamBoard board, int depth, int alpha, int beta) {
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
            int val = -1000 + depth;
            addToTable(board,val,alphaStart,beta);
            return val;
        }//Gewinnfall
        if (board.isDraw()) {
            int val = 0;
            addToTable(board,val,alphaStart,beta);
            return val;
        }//Unentschieden
        if (depth == 0) {
            int val = evaluateBoard(board);
            addToTable(board,val,alphaStart,beta);
            return val;
        }//gewünschte Tiefe wurde erreicht

        int bestVal = Integer.MIN_VALUE;
        List<StreamBoard> listOfMoves = (List<StreamBoard>) board.children().collect(Collectors.toList());
        for (StreamBoard entry : listOfMoves) {
            board = entry;
            int val = -alphaBeta(board, depth - 1, -beta, -bestVal);
            board = board.parent();
            if (val > bestVal) {
                bestVal = val;
            }
            alpha = alpha > val ? alpha : val;
            if (alpha >= beta) break;
        }//for

        addToTable(board,bestVal,alphaStart,beta);

        return bestVal;
    }//alphaBeta

    public void addToTable(StreamBoard board,int bestVal,int alphaStart, int beta){
        if (bestVal <= alphaStart) {
            ttable.put(board, new TableEntry(bestVal, 2));
        } else if (bestVal >= beta) {
            ttable.put(board, new TableEntry(bestVal, 1));
        } else {
            ttable.put(board, new TableEntry(bestVal, 0));
        }
    }

    public int playRandomly(StreamBoard board, boolean turn) {
        if (board.isWin()) {
            return (board.isBeginnersTurn() == turn) ? 1 : -1;
        }
        Random r = ThreadLocalRandom.current();
        while (!board.isDraw()) {
            List<StreamBoard> container = (List<StreamBoard>) board.children().collect(Collectors.toList());
            board = container.get(r.nextInt(container.size()));
            if (board.isWin()) {
                return (board.isBeginnersTurn() == turn) ? 1 : -1;
            }
        }
        return 0;
    }//playRandomly

    public int[] simulatePlays(StreamBoard board, int number) {
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

    public int evaluateBoard(StreamBoard board) {
        int[] val = simulatePlays(board, playCount);
        return val[2] - val[0];
    }//evaluateBoard

    public StreamBoard getBestMove() {
        return bestMove;
    }
}//class
