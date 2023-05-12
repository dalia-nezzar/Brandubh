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
import java.io.*;

import static boardifier.view.ConsoleColor.*;


public class BrandubhConsole {
    public static final int HUMAN_VS_HUMAN = 0;
    public static final int HUMAN_VS_COMPUTER = 1;
    public static final int COMPUTER_VS_COMPUTER = 2;
    public static Scanner input = new Scanner(System.in);

    private static PrintStream originalOut = System.out;
    private static boolean suppressOutput = true;
    // Override the default print stream
    private static class NullPrintStream extends PrintStream {
        public NullPrintStream() {
            super(new OutputStream() {
                public void write(int b) {
                    // Do nothing
                }
            });
        }
    }

    // Override the standard output stream
    private static void setOutputStream(PrintStream out) {
        System.setOut(out);
        System.setErr(out);
    }

    // Toggle output suppression
    private static void toggleOutput() {
        if (suppressOutput) {
            setOutputStream(new NullPrintStream());
            suppressOutput = false;
        } else {
            setOutputStream(originalOut);
            suppressOutput = true;
        }
    }
    public static void main(String[] args) {
        // Suppress output
        //toggleOutput();
        int choice=-1;
        if (args.length == 1) {
            try {
                choice = Integer.parseInt(args[0]);
                if ((choice <0) || (choice>2)) choice = -1;
            }
            catch(NumberFormatException e) {
                choice = -1;
            }
        }
        int mode=0;
        Model model = new Model();
        if (choice==-1){
            mode=chooseGameMode();
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
            if (mode == 0) {
                System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
                String player1 = getName();
                model.addHumanPlayer(player1);
                System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
                String player2 = getName();
                if (player1.toLowerCase().equals(player2.toLowerCase())) {
                    System.out.println(RED_BOLD + "You can't have two warriors with the same name!" + BLACK);
                    do {
                        System.out.println(BLACK_BOLD + "Enter a new name for the second player, son! Else... The war will never start! " + BLACK);
                        player2 = getName();
                    }
                    while (player1.toLowerCase().equals(player2.toLowerCase()));
                }
                model.addHumanPlayer(player2);
            }
            else if (mode == 1) {
                System.out.println("What role do you want to play in the war, brother?");
                String answer = "";
                //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                System.out.println(PURPLE_BOLD+"1. Defender"+BLACK);
                System.out.println(PURPLE_BOLD+"2. Attacker"+BLACK);
                try {
                    //TODO add exception if the user enters a number bigger than 2 and less than 1
                    answer = input.nextLine();
                } catch (Exception e) {
                    System.out.println("Error while reading your answer. Please try again.");
                }
                if (answer.equals("1")){
                    System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
                    String player1 = getName();
                    model.addHumanPlayer(player1);
                    System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
                    String AI1Mode1=setAI();
                    model.addComputerPlayer(AI1Mode1);
                }
                else if (answer.equals("2")){
                    System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
                    String AI1Mode1=setAI();
                    model.addComputerPlayer(AI1Mode1);
                    System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
                    String player1 = getName();
                    model.addHumanPlayer(player1);
                }
            }
            else if (mode == 2) {
                System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
                String AI1Mode2=setAI();
                model.addComputerPlayer(AI1Mode2);
                System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
                String AI2Mode2=setAI();
                model.addComputerPlayer(AI2Mode2);
            }
        }

        StageFactory.registerModelAndView("BRB", "model.BRBStageModel", "view.BRBStageView");
        View BRBView = new View(model);
        BRBController control = new BRBController(model,BRBView);
        control.setFirstStageName("BRB");
        int nbParties=setNumberGame();
        //toggleOutput();
        try {
            for (int i=0;i<nbParties;i++) {
                control.startGame();
                control.stageLoop();
                //every 1000 games, we save the files
                if (mode==2 && (i+1)%1000 == 0) {
                    control.saveAllFiles();
                    //toggleOutput();
                    System.out.println("Files saved");
                    //toggleOutput();
                }
            }
        }
        catch(GameException e) {
            System.out.println("Cannot start the war. Abort");
        }
        //if (mode == 2) control.saveAllFiles();
    }

    /**
     * Get the name of the player from the standard input
     * @return The name of the player
     **/
    static String getName() {
        String name = "";
        System.out.println("Enter your name, warrior! ");
        name = input.nextLine();
        if (name.equals("")) {
            System.out.println("You must enter a name, son! Try again.");
            name = getName();
        }
        return name;
    }

    public static String setAI() {
        int ai = 0;
        System.out.println("Choose your opponent, warrior! Will it be God Odin or God Loki?");
        System.out.println(PURPLE_BOLD+"1. God Odin"+BLACK);
        System.out.println(GREEN_BOLD+"2. God Loki"+BLACK);
        do{
            ai=input.nextInt();
            if (ai!=1 && ai!=2) System.out.println("Don't take me for a fool, son. That's not a real God! Try again.");
        } while(ai!=1 && ai!=2);
        if (ai==1){
            BRBController.typeAI=2;
            return "God Odin";
        }
        else if(ai==2) {
            BRBController.typeAI=3;
            return "God Loki";
        }
        else{
            System.out.println("Don't take me for a fool, son. That's not a real God! Abort.");
            return "God Loki";
        }
    }

