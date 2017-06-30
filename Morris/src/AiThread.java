/**
 * Created by Björn Franke on 28.06.2017.
 */
public class AiThread extends Thread {

    private boolean alive;
    private Ai ki;
    private StreamBoard board;
    private int depth;
    private StreamBoard bestBoard;

    public AiThread(StreamBoard board, int depth){
        ki=new Ai(10);
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
