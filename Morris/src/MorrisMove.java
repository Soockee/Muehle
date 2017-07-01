import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */
public class MorrisMove {

    private Integer from;
    private int to;
    private Integer remove;

    public static Optional<MorrisMove> parseMove(String input, boolean isPhase1) {
        Pattern p = Pattern.compile("\\s*(?:([01]\\d|2[0123])\\s*-)?\\s*([01]\\d|2[0123])\\s*(?:-\\s*([01]\\d|2[0123]))?\\s*", Pattern.COMMENTS);
        Matcher m = p.matcher(input);
        if (!m.matches()) {
            return Optional.empty();
        }
        /*return Optional.of(isPhase1 ?
                new MorrisMove(null,
                        Integer.parseInt(m.group(2)),
                        m.group(3) == null ? null : Integer.parseInt(m.group(3))) :
                new MorrisMove(m.group(1) == null ? null : Integer.parseInt(m.group(1)),
                        Integer.parseInt(m.group(2)),
                        m.group(3) == null ? null : Integer.parseInt(m.group(3))));*/
        List<Integer> parts = Arrays.stream(input.split("-"))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        switch (parts.size()) {
            case 3:
                return Optional.of(MorrisMove.moveOrJumpAndRemove(parts.get(0), parts.get(1), parts.get(2)));
            case 1:
                return Optional.of(MorrisMove.place(parts.get(0)));
            default:
                return Optional.of(isPhase1 ?
                        MorrisMove.placeAndRemove(parts.get(0), parts.get(1)) :
                        MorrisMove.moveOrJump(parts.get(0), parts.get(1)));
        }
    }

    public static MorrisMove place(int to) {
        return new MorrisMove(null, to, null);
    }

    public static MorrisMove placeAndRemove(int to, Integer remove) {
        return new MorrisMove(null, to, remove);
    }

    public static MorrisMove moveOrJump(Integer from, int to) {
        return new MorrisMove(from, to, null);
    }

    public static MorrisMove moveOrJumpAndRemove(Integer from, int to, Integer remove) {
        return new MorrisMove(from, to, remove);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from, to, remove);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof MorrisMove &&
                ((MorrisMove) obj).getTo() == to &&
                ((MorrisMove) obj).getFrom().equals(getFrom()) &&
                ((MorrisMove) obj).getRemove().equals(getRemove());
    }

    private MorrisMove(Integer from, int to, Integer remove) {
        this.from = from;
        this.to = to;
        this.remove = remove;
    }

    Optional<Integer> getFrom() {
        return Optional.ofNullable(from);
    }

    int getTo() {
        return to;
    }

    Optional<Integer> getRemove() {
        return Optional.ofNullable(remove);
    }

    @Override
    public String toString() { // used for saving/loading
        return (getFrom().isPresent() ? String.format("%02d", from) + "-" : "")
                + String.format("%02d", to)
                + (getRemove().isPresent() ? "-" + String.format("%02d", remove) : "");
    }

    public String toStringUser() { // used in console interaction
        return (getFrom().isPresent() ? String.format("%02d", from + 1) + "-" : "")
                + String.format("%02d", to + 1)
                + (getRemove().isPresent() ? "-" + String.format("%02d", remove + 1) : "");
    }
}

