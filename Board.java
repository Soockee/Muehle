package UnfinishedVersions.V0_1;

import java.util.ArrayList;

/**
 * Created by Simon on 28.05.2017.
 */
public class Board implements Boardable {
    /**-----------------------------------------------------------------------------------------------------------------
     * Fields
     * -- int[] boards: Representation of each player
     *                  -> boards[0]: Player 1 : Black
     *                  -> boards[0]: Player 1 : White
     * -- Board parent: The Node in the upper Level of the Gametree
     *                  -> if parent = this: Root e.g highest level
     * -- Board[] children: all Boards which can be made by moving one possible move on this current Board
     *------------------------------------------------------------------------------------------------------------------*/
    private int[] boards;
    private Board parent;
    private Board[] children;

    /**-----------------------------------------------------------------------------------------------------------------
     * Constructor
     * -- Boards(): Root constructor. Has no parent because being the root
     * -- Boards(Board parent): Is a child of Board
     *                                 -> Cannot be empty
     *------------------------------------------------------------------------------------------------------------------*/
    public Board(){
        parent = this;
    }
    public Board(Board parent){
        this.parent = parent;
    }

    /**-----------------------------------------------------------------------------------------------------------------
     * Methods
     * -- children(): returns an ArrayList<Board> which contains all children Boards from this Board
     * -- hashCode(): returns an unique HashCode for this Board
     *------------------------------------------------------------------------------------------------------------------*/
    public ArrayList<Board> children(){
        return null;
    }

    @Override
    public int hashCode(){
        return 0;
    }
}
