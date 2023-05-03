import boardifier.control.StageFactory;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.view.View;
import control.BRBController;
import control.BRBDecider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class BrandubhConsole {
    public static final int HUMAN_VS_HUMAN = 0;
    public static final int HUMAN_VS_COMPUTER = 1;
    public static final int COMPUTER_VS_COMPUTER = 2;
    public static void main(String[] args) {
        int mode = chooseGameMode();
        switch (mode) {
            case HUMAN_VS_HUMAN:
                System.out.println("You chose a brother (human) vs brother (human) game!");
                break;
            case HUMAN_VS_COMPUTER:
                System.out.println("You chose a brother (human) vs God (computer) game!");
                break;
            case COMPUTER_VS_COMPUTER:
                System.out.println("You chose a God (computer) vs God (computer) game!");
                break;
        }
        Model model = new Model();
        if (mode == 0) {
            String player1 = getName();
            model.addHumanPlayer(player1);
            String player2 = getName();
            model.addHumanPlayer(player2);
        }
        else if (mode == 1) {
            String playerAI = getName();
            model.addHumanPlayer(playerAI);
            model.addComputerPlayer("Computer");
        }
        else if (mode == 2) {
            model.addComputerPlayer("Computer1");
            model.addComputerPlayer("Computer2");
        }

        StageFactory.registerModelAndView("BRB", "model.BRBStageModel", "view.BRBStageView");
        View BRBView = new View(model);
        BRBController control = new BRBController(model,BRBView);
        control.setFirstStageName("BRB");
        try {
            for (int i=0;i<1000;i++) {
                control.startGame();
                control.stageLoop();
            }
        }
        catch(GameException e) {
            System.out.println("Cannot start the game. Abort");
        }
        control.saveAllFiles();
    }

    /**
     * Get the name of the player from the standard input
     * @return
     **/
    static String getName() {
        String name = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Enter your name, warrior ! ");
        try {
            name = br.readLine();
        }
        catch(IOException e) {
            System.out.println("Don't take me for a fool, son. That's not your real name ye! Abort.");
        }
        return name;
    }

    public static int chooseGameMode() {
        System.out.println("Welcome, warrior, to Brandubh!");
        System.out.println("Say, son, what kind of game would you like to play?");
        System.out.println("Write " + HUMAN_VS_HUMAN + " for a brother (human) vs brother (human) game,");
        System.out.println("or, write " + HUMAN_VS_COMPUTER + " for a brother (human) vs God (computer) game!");
        System.out.println("or, write " + COMPUTER_VS_COMPUTER + " for a God (computer) vs God (computer) game!");

        BufferedReader stringReader = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                int mode = Integer.parseInt(stringReader.readLine());
                if (mode >= HUMAN_VS_HUMAN && mode <= COMPUTER_VS_COMPUTER) {
                    return mode;
                } else {
                    System.out.println("Arf. You've got to try again, son ! Invalid game mode. Please enter a number between " + HUMAN_VS_HUMAN + " and " + COMPUTER_VS_COMPUTER + ".");
                }
            } catch (IOException e) {
                System.out.println("There was an error reading your answer (mode). Try again, son.");
            } catch (NumberFormatException e) {
                System.out.println("Arf, that's an invalid input. Please enter a number, son.");
            }
        }
    }
}