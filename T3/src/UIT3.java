import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Created by Simon on 19.06.2017.
 */
public class UIT3 implements UIInterface{

    T3 board;
    String in;
    Scanner sc;
    Ai ai;

    public UIT3() {
        board = new T3();
        in = "";
        sc = new Scanner(System.in);
        ai = new Ai(200);
    }

    public void play(){
        boolean isRunnin = true;
        while (isRunnin){
            System.out.println(printInteraction());
            checkInput();
            checkGameOver();
        }
    }
    @Override
    public void checkInput(){
        int movecheck = isValidMove();
        if (movecheck == 1){
            board = board.makeMove(Integer.parseInt(in)-1).get();
        }
        else if(movecheck == -1){
            System.out.println("Invalid command");
        }
    }


    public String printInteraction(){
        String buffer ="";
        buffer+= board.toString();
        buffer += "\n[0: Computer move, ?: Help]";
        return buffer;
    }

    public void showHelp(){
        String buffer = "\nYou can enter the following commands: \n";
        buffer += "<exit> : Exit the Application\n";
        buffer += "<save> : Saving the Game\n";
        buffer += "<0> : AI is making the Move for you\n";
        buffer+= "<1-9>: Enter a number between 1 and 24 to make this move and follow the instructions afterwards\n";
        buffer += "<undo>: undo the last move\n";
        buffer += "<guide>: A Gameguide which helps you to understand how the game works\n";
        buffer += "<save>: saves the current game\n";
        buffer += "<load>: loads the file 'save.txt' in the current directory\n";
        buffer += "<flip>: flip causes to switch the symbols of the stones\n";
        buffer += "<new>: new game\n";
        buffer+= "<guide>: A Gameguide which helps you to understand how the game works\n";
        buffer+= "Board Layout: \n";
        buffer+= "1 2 3\n";
        buffer+= "3 5 6\n";
        buffer+= "7 8 9\n";
        System.out.println(buffer);
    }
    public void save(){
        try{
            board.save(board,  "save.txt");
            System.out.println("Savefilepath: " + Paths.get("save.txt").toAbsolutePath().toString());
        }
        catch (IOException e){
            System.out.println(e.getMessage());
        }
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
        String regex = "^(y)|(n)$";
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

    @Override
    public void loadFile() {
        try {
            String filenameMatcher = "^[\\w,\\s-]+\\.[A-Za-z]{3}$";
            System.out.print("Please enter the file e.g. <save.txt>: ");
            in = sc.next();
            if (in.matches(filenameMatcher)) {
                board = board.load(in);
            } else {
                System.out.println("invalid filename");
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        if (board.isWin() || board.isDraw()) {
            String regex = "^(y)|(n)$";
            System.out.println(board.toString());
            System.out.println("\nDo you want to undo a move?? type: <y> / <n>");
            in = sc.next();
            in = in.toLowerCase();
            while (!in.matches(regex)) {
                System.out.println("invalid command. Please type: <y> or <n>");
                in = sc.next();
            }
            if (in.equalsIgnoreCase("y"))board = (T3)board.parent();
        }
    }

    @Override
    public int isValidMove() {
        String regex = "^((?:[1-9]|0[1-9]|1[0-9]|2[0-3])(?:\\.\\d{1,2})?|24?)$";
        String regexForOptions = "^(0|save|exit|\\?||guide||load||undo||flip||new)";
        in = in.trim();
        int res = -1;
        in = sc.next();
        if (in.matches(regexForOptions)) {
            if (in.equals("0")) {
                board = (T3) getBestMove();
            } else if (in.equalsIgnoreCase("save")) {
                save();
            } else if (in.equalsIgnoreCase("exit")) {
                try {
                    System.in.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
                System.exit(0);

            } else if (in.equals("?")) {
                showHelp();
            } else if (in.equalsIgnoreCase("guide")) {
                printGuide();
            } else if (in.equalsIgnoreCase("load")) {
                loadFile();
            } else if (in.equalsIgnoreCase("undo")) {
                board = (T3) board.parent();
            } else if (in.equalsIgnoreCase("flip")) {
                board = (T3) board.flip();
                System.out.println(board.isFlipped());
            } else if (in.equalsIgnoreCase("new")) {
                resetGame();
            }
            res = 0;
        } else if (in.matches(regex)) {
            res = 1;
        }
        return res;
    }

    @Override
    public void checkGameOver() {
        if (board.isWin()) {
            String buffer = "";
            if (board.isBeginnersTurn()) {
                if (board.isFlipped())buffer+= "X";
                else{
                    buffer+= "O";
                }
            } else {
                if (board.isFlipped())buffer+= "O";
                else{
                    buffer+= "X";
                }
            }
            buffer += " Won!";
            System.out.println(buffer);
            System.out.println("Endgame: ");
            System.out.println(board.toString());
            askSaveGame();
            resetGame();
        }
        if (board.isDraw()) {
            System.out.println("The game ended in a Draw!");
            System.out.println("Endgame: ");
            System.out.println(board.toString());
            askSaveGame();
            resetGame();
        }
    }
    @Override
    public StreamBoard getBestMove() {
        //pretty difficult AI: 200 montecarlo & searchdepth = 5
        ai.evaluateBestBoard(board,5);
        StreamBoard b = ai.getBestMove();
        return b;


        //---CompletableFuture Solution ---//
        /*StreamBoard res;
        T3 tmp = board.makeMove(board.streamMoves().findAny().getAsInt()).get();
        res = tmp;
        CompletableFuture<StreamBoard> cf1 = CompletableFuture.supplyAsync(() -> {
            ai.evaluateBestBoard(board,4);
            StreamBoard b = ai.getBestMove();
            return b;
        });
        try{
            res =  cf1.get();
            System.out.println("I moved to ... "+ res.getMove().get());
        }
        catch(InterruptedException iE){
            System.out.println(iE.getMessage());
        }
        catch(java.util.concurrent.ExecutionException iE){
            System.out.println(iE.getMessage());
        }
        return res;*/
    }
}
