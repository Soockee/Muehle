package Björn.src;

/**
 * Created by xXThermalXx on 26.06.2017.
 */
public class TestAlphaBeta {
    public static void main(String[] args) {
        ImmutableBoard board = new T3();
        Ai ki=new Ai();
        while (!board.isDraw()&&!board.isWin()){
            board=ki.iterativeDepthSearch(board,4);
        }
        System.out.println(board.toString());
    }

    /*
        board = board.makeMove(1);
        board = board.makeMove(4);
        board = board.makeMove(2);
        board = board.makeMove(6);
        board=board.makeMove(5);
        board =ki.iterativeDepthSearch(board,3);
        System.out.println(board.toString());
     */
}
