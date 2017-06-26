import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Simon on 19.06.2017.
 */
public class UIT3 {

    T3 board;
    String in;
    Scanner sc;

    public UIT3() {
        board = new T3();
        in = "";
        sc = new Scanner(System.in);
    }

    public void play(){
        boolean isRunnin = true;
        while (isRunnin){
            showInteraction();
            board.streamMoves().forEach(System.out::println);
            if (!handleInput()){
                continue;
            }
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
    private boolean handleInput(){
        boolean success;
        String regexMove = "^([1-9])$";
        String regexForOptions = "^(0|save|exit|\\?||guide)";
        in = sc.next();
        if (in.matches(regexMove)){
            int move = Integer.parseInt(in);
            if (board.streamMoves().filter(m -> m == move).count() > 0){
                board = (T3)board.makeMove(move);
                success = true;
            }
            else{
                System.out.println("invalid move");
                success = false;
            }
        }
        else if (in.matches(regexForOptions)){
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
            success = true;
        }
        else{
            System.out.println("invalid command");
            success = false;
        }
        return false;
    }

    private void showInteraction(){
        String buffer ="";
        buffer+= board.toString();
        buffer += "\n[0: Computer move, ?: Help]";
        System.out.println(buffer);
    }

    public void showHelp(){
        String buffer = "\nYou can enter the following commands: \n";
        buffer+= "<exit> : Exit the Application\n";
        buffer+= "<save> : Saving the Game\n";
        buffer+= "<0> : AI is making the Move for you\n";
        buffer+= "<1-9>: Enter a number between 1 and 24 to make this move and follow the instructions afterwards\n";
        buffer+= "<guide>: A Gameguide which helps you to understand how the game works";
        System.out.println(buffer);
    }
    public void save(){
        //board.save(board,  "save.txt");
        System.out.println("Savefilepath: " + Paths.get("save.txt").toAbsolutePath().toString());
    }
    public void printGuide(){
        String buffer ="";
        buffer+= "t3 guide";
        System.out.println(buffer);
    }
    public void resetGame() {
        board = new T3();
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
}
