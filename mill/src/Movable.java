
/**
 * Created by Paul Krappatsch on 31.05.2017.
 * -------------------------------------------
 * container-class for up to 3 ints
 */
public interface Movable {

    int getFrom();

    int getTo();

    int getRemove();

    void setFrom(int from);

    void setTo(int to);

    void setRemove(int remove);
}
