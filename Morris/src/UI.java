/*************************************************************
 * Created by Paul Krappatsch, Björn Franke, Simon Stockhause
 * Last edit: 29.06.2017
 ************************************************************/

import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
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
    private Morris board;
    private String in;
    private Scanner sc;
    private Ai ai;

    public UI() {
        board = new Morris();
        in = "";
        sc = new Scanner(System.in);
        ai = new Ai();
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
            checkGameOver();
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
        int phase = board.getPhase();
        if (phase == 1) {
            valid = movePhaseOne();
        } else {
            valid = movePhaseTwoToFive();
        }
        if (!valid) {
            System.out.println("Invalid move. Please try again");
            System.out.println("Following moves are possible: ");
            board.streamMoves().forEach(k-> System.out.print("["+k.toStringUser()+"]"+" "));
        }
    }

    public boolean movePhaseTwoToFive() {
        int to;
        Integer from = null;
        Integer remove = null;
        final Integer removeMove;
        final Integer fromMove;
        System.out.print("Enter Stone to move: ");
        in = sc.next();
        int movecheck = isValidMove();
        if (movecheck == 0) return true;
        if (movecheck == -1) {
            return false;
        }
        from = Integer.parseInt(in) - 1;

        System.out.print("Enter position to move stone: ");
        in = sc.next();
        movecheck = isValidMove();
        if (movecheck == 0) return true;
        if (movecheck == -1) {
            return false;
        }
        to = Integer.parseInt(in) - 1;
        Stream<MorrisMove> possibleMovesWithRemoves = board.streamMoves().filter(k -> k.getRemove().isPresent());
        Stream<MorrisMove> possibleMovesWithRemoves2 = board.streamMoves().filter(k -> k.getRemove().isPresent());
        if (possibleMovesWithRemoves2.anyMatch(k -> k.getTo() == to) && possibleMovesWithRemoves.count() > 0) {
            System.out.print("Enter stone to remove: ");
            in = sc.next();
            movecheck = isValidMove();
            if (movecheck == 0) return true;
            if (movecheck == -1) {
                return false;
            }
            remove = Integer.parseInt(in) - 1;
        }
        removeMove = remove;
        fromMove = from;
        if (remove != null && board.streamMoves().anyMatch(k -> k.equals(MorrisMove.moveOrJumpAndRemove(fromMove, to, removeMove)))) {
            board = board.makeMove(MorrisMove.moveOrJumpAndRemove(fromMove, to, removeMove)).get();
            return true;
        } else if (board.streamMoves().anyMatch(k -> k.equals(MorrisMove.moveOrJump(fromMove, to)))) {
            board = board.makeMove(MorrisMove.moveOrJump(fromMove, to)).get();
            return true;
        } else {
            return false;
        }

    }

    public boolean movePhaseOne() {
        int to;
        Integer remove = null;
        final Integer removeMove;
        System.out.print("Enter position to set stone: ");
        in = sc.next();
        int movecheck = isValidMove();
        if (movecheck == 0) return true;
        if (movecheck == -1) {
            return false;
        }
        to = Integer.parseInt(in) - 1;
        Stream<MorrisMove> possibleMovesWithRemoves = board.streamMoves().filter(k -> k.getRemove().isPresent());
        Stream<MorrisMove> possibleMovesWithRemoves2 = board.streamMoves().filter(k -> k.getRemove().isPresent());
        if (possibleMovesWithRemoves2.anyMatch(k -> k.getTo() == to) && possibleMovesWithRemoves.count() > 0) {
            System.out.print("Enter stone to remove: ");
            in = sc.next();
            movecheck = isValidMove();
            if (movecheck == 0) return true;
            if (movecheck == -1) {
                return false;
            }
            remove = Integer.parseInt(in) - 1;
        }
        removeMove = remove;
        if (remove != null && board.streamMoves().anyMatch(k -> k.equals(MorrisMove.placeAndRemove(to, removeMove)))) {
            board = board.makeMove(MorrisMove.placeAndRemove(to, removeMove)).get();
            return true;
        } else if (board.streamMoves().anyMatch(k -> k.equals(MorrisMove.place(to)))) {
            board = board.makeMove(MorrisMove.place(to)).get();
            return true;
        } else {
            return false;
        }
    }

    /***********************************************************************************
     * getValidMoves(Function<MorrisMove, Integer> f, int input):
     *      -> returns a stream containing valid moves, which match with the input
     *      -> the function determinates the filter (getTo, getFrom, getRemove)
     *
     **********************************************************************************/
    Stream<MorrisMove> getValidMoves(int input, int check) {
        if (check == 1) {
            return board.streamMoves().filter(morrisMove -> morrisMove.getFrom().get() == input);
        } else if (check == 2) {
            return board.streamMoves().filter(morrisMove -> morrisMove.getTo() == input);
        } else {
            return board.streamMoves().filter(morrisMove -> morrisMove.getRemove().get() == input);
        }
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
        String regex = "^((?:[1-9]|0[1-9]|1[0-9]|2[0-3])(?:\\.\\d{1,2})?|24?)$";
        String regexForOptions = "^(0|save|exit|\\?||guide||load||undo||flip||new)";
        in = in.trim();
        int res = -1;
        if (in.matches(regexForOptions)) {
            if (in.equals("0")) {
                board = (Morris) getBestMove().get();
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
                board = (Morris) board.parent();
            } else if (in.equalsIgnoreCase("flip")) {
                board = (Morris) board.flip();
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

    public void save() {
        String filenameMatcher = "^[\\w,\\s-]+\\.[A-Za-z]{3}$";
        System.out.print("Please enter the file e.g. <save.txt>: ");
        in = sc.next();
        try {
            if (in.matches(filenameMatcher)) board.save(board, in);
            System.out.println("Savefilepath: " + Paths.get(in).toAbsolutePath().toString());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void askSaveGame() {
        String regex = "^(y)|(n)$";
        System.out.println("\nDo you want to save the game? type: <y> / <n>");
        in = sc.next();
        in = in.toLowerCase();
        while (!in.matches(regex)) {
            System.out.println("invalid command. Please type: <y> or <n>");
            in = sc.next();
        }
        if (in.matches("y")) {
            save();
        }
    }

    public void showHelp() {
        String buffer = "\nYou can enter the following commands: \n";
        buffer += "<exit> : Exit the Application\n";
        buffer += "<save> : Saving the Game\n";
        buffer += "<0> : AI is making the Move for you\n";
        buffer += "<1-24>: Enter a number between 1 and 24 to make this move and follow the instructions afterwards\n";
        buffer += "<undo>: undo the last move\n";
        buffer += "<guide>: A Gameguide which helps you to understand how the game works\n";
        buffer += "<save>: saves the current game\n";
        buffer += "<load>: loads the file 'save.txt' in the current directory\n";
        buffer += "<flip>: flip causes to switch the symbols of the stones\n";
        buffer += "<new>: new game\n";
        buffer += "1  -  -  -  -  -  2  -  -  -  -  -  3\n";
        buffer += "|                 |                 |\n";
        buffer += "|     9  -  -  - 10  -  -  - 11     |\n";
        buffer += "|     |           |           |     |\n";
        buffer += "|     |    17  - 18  - 19     |     |\n";
        buffer += "8  - 16  - 24          20  - 12  -  4\n";
        buffer += "|     |    23  - 22  - 21     |     |\n";
        buffer += "|     |           |           |     |\n";
        buffer += "|    15  -  -  - 14  -  -  - 13     |\n";
        buffer += "|                 |                 |\n";
        buffer += "7  -  -  -  -  -  6  -  -  -  -  -  5\n";
        System.out.println(buffer);
    }

    public void printGuide() {
        String buffer = "";
        buffer += "Guide for Nine Mans Morris\n";
        buffer += "Play is in two phases. To begin with, player take turns to play a stone of their symbol on any unoccupied point until all eighteen pieces have been played\n";
        buffer += "After that, play continues alternately but each turn consist of moving a stone to an adjacent position\n";
        buffer += "If a player has less then 5 of his own stones, he is allowed to jump\n";
        buffer += "Jumping consists of selecting a stone and moving it to an unoccupied point\n";
        buffer += "Mills: a mill is formed by placing three stones in a row\n";
        buffer += "If a mill is completed, the player which owns the mill can remove a stone of his opponent as long as the stone is not part of a mill\n";
        buffer += "If all opponent stones are in a mill, these millstones can be removed aswell\n";
        buffer += "The player who achieves to reduce the opponent stones under 4 wins\n";

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

    public void checkGameOver() {
        if (board.isWin()) {
            String buffer = "";
            if (board.isBeginnersTurn()) {
                buffer += "O";
            } else {
                buffer += "X";
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
            System.out.println(board.toString());
        }
    }

    public Optional<StreamBoard> getBestMove() {
        StreamBoard res;
        Morris tmp = board.makeMove(board.streamMoves().findAny().get()).get();
        res = tmp;

        CompletableFuture<Boolean> userInterupt = CompletableFuture.supplyAsync(() -> {
            System.out.println("\nEnter any input to interrupt the search\n");
            sc.hasNext();
            return true;
        });

        CompletableFuture<StreamBoard> cf1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("Let me think about it");
            ai.evaluateBestBoard(board, 1);
            StreamBoard b = ai.getBestMove();
            return b;
        });
        CompletableFuture<StreamBoard> cf2 = cf1.thenComposeAsync((cf1Result) -> {
            CompletableFuture comfut = new CompletableFuture();
            System.out.println("What if i do...?");
            ai.evaluateBestBoard(board, 2);
            comfut.complete(ai.getBestMove());
            return comfut;
        });
        CompletableFuture<StreamBoard> cf3 = cf2.thenComposeAsync((cf2Result) -> {
            CompletableFuture comfut = new CompletableFuture();
            System.out.println("...or that?");
            ai.evaluateBestBoard(board, 3);
            comfut.complete(ai.getBestMove());
            return comfut;
        });
        CompletableFuture<StreamBoard> cf4 = cf3.thenComposeAsync((cf3Result) -> {
            CompletableFuture comfut = new CompletableFuture();
            System.out.println("This seems quite good");
            ai.evaluateBestBoard(board, 4);
            comfut.complete(ai.getBestMove());
            return comfut;
        });
        CompletableFuture<StreamBoard> cf5 = cf4.thenComposeAsync((cf4Result) -> {
            CompletableFuture comfut = new CompletableFuture();
            System.out.println("oh boy!");
            ai.evaluateBestBoard(board, 5);
            comfut.complete(ai.getBestMove());
            return comfut;
        });
        CompletableFuture<StreamBoard> cf6 = cf5.thenComposeAsync((cf5Result) -> {
            CompletableFuture comfut = new CompletableFuture();
            System.out.println("I need to think this through");
            ai.evaluateBestBoard(board, 6);
            comfut.complete(ai.getBestMove());
            return comfut;
        });
        CompletableFuture<StreamBoard> cf7 = cf6.thenComposeAsync((cf6Result) -> {
            CompletableFuture comfut = new CompletableFuture();
            System.out.println("This is going to be a masterful move!");
            ai.evaluateBestBoard(board, 7);
            comfut.complete(ai.getBestMove());
            return comfut;
        });
        while (!userInterupt.isDone() && !cf7.isDone()) {
            if (cf1.isDone()) res = cf1.join();
            if (cf2.isDone()) res = cf2.join();
            if (cf3.isDone()) res = cf3.join();
            if (cf4.isDone()) res = cf4.join();
            if (cf5.isDone()) res = cf5.join();
            if (cf6.isDone()) res = cf6.join();
            if (cf7.isDone()) {
                res = cf7.join();
                System.out.println("I am done searching!");
                userInterupt.cancel(true);
            }
        }
        try {
            if (cf1.cancel(true)) ;
            else if (cf2.cancel(true)) ;
            else if (cf3.cancel(true)) ;
            else if (cf4.cancel(true)) ;
            else if (cf5.cancel(true)) ;
            else if (cf6.cancel(true)) ;
            else if (cf7.cancel(true)) ;
            userInterupt.cancel(true);
        } catch (java.util.concurrent.CancellationException ce) {
            System.out.println("some cancel error with CompletableFutures");
        }
        sc = new Scanner(System.in);
        return Optional.of(res);
    }
}