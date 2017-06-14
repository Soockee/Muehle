/*************************************************************
 * Created by Paul Krappatsch, Björn Franke, Simon Stockhause
 * Last edit: 14.06.2017
 ************************************************************/
import java.util.Scanner;
import java.util.function.Function;
import java.util.stream.Stream;

public class UI {

    /**********************************************************
     *  Fields:
     *      board:
     *          -> current board: contains all the intelligence
     *      in:
     *          ->input of user
     *      sc:
     *          -> Scanner to get user inputs from terminal
     *      
     **********************************************************/
    Morris board;
    String in;
    String from;
    String to;
    String remove;
    Scanner sc;

    public UI() {
        board = new Morris();
        in = "";
        sc = new Scanner(System.in);
    }
    /**********************************************************
     *   play():
     *      ->Handles win and draw Situation
     *      ->checkInput()
     *          => handles the communication with the user
     *      ->resets the game in case of GameOver
     **********************************************************/
    public void play() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println(showOptions());
            checkInput();
            if (board.isWin()) {
                String buffer = "";
                int counter = board.getHistory().size();
                if (board.isBeginnersTurn()) {
                    buffer += "O";
                } else {
                    buffer += "X";
                }
                buffer += " Won!";
                System.out.println(buffer);
                //here possibility to save
                resetGame();
            }
            if (board.isDraw()) {
                System.out.println("The game ended in a Draw!");
                //here possibility to save
                resetGame();
            }
        }
    }

    /**************************************************
     *  checkInput():
     *      ->Builds up a MorrisMove there are three cases:
     *          => Placing phase:
     *              -> user declares his input (position he wants to place his stone)
     *              -> in case of a mill:
     *                  => user declares the opponent stone he wants to remove
     *          => Moving phase:
     *              -> user declares the stones he wants to move
     *              -> user declares the position he wants to move his selected stone
     *              -> in case of mill:
     *                  => user declares the opponent stone he wants to remove
     *          =>Jumping phase:
     *              -> toDo
     *      ->In case of an invalid move:
     *          => give the information the user
     *          => let him do the input process again
     *
     *
     **************************************************/
    public void checkInput() {
        boolean valid = true;
        MorrisMove move = new MorrisMove();
        Stream<MorrisMove> possibleMoves;
        if (board.streamMoves().filter(k -> k.getFrom() != -1).count() > 0) {
            System.out.print("Select stone to move: ");
            in = sc.next();
            if (!isValidMove()){
                System.out.println("Invalid move. Please try again");
                return;
            }
            move.setFrom(Integer.parseInt(in)-1);
            possibleMoves = getValidMoves(MorrisMove::getFrom, Integer.parseInt(in)-1);
        }
        System.out.print("Select location to set stone: ");
        in = sc.next();
        if (!isValidMove()){
            System.out.println("Invalid move. Please try again");
            return;
        }
        move.setTo(Integer.parseInt(in)-1);
        possibleMoves = getValidMoves(MorrisMove::getTo, Integer.parseInt(in)-1);
        possibleMoves = possibleMoves.filter(k -> k.getRemove() != -1);
        if (possibleMoves.count() > 0) {
            System.out.print("Select stone to Remove: ");
            in = sc.next();
            if (!isValidMove()){
                System.out.println("Invalid move. Please try again");
                return;
            }
            move.setRemove(Integer.parseInt(in)-1);

            possibleMoves = getValidMoves(MorrisMove::getRemove, Integer.parseInt(in)-1);
        }
        if (!board.streamMoves().anyMatch(k -> k.equals(move))) valid = false;
        if (valid) {
            board = (Morris) board.makeMove(move);
        } else {
            System.out.println("Invalid move. Please try again");
        }
    }

    /***********************************************************************************
     * getValidMoves(Function<MorrisMove, Integer> f, int input):
     *      -> returns a stream containing valid moves, which match with the input
     *      -> the function determinates the filter (getTo, getFrom, getRemove)
     *
     **********************************************************************************/
    Stream<MorrisMove> getValidMoves(Function<MorrisMove, Integer> f, int input) {
        return board.streamMoves().filter(morrisMove -> f.apply(morrisMove) == input);
    }
    /***********************************************************************************
     *  showOptions():
     *      ->returns a String which contains the Board representation and basic user commands
     *
     **********************************************************************************/
    public String showOptions() {
        String buffer = "";
        buffer += board.toString();
        buffer += "\n[0: Computer move, ?: Help]";
        return buffer;
    }
    /***********************************************************************************
     * isValidMove():
     *      ->checks if the input (should be a number) is valid (1-24 are valid inputs)
     *
     **********************************************************************************/
    public boolean isValidMove() {
        String regex = "^((?:[1-9]|1[0-9]|2[0-3])(?:\\.\\d{1,2})?|24?)$";
        if (in.matches(regex)){
            return true;
        } else {
            return false;
        }
    }
    /***********************************************************************************
     * resetGame():
     *      -> creates a Morris instance and overwrites it with the finished board
     *
     **********************************************************************************/

    public void resetGame() {
        board = new Morris();
    }
}