/**
 * Created by Simon on 13.06.2017.
 */

import Morris.Morris;

import java.util.Scanner;

public class UI {

    Morris board;
    String input;
    String from;
    String to;
    String remove;
    Scanner sc;

    public UI() {
        board = new Morris();
        Morris.class.;
        from = "";
        to = "";
        remove = "";
        input = "";
        sc = new Scanner(System.in);
    }

    public void play() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println(showOptions());
            checkInput();
        }
    }

    /**
     *
     *
     *
     *
     */
    public void checkInput() {
        boolean valid = true;
        MorrisMove move = new MorrisMove();
        if (board.getPhase() == 1) {
            System.out.println("choose position to set stone: ");
            input = sc.next();
            if (isValidMove()) {
                move.setTo(Integer.parseInt(input));
            } else {
                valid = false;
            }
        } else if (board.getPhase() == 2) {
            System.out.println("choose stone to move: ");
            input = sc.next();
            if (isValidMove()) {
                move.setFrom(Integer.parseInt(input));
            } else {
                valid = false;
            }
            System.out.println("choose location to set: ");
            input = sc.next();
            if (isValidMove()) {
                move.setTo(Integer.parseInt(input));
            } else {
                valid = false;
            }
            if(board.moveContainsRemove(move).count() > 0){
                System.out.print("choose opponent stone to remove: ");
                input = sc.next();
                if (isValidMove()){
                    move.setRemove(Integer.parseInt(input));
                }
                else{
                    valid = false;
                }
            }
        }
        // more phases toDo
        if (valid){
            board = (Morris)board.makeMove(move);
        }
        else{
            System.out.println("invalid move pls try again");
        }
    }

    public boolean isValidMove() {
        System.out.println(input);
        String regex = "^((?:[0-9]|1[0-9]|2[0-3])(?:\\.\\d{1,2})?|23?)$";

        if (input.matches(regex)){
            return true;
        } else {
            return false;
        }
    }

    public String showOptions() {
        String buffer = "";
        buffer += board.toString();
        buffer += "\n[0: Computer move, ?: Help]";
        return buffer;
    }

    public String start() {
        String buffer = "";
        buffer += "Make a move or let me start\n\n";
        buffer += showOptions();
        return buffer;

    }

}
