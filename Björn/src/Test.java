package Björn.src;

import java.util.Arrays;

/**
 * Created by xXThermalXx on 18.06.2017.
 * //Test Methode für das Problem in der Methode history;
 */
public class Test {
    public static void main(String [] args){
            long startTime = System.nanoTime();
            Morris board = new Morris();
            Ai ki = new Ai();
            System.out.println(ki.evaluateAlphaBeta(board,4).toString());
            System.out.println((System.nanoTime() - startTime) / 1000000000);

    }
}

/*
for(int i=0; i<15; i++) {
            long startTime = System.nanoTime();
            Morris board = new Morris();
            Ai ki = new Ai();
            //System.out.println(board.toString());
            ki.evaluateBoard(board);
            System.out.println((System.nanoTime() - startTime) / 1000000000);
        }
 */
