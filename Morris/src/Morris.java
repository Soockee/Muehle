import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Created by Paul Krappatsch on 11.06.2017.
 */

public class Morris implements SaveableGame<Morris>, ImmutableBoard<MorrisMove> {

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
     *  previous:
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
     *      -> determinate the colors of the Stones for the toString() method
     *
     *****************************************************************/

    private final int[] board;

    private final static int[][] zobristHash = { // 0 => player +1, 1 => player -1
            new Random().ints(24L).toArray(),
            new Random().ints(24L).toArray(),
    };

    private final int zobristHashCode = 1010;//PlaceHolder
    private final int turn;
    private final int movesWithoutRemoving; // used for detecting draws
    private final Morris parent;
    private final int phase;
    private final boolean isFlipped;
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

    int calculateZobristHashForChild(MorrisMove morrisMove) {
        if(morrisMove.getRemove() == -1 && morrisMove.getFrom() == 1) {
            return zobristHashCode ^ zobristHash[turn == +1 ? 0 : 1][morrisMove.getTo()];
        }
        int idx = turn == +1 ? 0 : 1;
        if(morrisMove.getFrom() == -1) {
            return zobristHashCode ^ zobristHash[idx][morrisMove.getTo()] ^ zobristHash[idx][morrisMove.getRemove()];
        }
        if(morrisMove.getRemove() == -1) {
            return zobristHashCode ^ zobristHash[idx][morrisMove.getTo()] ^ zobristHash[idx][morrisMove.getFrom()];
        }
        return zobristHashCode ^ zobristHash[idx][morrisMove.getTo()]
                ^ zobristHash[idx][morrisMove.getFrom()]
                ^ zobristHash[idx][morrisMove.getTo()];
    }

    public static void main(String[] args) {
        SaveableGame<Morris> game = new Morris();
    }

    @Override
    public ImmutableBoard<MorrisMove> parent() {
        return parent;
    }

    @Override
    public Optional<ImmutableBoard<MorrisMove>> makeMoveNew(MorrisMove morrisMove) {
        if (phase == 1) return Optional.of(makeMovePhasePlace(morrisMove));
        else return Optional.of(makeMovePhaseMoveAndJump(morrisMove));
    }

    @Override
    public ImmutableBoard<MorrisMove> makeMove(MorrisMove morrisMove) {
        if (phase == 1) return makeMovePhasePlace(morrisMove);
        else return makeMovePhaseMoveAndJump(morrisMove);
    }

    private Morris makeMovePhasePlace(MorrisMove morrisMove) {
        int[] newBoard = Arrays.copyOf(board, 24);
        newBoard[morrisMove.getTo()] = turn;
        int newMovesWithoutRemoving;
        if (morrisMove.getRemove() != -1) {
            newBoard[morrisMove.getRemove()] = 0;
            newMovesWithoutRemoving = 0;
        } else {
            newMovesWithoutRemoving = movesWithoutRemoving + 1;
        }
        int newPhase;
        if (getHistory().size() == 17) newPhase = 2;
        else newPhase = 1;
        return new Morris(newBoard, -turn, newMovesWithoutRemoving, this, newPhase, isFlipped);
    }

    private Morris makeMovePhaseMoveAndJump(MorrisMove morrisMove) {
        int[] newBoard = Arrays.copyOf(board, 24);
        newBoard[morrisMove.getFrom()] = 0;
        newBoard[morrisMove.getTo()] = turn;
        int newMoveswithRemoving;
        int newPhase;
        if (morrisMove.getRemove() != -1) {
            newBoard[morrisMove.getRemove()] = 0;
            newMoveswithRemoving = 0;
            if (phase != 1 && numberOfStones(-turn) == 4) { // phase1 stays at 1
                if (phase == 2) newPhase = turn == 1 ? 4 : 3;
                else newPhase = 5;
                //else if (child.phase == 3) child.phase = child.turn == 1 ? 3 : 5; // else child.phase = 5;
                //else if (child.phase == 4) child.phase = child.turn == 1 ? 5 : 4;
            } else newPhase = phase;
        } else {
            newPhase = phase;
            newMoveswithRemoving = movesWithoutRemoving + 1;
        }
        return new Morris(newBoard, -turn, newMoveswithRemoving, this, newPhase, isFlipped);
    }

    private long numberOfStones(int player) {
        return Arrays.stream(board)
                .filter(n -> n == player)
                .count();
    }

