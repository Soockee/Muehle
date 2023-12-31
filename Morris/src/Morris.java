import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */

public class Morris implements SaveableGame<Morris>, StreamBoard<MorrisMove> {

    private final int[] board;
    private final int turn;
    private final int movesWithoutRemoving; // used for detecting draws
    private final Morris parent;
    private final int phase;
    private final boolean isFlipped;

    /******************************************************************
     * Fields:
     *  board[]:
     *      -> 24 elements:
     *          -> -1 => Player2 ; 0 => empty (no stones placed) ; +1 => Player1
     *
     *  Turn:
     *      -> Represents the current player, also the value used to fill the board
     *
     *  movesWithoutRemoving:
     *      -> to determintate an draw
     *          => 50 moves without removing is a Draw
     *  parent:
     *      -> Parentboard of the current previous
     *          => Rootboard got no previous
     *
     *  phase:
     *      -> phase determinate the movepossibilites of the players
     *          => phase 1: Stones can be set anywhere as long as the element of the board[] is 0
     *          => phase 2: player can move their stones to an adjacent position
     *          => phase 3: player one can jumps to any free position, player -1 has to move
     *          => phase 4: like 3 but reversed
     *          => phase 5: both players can jump
     *
     *  isFlipped:
     *      -> determines the colors of the Stones for the toString() method
     *
     *****************************************************************/
    public static void main(String[] args) {
        Morris b = new Morris();

        b = b.makeMove(MorrisMove.place(0)).get();

        b = b.makeMove(MorrisMove.place(5)).get();

        b = b.makeMove(MorrisMove.place(1)).get();

        b = b.makeMove(MorrisMove.place(6)).get();

        //b = b.makeMove(MorrisMove.place(2)).get();

        try {

            b.save(b, "saveTwo.txt");

        } catch (IOException e) {

            System.out.println(e.getMessage());

        }
    }

    Morris() {
        board = new int[24];
        turn = +1;
        movesWithoutRemoving = 0;
        parent = null;
        phase = 1;
        isFlipped = false;
    }

    private Morris(int[] board, int turn, int movesWithoutRemoving, Morris parent, int phase, boolean isFlipped) {
        this.board = board;
        this.turn = turn;
        this.movesWithoutRemoving = movesWithoutRemoving;
        this.parent = parent;
        this.phase = phase;
        this.isFlipped = isFlipped;
    }

    @Override
    public Optional<Morris> makeMove(MorrisMove move) {// may be changed to Optional later
        return children()
                .filter(board -> board.getMove().get().equals(move))
                .findAny();
    }

    @Override
    public StreamBoard<MorrisMove> parent() {
        return parent;
    }

    private Morris buildChild(MorrisMove morrisMove) {
        if (phase == 1) return buildChildPhasePlace(morrisMove);
        else return buildChildPhaseMoveAndJump(morrisMove);
    }

    private Morris buildChildPhasePlace(MorrisMove morrisMove) {
        int[] newBoard = Arrays.copyOf(board, 24);
        newBoard[morrisMove.getTo()] = turn;
        int newMovesWithoutRemoving;
        if (morrisMove.getRemove().isPresent()) {
            newBoard[morrisMove.getRemove().get()] = 0;
            newMovesWithoutRemoving = 0;
        } else {
            newMovesWithoutRemoving = movesWithoutRemoving + 1;
        }
        int newPhase;
        if (getHistory().size() == 17) newPhase = 2;
        else newPhase = 1;
        return new Morris(newBoard, -turn, newMovesWithoutRemoving, this, newPhase, isFlipped);
    }

    private Morris buildChildPhaseMoveAndJump(MorrisMove morrisMove) {
        int[] newBoard = Arrays.copyOf(board, 24);
        newBoard[morrisMove.getFrom().get()] = 0; // getFrom() exists if game is not in phase 1
        newBoard[morrisMove.getTo()] = turn;
        int newMovesWithoutRemoving;
        int newPhase;
        if (morrisMove.getRemove().isPresent()) {
            newBoard[morrisMove.getRemove().get()] = 0;
            newMovesWithoutRemoving = 0;
            if (phase != 1 && numberOfStones(-turn) == 4) { // phase1 stays at 1
                if (phase == 2) newPhase = turn == 1 ? 4 : 3;
                else newPhase = 5;
            } else newPhase = phase;
        } else {
            newPhase = phase;
            newMovesWithoutRemoving = movesWithoutRemoving + 1;
        }
        return new Morris(newBoard, -turn, newMovesWithoutRemoving, this, newPhase, isFlipped);
    }

