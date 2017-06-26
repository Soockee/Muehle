package Björn.src;

import java.util.ArrayList;

/**
 * Created by xXThermalXx on 26.06.2017.
 */
public class CollectThread extends Thread {

    ArrayList<Integer> list=new ArrayList<>();
    boolean runs=true;

    @Override
    public void run() {
        final long end = System.currentTimeMillis() + (2*1000);
        while (System.currentTimeMillis()<end&&runs){
            list.add(1);
            //System.out.println(list.size());
        }
    }//run
}//class
