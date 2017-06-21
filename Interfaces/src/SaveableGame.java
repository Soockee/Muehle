import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Paul Krappatsch on 14.06.2017.
 */

public interface SaveableGame<Board extends ImmutableBoard<?>> {

    default void saveNew(Board board, String name) throws IOException {
        saveNew(board, Paths.get(name));
    }

    default void saveNew(Board board, Path path) throws IOException {
        BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        out.write(board.getHistoryNew()
                .map(ImmutableBoard::getMove)
                .map(Optional::get)
                .map(Object::toString)
                .collect(Collectors.joining(","))
        );
        if (board.isFlipped()) out.write(",f");
        out.close();
    }

    default void save(Board board, String name) throws IOException {
        save(board, Paths.get(name));
    }

    default void save(Board board, Path path) throws IOException {
        BufferedWriter out = Files.newBufferedWriter(path, StandardCharsets.UTF_8);
        out.write(board.getHistory().stream()
                .map(Object::toString)
                .collect(Collectors.joining(","))
        );
        if (board.isFlipped()) out.write(",f");
        out.close();
    }


    Board load(String name) throws IOException;

    Board load(Path path) throws IOException;
}
