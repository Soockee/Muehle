import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */
public class MorrisMove {

    private Integer from;
    private int to;
    private Integer remove;

    public static Optional<MorrisMove> parseMove(String input, boolean isPhase1) {
        if (!input.matches("\\s*(?:(\\d{1,2})\\s*-)?\\s*(\\d{1,2})\\s*(?:-\\s*(\\d{1,2}))?\\s*"))
            return Optional.empty();
        List<Integer> parts = Arrays.stream(input.split("-"))
                .map(String::trim)
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        switch (parts.size()) {
            case 3:
                return Optional.of(MorrisMove.moveOrJumpAndRemove(parts.get(0), parts.get(1), parts.get(2)));
            case 2:
                return Optional.of(isPhase1 ?
                        MorrisMove.placeAndRemove(parts.get(0), parts.get(1)) :
                        MorrisMove.moveOrJump(parts.get(0), parts.get(1)));
            default:
                return Optional.of(MorrisMove.place(parts.get(0)));
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
        return toString().hashCode();
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

    public Optional<Integer> getFrom() {
        return Optional.ofNullable(from);
    }

    public int getTo() {
        return to;
    }

    public Optional<Integer> getRemove() {
        return Optional.ofNullable(remove);
    }

    @Override
    public String toString() {
        return (getFrom().isPresent() ? String.format("%02d", from) + "-" : "")
                + String.format("%02d", to)
                + (getRemove().isPresent() ? "-" + String.format("%02d", remove) : "");
    }
}

