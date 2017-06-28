package ex;

/**
 * Created by Björn Franke on 28.06.2017.
 */
public class CollectThread extends Thread {

    private ImmutableBoard bestBoard;
    private long end;
    private AiThread ki;
    private boolean userIsActive;

    public CollectThread(int seconds, AiThread ki) {
        end = System.currentTimeMillis() + (60 * 1000 * seconds);
        bestBoard = null;
        this.ki = ki;
        userIsActive = true;
    }//Constructor


    @Override
    public void run() {

        do{
            bestBoard = ki.getKi().getBestMove();
        }while (System.currentTimeMillis() < end && ki.isAlive() && userIsActive);

        try {
            ki.interrupt();
        } catch (Exception e) {
            System.out.println("Ein Fehler ist aufgetretten");
        }//try-catch-Block

    }//run

    public ImmutableBoard getBestBoard() {
        return bestBoard;
    }//getBestBoard

    public void setUserIsActive(boolean val) {
        userIsActive = val;
    }//setUserIsActive
}//class
