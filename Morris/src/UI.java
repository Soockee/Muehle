/*************************************************************
 * Created by Paul Krappatsch, Björn Franke, Simon Stockhause
 * Last edit: 14.06.2017
 ************************************************************/
import java.nio.file.Paths;
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
     **********************************************************/
    Morris board;
    String in;
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
            System.out.println(printInteraction());
            checkInput();
            if (board.isWin()) {
                String buffer = "";
                if (board.isBeginnersTurn()) {
                    buffer += "O";
                } else {
                    buffer += "X";
                }
                buffer += " Won!";
                System.out.println(buffer);
                askSaveGame();
                resetGame();
            }
            if (board.isDraw()) {
                System.out.println("The game ended in a Draw!");
                askSaveGame();
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
        int moveCheck = -1;
        if (board.streamMoves().filter(k -> k.getFrom() != -1).count() > 0) {
            System.out.print("Select stone to move: ");
            in = sc.next();
            moveCheck = isValidMove();
            if (moveCheck == -1){
                System.out.println("Invalid move. Please try again");
                return;
            }
            else if (moveCheck == 0)return;
            move.setFrom(Integer.parseInt(in)-1);
            possibleMoves = getValidMoves(MorrisMove::getFrom, Integer.parseInt(in)-1);
        }
        System.out.print("Select location to set stone: ");
        in = sc.next();
        moveCheck = isValidMove();
        if (moveCheck == -1){
            System.out.println("Invalid move. Please try again");
            return;
        }
        else if (moveCheck == 0)return;
        move.setTo(Integer.parseInt(in)-1);
        possibleMoves = getValidMoves(MorrisMove::getTo, Integer.parseInt(in)-1);
        possibleMoves = possibleMoves.filter(k -> k.getRemove() != -1);
        if (possibleMoves.count() > 0) {
            System.out.print("Select stone to Remove: ");
            in = sc.next();
            moveCheck = isValidMove();
            if (moveCheck == -1){
                System.out.println("Invalid move. Please try again");
                return;
            }
            else if (moveCheck == 0)return;
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
    public String printInteraction() {
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
    public int isValidMove() {
        String regex = "^((?:[1-9]|1[0-9]|2[0-3])(?:\\.\\d{1,2})?|24?)$";
        String regexForOptions = "^(0|save|exit|\\?||guide)";
        in = in.trim();
        int res = -1;
        if (in.matches(regexForOptions)){
            if (in.equals("0")){
                //computer move
            }
            else if(in.equalsIgnoreCase("save")){
                save();
            }
            else if(in.equalsIgnoreCase("exit")){
                System.exit(0);

            }
            else if(in.equals("?")){
                showHelp();
            }
            else if(in.equalsIgnoreCase("guide")){
                printGuide();
            }
            res = 0;
        }
        else if (in.matches(regex)){
            res = 1;
        }
        return res;
    }

    public void save(){
            //board.save(board,  "save.txt");
            System.out.println("Savefilepath: " + Paths.get("save.txt").toAbsolutePath().toString());
    }
    public void askSaveGame(){
        String regex = "^(y)| (n)$";
        System.out.println("\nDo you want to save the game? type: <y> / <n>");
        in = sc.next();
        in = in.toLowerCase();
        while (!in.matches(regex)){
            System.out.println("invalid command. Please type: <y> or <n>");
            in = sc.next();
        }
        if (in.matches("y")){
            save();
        }
    }

    public void showHelp(){
        String buffer = "\nYou can enter the following commands: \n";
        buffer+= "<exit> : Exit the Application\n";
        buffer+= "<save> : Saving the Game\n";
        buffer+= "<0> : AI is making the Move for you\n";
        buffer+= "<1-24>: Enter a number between 1 and 24 to make this move and follow the instructions afterwards\n";
        buffer+= "<guide>: A Gameguide which helps you to understand how the game works";
        System.out.println(buffer);
    }
    public void printGuide(){
        String buffer ="";
        buffer+= "Guide for Nine Mans Morris\n";
        buffer+= "Play is in two phases. To begin with, player take turns to play a stone of their symbol on any unoccupied point until all eighteen pieces have been played\n";
        buffer+= "After that, play continues alternately but each turn consist of moving a stone to an adjacent position\n";
        buffer+= "If a player has less then 5 of his own stones, he is allowed to jump\n";
        buffer+= "Jumping consists of selecting a stone and moving it to an unoccupied point\n";
        buffer+= "Mills: a mill is formed by placing three stones in a row\n";
        buffer+= "If a mill is completed, the player which owns the mill can remove a stone of his opponent as long as the stone is not part of a mill\n";
        buffer+= "If all opponent stones are in a mill, these millstones can be removed aswell\n";
        buffer+= "The player who achieves to reduce the opponent stones under 4 wins\n";
        System.out.println(buffer);
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