    private int numberOfStones(int player) {
        return (int) Arrays.stream(board) // at max 9, so never above Integer.MAX_VALUE
                .filter(n -> n == player)
                .count();
    }

    @Override
    public Stream<Morris> children() {
        return streamMoves().map(this::buildChild);
    }

    /******************************************************************
     * streamMoves()
     *      -> chooses the stream
     *      -> depends on the phase
     *
     *****************************************************************/
    Stream<MorrisMove> streamMoves() {
        if (phase == 1) return streamMovesPhasePlace();
        else if (phase == 2 || phase == 3 && turn == -1 || phase == 4 && turn == +1)
            return streamMovesPhaseMove();
        return streamMovesPhaseJump();
    }

    private Stream<MorrisMove> streamMovesPhasePlace() {
        return IntStream.range(0, 24)
                .filter(to -> board[to] == 0)
                .mapToObj(MorrisMove::place)
                .map(this::streamMovesWithRemoves)
                .flatMap(morrisMoveStream -> morrisMoveStream);
    }

    /******************************************************************
     *   streamMovesPhaseMove()
     *      -> iterates through the every element of the board
     *      -> filters the positions of the current player
     *      -> adds a MorrisMove if the direct neighbour is an empty Position
     *      -> adds the streamMovesWithRemoves to the stream
     *      -> combines all the mini streams into one big and returns it
     *
     *****************************************************************/
    private Stream<MorrisMove> streamMovesPhaseMove() {
        final int[][] moves = {
                {1, 7}, {0, 2, 9}, {1, 3}, {2, 11, 4}, {3, 5}, {4, 13, 6}, {5, 7}, {0, 6, 15},
                {9, 15}, {1, 8, 10, 17}, {9, 11}, {3, 10, 12, 19}, {11, 13}, {5, 12, 14, 21}, {13, 15}, {7, 8, 14, 23},
                {17, 23}, {9, 16, 18}, {17, 19}, {18, 11, 20}, {19, 21}, {13, 20, 22}, {21, 23}, {15, 16, 22}
        };
        return IntStream.range(0, 24)
                .filter(from -> board[from] == turn)
                .mapToObj(from -> Arrays.stream(moves[from])
                        .filter(to -> board[to] == 0)
                        .mapToObj(to -> MorrisMove.moveOrJump(from, to))
                )
                .flatMap(moveStream -> moveStream)
                .map(this::streamMovesWithRemoves)
                .flatMap(morrisMoveStream -> morrisMoveStream);
    }

    /******************************************************************
     *  streamMovesPhaseJump():
     *      -> iterates through every element of the board
     *      -> filters all positions of the current player
     *      -> adds a MorrisMove for every free Position on the Board
     *      -> combines all the mini-streams into a big one and returns it
     *
     *****************************************************************/
    private Stream<MorrisMove> streamMovesPhaseJump() {
        return IntStream.range(0, 24)
                .filter(from -> board[from] == turn)
                .mapToObj(from -> IntStream.range(0, 24)
                        .filter(to -> board[to] == 0)
                        .mapToObj(to -> MorrisMove.moveOrJump(from, to))
                )
                .flatMap(morrisMoveStream -> morrisMoveStream)
                .map(this::streamMovesWithRemoves)
                .flatMap(morrisMoveStream -> morrisMoveStream);
    }

    /******************************************************************
     * isFlipped():
     *      -> pretty much a getter-method
     *      -> returns if the board was flipped
     *****************************************************************/
    @Override
    public boolean isFlipped() {
        return isFlipped;
    }

