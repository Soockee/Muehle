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
        int i=0;
        while (i<100){
            System.out.println(ki.playRandomly(board));
        }

    }
}
