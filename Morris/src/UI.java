/**
 * Created by Simon on 13.06.2017.
 */
import java.util.Scanner;
import java.util.stream.Stream;

public class UI {

    Morris board;
    String input;
    String from;
    String to;
    String remove;
    Scanner sc;

    public UI() {
        board = new Morris();
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
        int in;
        Stream<MorrisMove> poss;
        if (board.streamMoves().filter(k->k.getFrom()!=-1).count()>0){
            System.out.print("select stone to move: ");
            in = sc.nextInt();
            move.setFrom(in);
            poss = getValidMoves(1,in);
        }
        System.out.print("select location to set stone: ");
        in = sc.nextInt();
        move.setTo(in);
        poss = getValidMoves(2,in);
        poss = poss.filter(k->k.getRemove()!=-1);
        if (poss.count()>0){
            System.out.print("select stone to Remove: ");
            in = sc.nextInt();
            move.setRemove(in);
            poss = getValidMoves(3,in);
        }
        // more phases toDo
        if (!board.streamMoves().anyMatch(k->k.equals(move)))valid = false;
        if (valid){
            board = (Morris)board.makeMove(move);
        }
        else{
            System.out.println("invalid move pls try again");
        }
    }
    Stream<MorrisMove> getValidMoves(int k , int input) {
        Stream<MorrisMove> stream = board.streamMoves();
        if (k == 1){
            return stream.filter(morrisMove -> morrisMove.getFrom() == input);
        }
        else if (k == 2){
            return stream.filter(morrisMove -> morrisMove.getTo() == input);
        }
        else if( k ==3){
            return stream.filter(morrisMove -> morrisMove.getRemove() == input);
        }
        return null;
    }
    public boolean isValidMove() {
        String regex = "^((?:[1-9]|1[0-9]|2[0-3])(?:\\.\\d{1,2})?|24?)$";
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
}