    private Stream<MorrisMove> streamMovesWithRemoves(MorrisMove morrisMove) {
        if (numberOfClosedPotentialMills(morrisMove) > 0) { // doesn't account for double mills atm
            int[] openStone = findOpenStones(-turn).toArray();
            if (openStone.length == 0) {//if all Stones are in Mills, Mills can be broken
                return IntStream.range(0, 24)
                        .filter(pos -> board[pos] == -turn)
                        .mapToObj(remove -> MorrisMove.moveOrJumpAndRemove(morrisMove.getFrom().orElse(null), morrisMove.getTo(), remove));
            }//falls getFrom nichtexistent ist,
            return Arrays.stream(openStone)
                    .mapToObj(remove -> MorrisMove.moveOrJumpAndRemove(morrisMove.getFrom().orElse(null), morrisMove.getTo(), remove));
        }
        return Stream.of(morrisMove);
    }

    private long numberOfClosedPotentialMills(MorrisMove move) {
        final int[][][] mills = {
                {{1, 2,}, {6, 7}},
                {{0, 2}, {9, 17}},
                {{0, 1}, {3, 4}},
                {{2, 4}, {19, 11}},
                {{2, 3}, {6, 5}},
                {{6, 4}, {21, 13}},
                {{5, 4}, {0, 7}},
                {{0, 6}, {15, 23}},

                {{9, 10}, {14, 15}},
                {{8, 10}, {1, 17}},
                {{8, 9}, {11, 12}},
                {{10, 12}, {19, 3}},
                {{10, 11}, {14, 13}},
                {{14, 12}, {21, 5}},
                {{13, 12}, {15, 8}},
                {{14, 8}, {7, 23}},

                {{17, 18}, {23, 22}},
                {{16, 18}, {1, 9}},
                {{16, 17}, {19, 20}},
                {{18, 20}, {11, 3}},
                {{18, 19}, {22, 21}},
                {{22, 20}, {13, 22}},
                {{21, 20}, {16, 23}},
                {{16, 22}, {7, 15,}}
        };
        return Arrays.stream(mills[move.getTo()])
                .map(ints -> Arrays.stream(ints)
                        .map(i -> i == move.getFrom().orElse(-1) ? 0 : board[i])
                        .filter(i -> i == turn)
                        .count()
                )
                .filter(i -> i == 2)
                .count();
    }

    /******************************************************************
     *
     *
     *****************************************************************/
    private IntStream findOpenStones(int player) { // player being the encoding for the players stones on the board, +1 or -1
        final int[][][] mills = {
                {{1, 2,}, {6, 7}},
                {{0, 2}, {9, 17}},
                {{0, 1}, {3, 4}},
                {{2, 4}, {19, 11}},
                {{2, 3}, {6, 5}},
                {{6, 4}, {21, 13}},
                {{5, 4}, {0, 7}},
                {{0, 6}, {15, 23}},

                {{9, 10}, {14, 15}},
                {{8, 10}, {1, 17}},
                {{8, 9}, {11, 12}},
                {{10, 12}, {19, 3}},
                {{10, 11}, {14, 13}},
                {{14, 12}, {21, 5}},
                {{13, 12}, {15, 8}},
                {{14, 8}, {7, 23}},

                {{17, 18}, {23, 22}},
                {{16, 18}, {1, 9}},
                {{16, 17}, {19, 20}},
                {{18, 20}, {11, 3}},
                {{18, 19}, {22, 21}},
                {{22, 20}, {13, 22}},
                {{21, 20}, {16, 23}},
                {{16, 22}, {7, 15,}}
        };

        return IntStream.range(0, 24)
                .filter(i -> board[i] == player)
                .filter(i -> Arrays.stream(mills[i]) // stream mill partners
                        .map((int[] ints) -> (Arrays.stream(ints)
                                .map(pos -> board[pos])
                                .filter(i1 -> i1 == player)
                                .count())
                        )
                        .filter(integer -> integer == 2)
                        .count() == 0);
    }

