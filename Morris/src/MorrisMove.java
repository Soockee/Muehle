

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */
public class MorrisMove {

    private int from ;
    private int to;
    private int remove;

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MorrisMove) {
            MorrisMove other = (MorrisMove) obj;
            if (other.hashCode() == hashCode()) return true;
        }
        return false;
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

    MorrisMove() {
        from = -1;
        to = -1;
        remove = -1;
    }

    MorrisMove(int from, int to, int remove) {
        this.from = from;
        this.to = to;
        this.remove = remove;
    }

    public int getFrom() {
        return from;
    }

    public int getTo() {
        return to;
    }

    public int getRemove() {
        return remove;
    }

    @Override
    public String toString() {
        return (from != -1 ? from + "-" : "") + to + (remove != -1 ? "-" + remove : "" );
    }
}

