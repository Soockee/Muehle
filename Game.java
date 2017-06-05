package UnfinishedVersions.V0_1;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Stack;

/**
 * Created by Simon on 28.05.2017.
 */
/**********************************************/
/*                                            */
/*                                            */
/**********************************************/
public class Game implements Gameable{
    private Hashtable<Integer, Integer> transTable;
    /**********************************************
    /* board: current Board and Root of the Tree                                            *
    /**********************************************/
    private Board board;
    private Stack<Board> history;
    public Game(){
        history = new Stack<>();
        board = new Board();
    }

    public void makeMove(int turn, int move){
        history.push(board);
        board =  new Board(board,turn,move);
    }
    public boolean isWin(int board){
        return true;
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

    public Board getBoard() {
        return board;
    }
}
