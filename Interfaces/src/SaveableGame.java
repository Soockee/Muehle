import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by Paul Krappatsch on 14.06.2017.
 */

public interface SaveableGame<SaveableBoard>  /*S extends SaveableBoard<?>>*/ {

    void save(SaveableBoard board, String name) throws IOException;

    void save(SaveableBoard board, Path path) throws IOException;

    SaveableBoard load(String name) throws IOException;

    SaveableBoard load(Path path) throws IOException;

    /*default void save(SaveableBoard board, String name) throws IOException {
        save(board, Paths.get(name));
    }

    default void save(SaveableBoard board, Path path) throws IOException {
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

    SaveableBoard load(String name) throws IOException;

    SaveableBoard load(Path path) throws IOException;*/
}