    @Override
    public Stream<ImmutableBoard<MorrisMove>> childs() {
        return streamMoves().map(this::makeMoveNew).map(Optional::get);
    }

    @Override
    public List<MorrisMove> moves() {
        return streamMoves().collect(Collectors.toList());
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
                .mapToObj(MorrisMove::new)
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
                        .mapToObj(to -> new MorrisMove(from, to))
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
                        .mapToObj(to -> new MorrisMove(from, to))
                )
                .flatMap(morrisMoveStream -> morrisMoveStream)
                .map(this::streamMovesWithRemoves)
                .flatMap(morrisMoveStream -> morrisMoveStream);
    }

    /******************************************************************
     * isFlipped():
     *      -> pretty much a getter-method
     *      -> returns if the board was flipped
     *
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
                        .filter(i -> i == -turn)
                        .mapToObj(i -> new MorrisMove(morrisMove.getFrom(), morrisMove.getTo(), i));
            }
            return Arrays.stream(openStone)
                    .mapToObj(i -> new MorrisMove(morrisMove.getFrom(), morrisMove.getTo(), i));
        }
        return Stream.of(morrisMove);
    }

    long numberOfClosedPotentialMills(MorrisMove move) {
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
                        .map(i -> i == move.getFrom() ? 0 : board[i])
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
    IntStream findOpenStones(int player) { // player being the encoding for the players stones on the board, +1 or -1
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
                .filter(i ->
                        Arrays.stream(mills[i]) // stream mill partners
                                .map((int[] ints) -> (Arrays.stream(ints)
                                        .map(pos -> board[pos])
                                        .filter(i1 -> i1 == player)
                                        .count()))
                                .filter(integer -> integer == 2)
                                .count() == 0);
    }

    /******************************************************************
     *  getMove():
     *      returns a MorrisMove which holds the information to move from the current board
     *      to the child
     *
     *      returns a illegalArgumentException if no MorrisMove exists which would lead to child
     *
     *****************************************************************/
    @Override
    public Optional<MorrisMove> getMove() {
        if(parent == null) return Optional.empty();
        return Optional.of(new MorrisMove(IntStream.range(0, 24)
                .filter(i -> parent.board[i] == turn)
                .filter(i -> board[i] == 0)
                .findAny()
                .orElse(-1),
        IntStream.range(0, 24)
                .filter(i -> parent.board[i] == 0)
                .filter(i -> board[i] == turn)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("no MorrisMove possible")),
        IntStream.range(0, 24)
                .filter(i -> parent.board[i] == -turn)
                .filter(i -> board[i] == 0)
                .findAny()
                .orElse(-1)
        ));
    }

    /******************************************************************
     *  getHistory():
     *      -> returns a List, which contains the MorrisMoves used to create the current board
     *
     *****************************************************************/
    @Override
    public Stream<ImmutableBoard<MorrisMove>> getHistoryNew() {
        Morris[] historyReversed =
                Stream.iterate(this, morris -> morris.parent() != null, morris -> morris.parent)
                .toArray(Morris[]::new);
        return IntStream.rangeClosed(1,historyReversed.length)
                .mapToObj(i -> historyReversed[historyReversed.length - i]);
    }

    @Override
    public List<MorrisMove> getHistory() {
        LinkedList<MorrisMove> history = new LinkedList<>();
        Stream.iterate(this, t3 -> t3.parent != null, t3 -> t3.parent)
                .filter(t3 ->t3.parent != null)
                .map(Morris::getMove)
                .map(Optional::get)
                .forEachOrdered(history::addFirst);
        return history;
    }

    @Override
    public boolean isBeginnersTurn() {
        return Stream.iterate(this, morris -> morris.parent != null, morris -> morris.parent)
                .count() % 2 == 0;
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
        char[] repr = isFlipped ? new char[]{'X', '.', 'O', '-', '|', ' '} : new char[]{'O', '.', 'X', '-', '|', ' '};
        return IntStream.rangeClosed(0, 10).mapToObj(row -> Arrays.stream(display[row])
                //.boxed()
                .map(n -> (n < 0) ? n + 6 : board[n])
                .map(n -> repr[n + 1])
                .mapToObj(n -> Character.toString( (char) n))
                .collect(Collectors.joining("  ")) //1-3 Felder Abstand
        ).collect(Collectors.joining("\n", "\n", "")); // prefix "\n" für jShell
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
        return phase != 1 && (streamMoves().count() == 0 ||
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
    public boolean isDraw() {
        return movesWithoutRemoving >= 50;
    }

    /******************************************************************
     *  flip():
     *      -> switch the colors of the current Board
     *
     *****************************************************************/
    @Override
    public ImmutableBoard<MorrisMove> flip() {
        return new Morris(Arrays.copyOf(board, 24), turn, movesWithoutRemoving, parent, phase, !isFlipped);
    }

    /******************************************************************
     *
     *
     *****************************************************************/
    //\s*(?:Turn\s*\d*\s*:)?\s*(\d+)?\s*->\s*(\d+)\s*(?::\s*(\d+))?\s* regex for Loading
    @Override
    public Morris load(String name) {
        return load(Paths.get(name));
    }

    @Override
    public Morris load(Path path) {
        Morris load = new Morris();
        Pattern format = Pattern.compile("(?:\\d, )* (?:,(f))");
        try {
            LinkedList<String> moves = Files.lines(path, StandardCharsets.UTF_8)
                    .map(s -> s.split(","))
                    .map(Arrays::stream)
                    .flatMap(stringStream -> stringStream)
                    .map(String::trim)
                    .collect(Collectors.toCollection(LinkedList::new));
            if (moves.getLast().toLowerCase().equals("f")) {
                moves.removeLast();
                load.flip();
            }
            for (String move : moves) {
                List<Integer> lst = Stream.of(move)
                        .map(s -> s.split("-"))
                        .map(Arrays::stream)
                        .flatMap(stringStream -> stringStream)
                        .map(String::trim)
                        .map(Integer::parseInt)
                        .collect(Collectors.toList());
                switch (lst.size()) {
                    case 3:
                        load = (Morris) load.makeMove(new MorrisMove(lst.get(0), lst.get(1), lst.get(2)));
                        break;
                    case 2:
                        load = (Morris) load.makeMove(new MorrisMove(lst.get(0), lst.get(1)));
                        break;
                    default:
                        load = (Morris) load.makeMove(new MorrisMove(lst.get(0)));
                        break;
                }
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return load;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(IntStream.of(
                getID(),
                rotate().getID(),
                rotate().rotate().getID(),
                rotate().rotate().rotate().getID(),
                mirror().getID(),
                rotate().mirror().getID(),
                rotate().rotate().mirror().getID(),
                rotate().rotate().rotate().mirror().getID(),
                swapInnerAndOuterRing().getID(),
                swapInnerAndOuterRing().rotate().getID(),
                swapInnerAndOuterRing().rotate().rotate().getID(),
                swapInnerAndOuterRing().rotate().rotate().rotate().getID(),
                swapInnerAndOuterRing().mirror().getID(),
                swapInnerAndOuterRing().rotate().mirror().getID(),
                swapInnerAndOuterRing().rotate().rotate().mirror().getID(),
                swapInnerAndOuterRing().rotate().rotate().rotate().mirror().getID()
        ).toArray());
    }

    int getID() {
        return Arrays.hashCode(board);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Morris) {
            Morris other = (Morris) obj;
            return other.hashCode() == hashCode();
        }
        return false;
    }

    // Test only
    public int getPhase() {
        return phase;
    }

    Morris mirror() {
        final int[] newBoardOrder = {2, 1, 0, 7, 6, 5, 4, 3, 10, 9, 8, 15, 14, 13, 12, 11, 18, 17, 16, 23, 22, 21, 20, 19};
        int[] newBoard = Arrays.stream(newBoardOrder)
                .map(i -> board[i])
                .toArray();
        return new Morris(newBoard, turn, movesWithoutRemoving, parent, phase, isFlipped);
    }

    Morris rotate() {
        final int[] newBoardOrder = {6, 7, 0, 1, 2, 3, 4, 5, 14, 15, 8, 9, 10, 11, 12, 13, 22, 23, 16, 17, 18, 19, 20, 21};
        int[] newBoard = Arrays.stream(newBoardOrder)
                .map(i -> board[i])
                .toArray();
        return new Morris(newBoard, turn, movesWithoutRemoving, parent, phase, isFlipped);
    }

    Morris swapInnerAndOuterRing() {
        final int[] newBoardOrder = {16, 17, 18, 19, 20, 21, 22, 23, 8, 9, 10, 11, 12, 13, 14, 15, 0, 1, 2, 3, 4, 5, 6, 7};
        int[] newBoard = Arrays.stream(newBoardOrder)
                .map(i -> board[i])
                .toArray();
        return new Morris(newBoard, turn, movesWithoutRemoving, parent, phase, isFlipped);
    }
}