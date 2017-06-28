/**
 * Created by Björn Franke on 28.06.2017.
 */
public class AiThread extends Thread {

    private boolean alive;
    private Ai ki;
    private ImmutableBoard board;
    private int depth;
    private ImmutableBoard bestBoard;

    public AiThread(ImmutableBoard board, int depth){
        ki=new Ai();
        this.depth=depth;
        this.board=board;
        this.bestBoard=null;
    }

    @Override
    public void run() {
        ki.evaluateBestBoard(board, depth);
    }//run

    public Ai getKi(){
        return ki;
    }//getKi

}//class
