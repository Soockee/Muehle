/**
 * Created by Paul Krappatsch on 11.06.2017.
 */
public class MorrisMove {

    private int from = -1;
    private int to = -1;
    private int remove = -1;
    private int test = 5;
    private int test2 = 5;
    
    MorrisMove() {

    }

    MorrisMove(int to) {
        this(-1, to, -1);
    }

    MorrisMove(int from, int to) {
        this(from, to, -1);
    }

    MorrisMove(int from, int to, int remove) {
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
    public boolean equals(Object obj) {
        return this.getTo() == ((MorrisMove) obj).getTo() && this.getFrom() == ((MorrisMove) obj).getFrom() && this.getRemove() == ((MorrisMove) obj).getRemove();
    }

    @Override
    public String toString() {
        return "(" + (from + 1) + ", " + (to + 1) + ", " + (remove + 1) + ")";
    }
}

