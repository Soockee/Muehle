package UnfinishedVersions.V0_1;

import java.util.ArrayList;
import java.util.Hashtable;

/**
 * Created by Simon on 28.05.2017.
 */
public class Game implements Gameable{
    Hashtable<Integer, Integer> transTable;
    Board[] boards;
    public Game(){
        boards = new Board[2];
        boards[0] = new Board();
        boards[1] = new Board();
    }

    public int makeMove(int move, int board){
        return 0;
    }
    public boolean isWin(int board){
        return false;
    }
    public ArrayList<Integer> moves(int board){
        return null;
    }
    public int undoMove(int board){
        return 0;
    }
    public String toString(){
        return null;
    }
}
