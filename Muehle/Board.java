package Muehle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Created by xXThermalXx on 10.06.2017.
 */
public class Board implements MutableBoard<Integer> {

    private int[] players;
    private int[] stones;
    private int turn;
    private List<Integer> history;
    private boolean flipped;
    private int[][] hashCodes;


    public Board() {
        /*
            - Anmerkungen zu Attributen
                - Das players-Array enthält für jeden Spieler einen 24 bit langen int-Wert.
                - Jeder Spieler hat zu Beginn des Spieles 9 Spielsteine, welche im stones-Array gespeichert werden
                - Die Variable turn ermöglicht es den Spieler zu ermitteln der grade am zug ist
                - In der List history werden alle vergangen Züge gespeichert
                - Die Variable flipped speichert, ob der Benutzer die Spielsteine vertauschen möchte
                - new Comment
         */
        players = new int[]{0b0, 0b0};
        stones = new int[]{9, 9};
        turn = 0;
        history = new ArrayList<>();
        flipped = false;
    }//Constructor

    @Override
    public void makeMove(Integer integer) {
        //TO-DO
    }

    @Override
    public void undoMove() {
        //TO-DO
    }

    @Override
    public List<Integer> moves() {
        //TO-DO
        return null;
    }

    @Override
    public List<Integer> getHistory() {
        return history;
    }//getHistory

    @Override
    public boolean isWin() {
        if (stones[turn & 1] == 0) {
            int winSum = IntStream.range(0, 24).filter(i -> (players[(turn & 1)] & (1 << i)) > 0).toArray().length;
            return (winSum < 3) ? true : false;
        }//if
        return false;
    }

    @Override
    public boolean isDraw() {
        //TO-DO
        return false;
    }

    @Override
    public void flip() {
        //Wechsel der besetzten Felder
        int dummy = players[0];
        players[0] = players[1];
        players[1] = dummy;
        //Wechsel der Spielsteine
        dummy = stones[0];
        stones[0] = stones[1];
        stones[1] = dummy;
        //flipped erhält eine neue Zuweisung
        flipped=!flipped;
    }//flip

    @Override
    public boolean isFlipped() {
        return flipped;
    }

    @Override
    public void load(String name) {
        //TO-DO
    }

    @Override
    public void load(Path path) {
        //TO-DO
    }

    @Override
    public void save(String name) {
        //TO-DO
    }

    @Override
    public void save(Path path) {
        //TO-DO
    }
}//class
