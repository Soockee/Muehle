import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by Björn Franke on 28.06.2017.
 * Die Klasse enthält den Programmteil der Threadkommunikation, der in Simons Ui Klasse gehört
 */
public class Test {

    public Optional<ImmutableBoard> getBestMove(ImmutableBoard board) {

        AiThread ki = new AiThread(board, 4);
        CollectThread collector = new CollectThread(10, ki);

        boolean active = true;
        Scanner sc = new Scanner(System.in);

        while (collector.isAlive() && active) {
            System.out.print("Drücken sie eine beliebige Taste um den Suchvorgang abzubrechen: ");
            String eingabe = sc.nextLine();
            if (!eingabe.equals("")) {
                active = false;
            }
        }

        Optional<ImmutableBoard> bestBoard = Optional.of(collector.getBestBoard());

        if (!bestBoard.isPresent()) {
            bestBoard = board.childs().findAny();
        }

        return bestBoard;
    }//getBestMove
}
