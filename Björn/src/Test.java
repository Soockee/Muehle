package Björn.src;

import java.util.Arrays;
import java.util.Scanner;
import java.util.Set;

/**
 * Created by xXThermalXx on 18.06.2017.
 * //Test Methode für das Problem in der Methode history;
 */
public class Test {
    public static void main(String[] args) throws Throwable {
        CollectThread t =new CollectThread();
        Scanner sc =new Scanner(System.in);
        t.start();
        boolean b=true;
        while (t.isAlive()&& b){
            String eingabe=sc.next();
            if(!eingabe.equals("")){
               b=false;
               t.runs=false;
            }
        }
        System.out.println(t.list.size());
    }
}

/*
long startTime = System.nanoTime();
        Morris board = new Morris();
        Ai ki = new Ai();
        ki.evaluateBestBoard(board,2);
        Set<Integer> set=ki.getBestMoves().keySet();
        set.forEach(s->System.out.println(ki.getBestMoves().get(s).toString()));

        System.out.println((System.nanoTime() - startTime) / 1000000000 + " Sekunden");
 */
