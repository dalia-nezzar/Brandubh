import boardifier.control.StageFactory;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.view.View;
import control.BRBController;
import control.BRBDecider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;
import java.util.Date;

import static boardifier.view.ConsoleColor.*;


public class BrandubhConsole {
    public static final int HUMAN_VS_HUMAN = 0;
    public static final int HUMAN_VS_COMPUTER = 1;
    public static final int COMPUTER_VS_COMPUTER = 2;
    Scanner input = new Scanner(System.in);
    public static void main(String[] args) {
        int mode = chooseGameMode();
        switch (mode) {
            case HUMAN_VS_HUMAN:
                System.out.println("You chose a"+ BLACK_BOLD +" brother (human) vs brother (human) "+ BLACK +"game!");
                break;
            case HUMAN_VS_COMPUTER:
                System.out.println("You chose a"+ BLACK_BOLD +" brother (human) vs God (computer) "+ BLACK +"game!");
                break;
            case COMPUTER_VS_COMPUTER:
                System.out.println("You chose a"+ BLACK_BOLD +" God (computer) vs God (computer) "+ BLACK +"game!");
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
        System.out.println(BLACK_BOLD+"Welcome, warrior, to Brandubh!");
        System.out.println("Oh! Say, son, do you know"+ PURPLE_BRIGHT+ " the rules of the Brandubh?"+BLACK);
        String answer = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                answer = br.readLine();
                if (answer.equals("yes")
                || answer.equals("y")
                || answer.equals("yeah")
                || answer.equals("yep")
                || answer.equals("yup")
                || answer.equals("ye")
                || answer.equals("yee")
                || answer.equals("oui")) {
                    System.out.println("Fair. Let's head to the warzone, son!");
                    break;
                } else if (answer.equals("no")
                || answer.equals("n")
                || answer.equals("nope")
                || answer.equals("nah")
                || answer.equals("non")
                || answer.equals("noo")) {
                    System.out.println("Oh, that's too bad, son. Let me explain them to you then.");
                    rulesBrandubh();
                    break;
                } else {
                    System.out.println("Arf. You've got to try again, son ! Please answer yes or no.");
                }
            } catch (IOException e) {
                System.out.println("There was an error reading your answer (rules). Try again, son.");
            }
        }
        System.out.println(BLUE_BOLD + "Speak, son, what kind of game would you like to play?" + BLACK);
        System.out.println("Write " + RED_BOLD +HUMAN_VS_HUMAN + BLACK+" for a brother (human) vs brother (human) war,");
        System.out.println("or, write "+ RED_BOLD + HUMAN_VS_COMPUTER + BLACK+" for a brother (human) vs God (computer) war!");
        System.out.println("or, write " + RED_BOLD + COMPUTER_VS_COMPUTER + BLACK+" for a God (computer) vs God (computer) war!");

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

    static void rulesBrandubh(){
        try{
            System.out.println("Brandubh is a game born from our ancestors, warrior.");
            Thread.sleep(2000);
            System.out.println("A "+RED_BOLD+"WAR"+BLACK+", was declared on this 7x7 territory. You can either"+ GREEN_BOLD+" DEFEND"+BLACK+" your Branan or"+ RED_BOLD+" ATTACK"+BLACK +" the enemy's Branan.");
            Thread.sleep(5000);
            System.out.println("BUT, beware... If you wish to defend the Branan, you and your brothers will only be "+ GREEN_BOLD+"four"+BLACK+", and your enemy will possess"+ BLACK+ RED_BOLD+" eight "+BLACK +"warriors!");
            Thread.sleep(3000);
            System.out.println("The end of this war is proclaimed if "+ RED_UNDERLINED+"the King gets captured between two enemy warriors"+BLACK+", or if "+GREEN_UNDERLINED+"the King gets to one of the corners of the board."+BLACK);
            Thread.sleep(5000);
            System.out.println("To capture an enemy, you'll have to surround one of them between "+ PURPLE_BOLD+"two of your warriors. "+BLACK+ BLACK_UNDERLINED+"If the enemy enters volonteeringly between two of your warriors, he will NOT be captured."+BLACK);
            Thread.sleep(8000);
            System.out.println(PURPLE_BOLD+"The corners and the center of the board are unaccessible for all the warriors."+BLACK+" "+ GREEN_BOLD+"Only the King can get there."+BLACK);
            Thread.sleep(3000);
            System.out.println("And if the King steps out of the center, his throne, he can" +GREEN_BOLD+" no longer return to it.");
            System.out.println(BLACK_BOLD+"============= END OF THE RULES =============");
            System.out.println("");
        }
        catch(InterruptedException e){
            System.out.println("There was an error reading the rules. Try again, son.");
        }
    }
}