    public static int chooseGameMode() {
        System.out.println(BLACK_BOLD+"Welcome, warrior, to Brandubh!");
        System.out.println("Oh! Say, son, do you know"+ PURPLE_BRIGHT+ " the rules of the Brandubh?"+BLACK);
        String answer = "";
        while (true) {
            try {
                answer = input.nextLine();
                if (answer.toLowerCase().equals("yes")
                || answer.toLowerCase().equals("y")
                || answer.toLowerCase().equals("yeah")
                || answer.toLowerCase().equals("yep")
                || answer.toLowerCase().equals("yup")
                || answer.toLowerCase().equals("ye")
                || answer.toLowerCase().equals("yee")
                || answer.toLowerCase().equals("oui")
                || answer.toLowerCase().equals("1")) {
                    System.out.println("Fair. Let's head to the warzone, ey!");
                    System.out.println("=======================================");
                    break;
                } else if (answer.toLowerCase().equals("no")
                || answer.toLowerCase().equals("n")
                || answer.toLowerCase().equals("nope")
                || answer.toLowerCase().equals("nah")
                || answer.toLowerCase().equals("non")
                || answer.toLowerCase().equals("noo")
                || answer.toLowerCase().equals("0")) {
                    System.out.println("Oh, that's too bad, son. Let me explain them to you then.");
                    rulesBrandubh();
                    break;
                } else {
                    System.out.println("Arf. You've got to try again, son ! Please answer yes or no.");
                }
            } catch (Exception e) {
                System.out.println("Don't take me for a fool, son. That's not a real answer! Abort.");
            }
        }
        System.out.println(BLUE_BOLD + "Speak, son, what kind of war would you like to engage yourself in?" + BLACK);
        System.out.println("Write " + RED_BOLD +HUMAN_VS_HUMAN + BLACK+" for a brother (human) vs brother (human) war,");
        System.out.println("or, write "+ RED_BOLD + HUMAN_VS_COMPUTER + BLACK+" for a brother (human) vs God (computer) war!");
        System.out.println("or, write " + RED_BOLD + COMPUTER_VS_COMPUTER + BLACK+" for a God (computer) vs God (computer) war!");

        //BufferedReader stringReader = new BufferedReader(new InputStreamReader(System.in));
        int mode = 0;
        while (true) {
            try {
                mode = Integer.parseInt(input.nextLine());
                System.out.println("You chose " + mode + "!");
                if (mode >= HUMAN_VS_HUMAN && mode <= COMPUTER_VS_COMPUTER) {
                    return mode;
                } else {
                    System.out.println("Arf. You've got to try again, son ! Invalid game mode. Please enter a number between " + HUMAN_VS_HUMAN + " and " + COMPUTER_VS_COMPUTER + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("You chose " + mode + "!");
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
            System.out.println("BUT, beware... If you wish to defend the Branan, you and your brothers will only be "+ GREEN_BOLD+"four"+BLACK+", while your enemy will possess"+ BLACK+ RED_BOLD+" eight "+BLACK +"warriors!");
            Thread.sleep(3000);
            System.out.println("The end of this war is proclaimed if "+ RED_UNDERLINED+"the Branan gets captured between two enemy warriors"+BLACK+", or if "+GREEN_UNDERLINED+"the Branan gets to one of the corners of the board."+BLACK);
            Thread.sleep(5000);
            System.out.println("To capture an enemy, you'll have to surround one of them between "+ PURPLE_BOLD+"two of your warriors. "+BLACK+ BLACK_UNDERLINED+"If one enemy soldier enters volonteeringly between two of your warriors, he will NOT be captured."+BLACK);
            Thread.sleep(8000);
            System.out.println(PURPLE_BOLD+"The corners and the center of the board are unaccessible for all the warriors."+BLACK+" "+ GREEN_BOLD+"Only the King can get there."+BLACK);
            Thread.sleep(3000);
            System.out.println("And if the Branan steps out of the center, his throne, he can" +GREEN_BOLD+" no longer return to it.");
            System.out.println(BLACK_BOLD+"============= END OF THE RULES =============");
            System.out.println("");
        }
        catch(InterruptedException e){
            System.out.println("There was an error reading the rules. Try again, son.");
        }
    }

    static int setNumberGame(){
        System.out.println("How many wars will there be?");
        try{
            int numberGame = input.nextInt();
            return numberGame;
        } catch(NumberFormatException e){
            System.out.println("Arf, that's an invalid input. Please enter a number, son.");
        }
        return 0;
    }
}