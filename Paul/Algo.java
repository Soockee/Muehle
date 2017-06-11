package mill;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

/**
 * Created by Paul Krappatsch on 02.06.2017.
 */
public class Algo {

    //43744155897 seq
    //24044524830 par
    ConcurrentHashMap<Integer, Integer> table = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        IntStream.range(0,10).forEach(i -> new Random());
        long t0 = System.nanoTime();
        Random r = new Random();
        System.out.println(System.nanoTime() -t0);
        /*Algo algo = new Algo();
        IntStream.range(0,10).forEach(i -> algo.collectRndmMoves(new Mill()));
        long t0 = System.nanoTime();
        algo.collectRndmMoves(new Mill());
        System.out.println(System.nanoTime()- t0);*/
    }

    void testOld() {
        IntStream.range(0,1000).forEach(i -> evaluateRndmMoves(new Mill()));
    }

    void testNew() {
        IntStream.range(0,1000).forEach(i -> evaluateRndmMovesAlt(new Mill()));
    }

    Board iterativeDepthSearch(int maxDepth, Board board) {
        return IntStream.rangeClosed(0, maxDepth)
                .parallel()
                .mapToObj(n -> depthSearch(n, new ConcurrentHashMap<>(), board))
                .skip(maxDepth)
                .findAny()
                .orElseThrow(Error::new);
    }

    Board depthSearch(int depth, ConcurrentHashMap<Integer, Integer> ttable, Board board) {
        Board tmp = board.streamChilds()
                .max(Comparator.comparingInt(board1 -> -negaMax(depth, ttable, board1)))
                .orElseThrow(Error::new);
        System.out.println("Bis jetzt bester Zug ist: " + board.getMove(tmp) + " in Tiefe " + depth);
        return tmp;
    }

    int negaMax(int depth, ConcurrentHashMap<Integer, Integer> ttable, Board board) {
        if (ttable.containsKey(board.hashCode())) {
            return ttable.get(board.hashCode());
        }
        if (board.isWin()) {
            addDoubles(-1000 + board.getDepth(), ttable, board);
            return -1000 + board.getDepth();
        }
        if (board.isDraw()) {
            addDoubles(0, ttable, board);
            return 0;
        }
        if (depth == 0) {
            int value = evaluateBoard(board) * board.getTurn();
            addDoubles(value, ttable, board);
            return value;
        }
        int max = board.streamChilds()
                .mapToInt(board1 -> -negaMax(depth - 1, ttable, board1))
                .max()
                .orElseThrow(Error::new);
        addDoubles(max, ttable, board);
        return max;
    }

    void addDoubles(int value, ConcurrentHashMap<Integer, Integer> ttable, Board board) {
        board.getIDsOfGroup()
                .parallel()
                .collect(() -> ttable, (map, i) -> map.put(i, value), ConcurrentHashMap::putAll);
    }

    int evaluateBoard(Board b) {
        int[] arr = collectRndmMoves(b);
        return arr[2] - arr[0];
    }

    int[] collectRndmMoves(Board b) {
        return IntStream
                .range(0, 10)
                .parallel() //=>ok
                .map(n -> evaluateRndmMoves(b))
                .collect(
                        () -> new int[3],
                        (int[] r, int i) -> r[i + 1] += 1,
                        (int[] ints, int[] ints2) -> {
                            ints[0] = ints2[0] = ints[0] + ints2[0];
                            ints[1] = ints2[1] = ints[1] + ints2[1];
                            ints[2] = ints2[2] = ints[2] + ints2[2];
                        }
                );
    }

    int evaluateRndmMoves(Board b) { //1 wenn maximierender Spieler gewinnt, 0 bei unentschieden, -1 falls minimierender Spieler
        Random r = ThreadLocalRandom.current();
        while (!b.isDraw()) {
            Board[] moves = b.streamChilds().toArray(Board[]::new);
            b = moves[r.nextInt(moves.length)];
            if (b.isWin()) return -b.getTurn();
        }
        return 0;
    }

    int evaluateRndmMovesRek(Board b, ThreadLocalRandom random) {
        if(b.isWin()) return -b.getTurn();
        if(b.isDraw()) return 0;
        Board[] childs = b.streamChilds().toArray(Board[]::new);
        return evaluateRndmMovesRek(childs[random.nextInt(childs.length)], random);
    }

    Board playRandoml(Board b) { //1 wenn maximierender Spieler gewinnt, 0 bei unentschieden, -1 falls minimierender Spieler
        Random r = ThreadLocalRandom.current();
        while (!b.isDraw()) {
            Board[] moves = b.streamChilds().toArray(Board[]::new);
            b = moves[r.nextInt(moves.length)];
            if (b.isWin()) return b;
        }
        return b;
    }


    int evaluateRndmMovesAlt(Board b) {
        if (b.isWin()) return -b.getTurn();
        if (b.isDraw()) return 0;
        ThreadLocalRandom r = ThreadLocalRandom.current();
        while (!b.isDraw()) {
            int idx = (int) b.streamChilds().count();
            b = b.streamChilds()
                    .skip(r.nextInt(idx))
                    .findFirst()
                    .get();
            if(b.isWin()) return -b.getTurn();
        }
        return 0;
    }
}