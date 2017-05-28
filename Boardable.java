package UnfinishedVersions.V0_1;

import java.util.ArrayList;

/**
 * Created by Simon on 28.05.2017.
 */
public interface Boardable {
    @Override public int hashCode();
    public ArrayList<Board> children();
}
