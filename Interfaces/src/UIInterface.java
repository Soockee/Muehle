/**
 * Created by Simon on 30.06.2017.
 */

import java.util.Optional;

public interface UIInterface {

    void play();
    void checkInput();
    String printInteraction();
    int isValidMove();
    void save();
    void askSaveGame();
    void showHelp();
    void printGuide();
    void resetGame();
    void checkGameOver();
    void loadFile();
    StreamBoard getBestMove();
}