    /******************************************************************
     *  getMove():
     *      returns a MorrisMove which holds the information to move from the previous board to  t
     *      he current board
     *
     *      returns a illegalArgumentException if no MorrisMove exists which would lead to child
     *
     *****************************************************************/
    @Override
    public Optional<MorrisMove> getMove() {
        if (parent == null) return Optional.empty();
        return Optional.of(MorrisMove.moveOrJumpAndRemove(
                IntStream.range(0, 24)
                        .filter(i -> parent.board[i] == -turn)
                        .filter(i -> board[i] == 0)
                        .boxed()
                        .findAny()
                        .orElse(null),
                IntStream.range(0, 24)
                        .filter(i -> parent.board[i] == 0)
                        .filter(i -> board[i] == -turn)
                        .findAny()
                        .getAsInt(), // can´t be null
                IntStream.range(0, 24)
                        .filter(i -> parent.board[i] == turn)
                        .filter(i -> board[i] == 0)
                        .boxed()
                        .findAny()
                        .orElse(null)
        ));
    }

    /******************************************************************
     *  getHistory():
     *      -> returns a List, which contains the MorrisMoves used to create the current board
     *
     *****************************************************************/

    @Override
    public boolean isBeginnersTurn() {
        return turn == +1;
    }

    /******************************************************************
     *  toString():
     *      -> creates a textual representation of the Morrisboard
     *
     *****************************************************************/

    @Override
    public String toString() {
        final int[][] display = {
                {0, -4, -4, -4, -4, -4, 1, -4, -4, -4, -4, -4, 2},
                {-3, -2, -2, -2, -2, -2, -3, -2, -2, -2, -2, -2, -3},
                {-3, -2, 8, -4, -4, -4, 9, -4, -4, -4, 10, -2, -3},
                {-3, -2, -3, -2, -2, -2, -3, -2, -2, -2, -3, -2, -3},
                {-3, -2, -3, -2, 16, -4, 17, -4, 18, -2, -3, -2, -3},
                {7, -4, 15, -4, 23, -2, -2, -2, 19, -4, 11, -4, 3},
                {-3, -2, -3, -2, 22, -4, 21, -4, 20, -2, -3, -2, -3},
                {-3, -2, -3, -2, -2, -2, -3, -2, -2, -2, -3, -2, -3},
                {-3, -2, 14, -4, -4, -4, 13, -4, -4, -4, 12, -2, -3},
                {-3, -2, -2, -2, -2, -2, -3, -2, -2, -2, -2, -2, -3},
                {6, -4, -4, -4, -4, -4, 5, -4, -4, -4, -4, -4, 4}
        };
        final char[] repr = isFlipped ? new char[]{'X', '.', 'O', '-', '|', ' '} : new char[]{'O', '.', 'X', '-', '|', ' '};
        return IntStream.rangeClosed(0, 10).mapToObj(row -> Arrays.stream(display[row])
                .map(n -> (n < 0) ? n + 6 : board[n])
                .map(n -> repr[n + 1])
                .mapToObj(n -> Character.toString((char) n))
                .collect(Collectors.joining("  ")) //1-3 Felder Abstand
        ).collect(Collectors.joining("\n", "\n", "\n"));
    }

    /******************************************************************
     *  isWin():
     *      returns true if:
     *          => the current player as less then 3 stones
     *          => the current player as no possible moves
     *      returns false if:
     *          => phase 1 is active
     *
     *     the opponent wins when the return value is true
     *
     *****************************************************************/

    @Override
    public boolean isWin() { //return true after winning game, player -turn wins
        return phase != 1 && (streamMoves().count() == 0 || // numberOfStones(turn) < 3
                Arrays.stream(board)
                        .filter(n -> turn == n)
                        .count() < 3);
    }

    /******************************************************************
     * isDraw():
     *      -> if a games has 50 consecutive moves without removing a stone
     *      the game ends in a draw
     *
     *****************************************************************/
    @Override

    //movesWithoutRemoving needs adjustment 30.06: raised to 25 in order to prevent isDraw():true too early
    public boolean isDraw() {
        return phase != 1 && (movesWithoutRemoving > 25 || (numberOfStones(turn) == 3 && numberOfStones(-turn) == 3));
    }

