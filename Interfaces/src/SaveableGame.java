import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by Paul Krappatsch on 14.06.2017.
 */

public interface SaveableGame<SaveableBoard /*S extends SaveableBoard<?>*/> {

    default void save(SaveableBoard board, String name) throws IOException {
        save(board, Paths.get(name));
    }

    void save(SaveableBoard board, Path path) throws IOException;

    default SaveableBoard load(String name) throws IOException {
        return load(Paths.get(name));
    }

    SaveableBoard load(Path path) throws IOException;

    /*default void save(S board, String name) throws IOException {
        save(board, Paths.get(name));
    }

    default void save(S board, Path path) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            out.write(board.getHistory().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(","))
            );
            if (board.isFlipped()) {
                out.write(",f");
            }
            out.write("\n");
        }
    }

    default S load(String name) throws IOException {
        return load(Paths.get(name));
    }

    S load(Path path) throws IOException;*/
}
