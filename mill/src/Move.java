/**
 * Edit by Simon on 11.06.2017.
 */

/**
 * Created by Paul Krappatsch on 03.06.2017.
 */
public class Move implements Movable{

    private int from = -1;
    private int to = -1;
    private int remove = -1;

    Move() {

    }

    Move(int to) {
        this(-1, to, -1);
    }

    Move(int from, int to) {
        this(from, to, -1);
    }

    Move(int from, int to, int remove) {
        this.from = from;
        this.to = to;
        this.remove = remove;
    }

    public int getFrom() {
        return from;
    }

    public void setFrom(int from) {
        this.from = from;
    }

    public void setTo(int to) {
        this.to = to;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }

    public int getTo() {
        return to;
    }

    public int getRemove() {
        return remove;
    }

    @Override
    public String toString() {
        return "(" + (from + 1) + ", " + (to + 1) + ", " + (remove + 1) + ")";
    }
}
