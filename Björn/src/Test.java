package Björn.src;

import java.util.Arrays;

/**
 * Created by xXThermalXx on 18.06.2017.
 * //Test Methode für das Problem in der Methode history;
 */
public class Test {
    public static void main(String [] args){
        Morris board=new Morris();
        Ai ki=new Ai();
        //System.out.println(board.toString());
        System.out.println(ki.evaluateAlphaBeta(board,10).toString());


    }
}
