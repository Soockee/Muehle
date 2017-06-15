import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by Paul Krappatsch on 14.06.2017.
 */
public interface SaveableGame<Board extends ImmutableBoard> {

    default void save(Board board, String name) {
        save(board, Paths.get(name));
    }

    default void save(Board board, Path path) {
        try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            List history = board.getHistory();
            out.write(history.remove(0).toString());
            for(Object move : board.getHistory()) {
                out.write(", ");
                out.write(move.toString());
            }
            if(board.isFlipped()) out.write(", f");
        } catch(IOException ioe) {
            ioe.printStackTrace();
        }
    }

    Board load(String name);

    Board load(Path path);
}
