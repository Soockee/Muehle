import java.util.Scanner;

/**
 * Created by Simon on 13.06.2017.
 */
public class App {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Welcome to the board games application");
        System.out.println("Select the game: ");
        System.out.println("    <1>:  Nine mans Morris");
        System.out.println("    <2>:  TicTacToe");
        System.out.println("or type <exit> to leave the application");

        boolean success = false;
        while (!success){
            String selection = sc.next();
            selection = selection.trim();
            if (selection.equals("1")){
                UI uiNNM = new UI();
                uiNNM.play();
            }
            else if(selection.equals("2")){
                UIT3 uiT3 = new UIT3();
                uiT3.play();
            }
            else if (selection.equalsIgnoreCase("exit")){
                System.exit(0);
            }
            else{
                System.out.println("invalid command. Please try again");
            }
        }
    }
}