    /******************************************************************
     *  flip():
     *      -> switch the colors of the current Board
     *
     *****************************************************************/
    @Override
    public StreamBoard<MorrisMove> flip() {
        return new Morris(Arrays.copyOf(board, 24), turn, movesWithoutRemoving, parent, phase, !isFlipped);
    }

    @Override
    public void save(Morris board, String name) throws IOException {
        save(board, Paths.get(name));
    }

    @Override
    public void save(Morris board, Path path) throws IOException {
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

    @Override
    public Morris load(String name) throws IOException {
        return load(Paths.get(name));
    }

    @Override
    public Morris load(Path path) throws IOException {
        Morris load = new Morris();
        LinkedList<String> moveParts;
        try (Stream<String> lines = Files.lines(path, StandardCharsets.UTF_8)) {
            moveParts = lines
                    .filter(s -> !s.isEmpty())
                    .map(s -> s.split(","))
                    .map(Arrays::stream)
                    .flatMap(stringStream -> stringStream)
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toCollection(LinkedList::new));
        }
        if (moveParts.getLast().toLowerCase().equals("f")) {
            moveParts.removeLast();
            load = (Morris) load.flip();
        }
        for (String movePart : moveParts) {
            MorrisMove move = MorrisMove.parseMove(movePart, load.phase == 1)
                    .orElseThrow(() -> new IOException("File is in invalid Format"));
            load = load.makeMove(move).orElseThrow(() -> new IOException("File contains illegal Moves"));
        }
        return load;
    }

    @Override
    public int hashCode() { //? improve HashCode function with new and better Hash-Method
        return Arrays.hashCode(getGroup().mapToInt(Morris::getID).sorted().toArray());
    }

    private Stream<Morris> getGroup() {
        return Stream.of(
                this, //this cuz immutable boards => new Morris(Arrays.copyOf(board, 24), turn, movesWithoutRemoving, parent, phase, isFlipped), //? maybe this
                rotate(),
                rotate().rotate(),
                rotate().rotate().rotate(),
                mirror(),
                rotate().mirror(),
                rotate().rotate().mirror(),
                rotate().rotate().rotate().mirror(),
                swapInnerAndOuterRing(),
                swapInnerAndOuterRing().rotate(),
                swapInnerAndOuterRing().rotate().rotate(),
                swapInnerAndOuterRing().rotate().rotate().rotate(),
                swapInnerAndOuterRing().mirror(),
                swapInnerAndOuterRing().rotate().mirror(),
                swapInnerAndOuterRing().rotate().rotate().mirror(),
                swapInnerAndOuterRing().rotate().rotate().rotate().mirror()
        );
    }

    private int getID() {
        return Arrays.hashCode(board) * turn;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Morris &&
                turn == ((Morris) obj).turn &&
                phase == ((Morris) obj).phase &&
                getGroup().anyMatch(morris -> Arrays.equals(morris.board, ((Morris) obj).board));
    }

    private Morris mirror() {
        final int[] newBoardOrder = {2, 1, 0, 7, 6, 5, 4, 3, 10, 9, 8, 15, 14, 13, 12, 11, 18, 17, 16, 23, 22, 21, 20, 19};
        int[] newBoard = Arrays.stream(newBoardOrder)
                .map(pos -> board[pos])
                .toArray();
        return new Morris(newBoard, turn, movesWithoutRemoving, parent, phase, isFlipped);
    }

    private Morris rotate() {
        final int[] newBoardOrder = {6, 7, 0, 1, 2, 3, 4, 5, 14, 15, 8, 9, 10, 11, 12, 13, 22, 23, 16, 17, 18, 19, 20, 21};
        int[] newBoard = Arrays.stream(newBoardOrder)
                .map(pos -> board[pos])
                .toArray();
        return new Morris(newBoard, turn, movesWithoutRemoving, parent, phase, isFlipped);
    }

    private Morris swapInnerAndOuterRing() {
        final int[] newBoardOrder = {16, 17, 18, 19, 20, 21, 22, 23, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
        int[] newBoard = Arrays.stream(newBoardOrder)
                .map(pos -> board[pos])
                .toArray();
        return new Morris(newBoard, turn, movesWithoutRemoving, parent, phase, isFlipped);
    }
}