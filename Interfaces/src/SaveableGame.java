import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;

/**
 * Created by Paul Krappatsch on 14.06.2017.
 */

public interface SaveableGame<SaveableBoard> {

    void save(SaveableBoard board, String name) throws IOException;

    void save(SaveableBoard board, Path path) throws IOException;

    SaveableBoard load(String name) throws IOException;

    SaveableBoard load(Path path) throws IOException;

/*default void save(S board, String name) throws IOException {
        save(board, Paths.get(name));
    }

    default void save(S board, Path path) throws IOException {
        BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        out.write(board.getHistory() .stream()
                .map(Object::toString)
                .collect(Collectors.joining(","))
        );
        if (board.isFlipped()) {
            out.write(",f");
        }
        out.write("\n");
        out.close();
    }

    SaveableBoard load(String name) throws IOException;

    SaveableBoard load(Path path) throws IOException;*/
}
