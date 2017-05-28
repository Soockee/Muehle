package UnfinishedVersions.V0_1;

import java.util.ArrayList;
/**
 * Created by Simon on 28.05.2017.
 */
public interface Gameable {
    public int makeMove(int move, int board);
    public boolean isWin(int board);
    public ArrayList<Integer> moves(int board);
    public int undoMove(int board);
    public String toString();
}
