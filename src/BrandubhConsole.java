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
import javax.swing.*;

import static boardifier.view.ConsoleColor.*;
import static model.GameSettings.setNumberGame;


public class BrandubhConsole {
    public static final int HUMAN_VS_HUMAN = 0;
    public static final int HUMAN_VS_COMPUTER = 1;
    public static final int COMPUTER_VS_COMPUTER = 2;

    public static void main(String[] args) {
        Thread loadFilesThread = new Thread(() -> BRBDecider.loadData("dataMap.bin"));
        loadFilesThread.start();

        int modeChoice;
        try {
            modeChoice = Integer.parseInt(args[0]);
            if ((modeChoice <0) || (modeChoice>2)) modeChoice = -1;
        }
        catch(NumberFormatException | ArrayIndexOutOfBoundsException e) {
            modeChoice = -1;
        }
        int mode;
        Model model = new Model();
        if (modeChoice==-1) mode=chooseGameMode();
        else if (modeChoice > 0 && modeChoice < 3) mode = modeChoice;
        else mode = -1;

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
        String AI1Mode1="";
        String AI2Mode1="";
        String AI1Mode2="";
        String AI2Mode2="";
        if (mode == 0) {
            System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
            String player1 = getName();
            if (player1.equals("")) player1 = "Player1";
            model.addHumanPlayer(player1);
            System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
            String player2 = getName();
            if (player2.equals("")) player2 = "Player2";
            if (player1.toLowerCase().equals(player2.toLowerCase()) && !player2.equals("")) {
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
                answer = BRBController.input.nextLine();
            } catch (Exception e) {
                System.out.println("Error while reading your answer. Please try again.");
            }
            if (answer.equals("1")){
                System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
                String player1 = getName();
                model.addHumanPlayer(player1);
                System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
                AI1Mode1=setAI(1, args);
                model.addComputerPlayer(AI1Mode1);
            }
            else if (answer.equals("2")){
                System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
                AI1Mode1=setAI(1, args);
                model.addComputerPlayer(AI1Mode1);
                System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
                String player1 = getName();
                model.addHumanPlayer(player1);
            }
        }
        else if (mode == 2) {
            System.out.println(GREEN_BOLD+"===== DEFENDERS ====="+BLACK);
            AI1Mode2=setAI(2, args);
            model.addComputerPlayer(AI1Mode2);
            System.out.println(RED_BOLD+"===== ATTACKERS ====="+BLACK);
            AI2Mode2=setAI(1, args);
            model.addComputerPlayer(AI2Mode2);
        }

        StageFactory.registerModelAndView("BRB", "model.BRBStageModel", "view.BRBStageView");
        View BRBView = new View(model);
        BRBController control = new BRBController(model,BRBView);
        control.setFirstStageName("BRB");
        BRBController.nbParties=setNumberGame(args);

        /*
        //if one of the AI is "Smart" then BRuBDecider.loadData(dataMap.bin)
        if (BRBController.typeAI1 == 2 || BRBController.typeAI2 == 2) {
            BRBDecider.loadData("dataMap.bin");
        }
         */
        //if both of the AI are not smart then interrupt the thread
        if (BRBController.typeAI1 != 2 && BRBController.typeAI2 != 2 && loadFilesThread.isAlive()) {
            //System.out.println("interrupting thread");
            loadFilesThread.interrupt();
        } else if ((BRBController.typeAI1 == 2 || BRBController.typeAI2 == 2) && loadFilesThread.isAlive()) {
            Thread loadBarThread = new Thread(() -> loadBar(500, "Data still loading, please wait"));
            loadBarThread.start();
            try {
                loadFilesThread.join();
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            loadBarThread.interrupt();
        }

        try {
            for (int i=0;i<BRBController.nbParties;i++) {
                control.startGame();
                control.stageLoop();
                if (BRBController.stopBool) break;
            }
        }
        catch(GameException e) {
            System.out.println("Cannot start the war. Abort");
        }
        if (BRBController.nbParties > 1000) {
            System.out.println("DataMap size: " + BRBController.dataMap.size() + " elements.");
            System.out.println("This will take " + BRBController.dataMap.size()/1000000 + " seconds to save.");
            Thread saveFilesThread = new Thread(() -> progressBar(BRBController.dataMap.size()/1000, "Saving files, do not quit... "));
            saveFilesThread.start();
            control.saveAllFiles();
            if (saveFilesThread.isAlive()) {
                saveFilesThread.interrupt();
                progressBar(10, "\rSaving files, do not quit... ");
            }
        }
    }

    /**
     * Progress bar
     **/
    public static void progressBar(int sleepTime, String text) {
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(sleepTime);
                //System.out.print(BLACK_BOLD + "█" + BLACK);
            } catch (InterruptedException e) {
                //e.printStackTrace();
            }
            System.out.print(RED_BOLD + "\r" + text + BLACK + "[");
            for (int j = 0; j <= i; j++) System.out.print(RED_BOLD + "=" + BLACK);
            for (int j = 0; j < 9 - i; j++) System.out.print(" ");
            System.out.print(BLACK + "]");
        }
        System.out.println();
    }

    /**
     * Loading print
     **/
    public static void loadBar(int sleepTime, String text) {
        int i = 0;
        while (true) {
            if (Thread.currentThread().isInterrupted()) return;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            if (i == 0) System.out.print(RED_BOLD + "\r" + text + ". ");
            else if (i == 1) System.out.print(RED_BOLD + "\r" + text + "..");
            else if (i == 2) System.out.print(RED_BOLD + "\r" + text + "...");
            else if (i == 3) System.out.print(RED_BOLD + "\r" + text + "   ");
            else if (i > 3) i = -1;
            i++;
        }
    }


    /**
     * Get the name of the player from the standard input
     * @return The name of the player
     **/
    static String getName() {
        String name = "";
        System.out.println("Enter your name, warrior! ");
        name = BRBController.input.nextLine();
        return name;
    }

    public static String setAI(int player, String[] args) {
        int ai;
        System.out.println("Choose your opponent, warrior! Will it be God Odin or God Loki?");
        System.out.println(PURPLE_BOLD+"1. God Odin (SMART)"+BLACK);
        System.out.println(GREEN_BOLD+"2. God Loki (EAT)"+BLACK);
        System.out.println(RED_BOLD+"3. Goddess Frigg (RANDOM)"+BLACK);

        try {
            if (player == 1)
                ai = Integer.parseInt(args[1]);
            else if (player == 2)
                ai = Integer.parseInt(args[2]);
            else
                ai = -1;
        } catch (Exception e) {
            ai = -1;
        }

        if (ai != 1 && ai != 2 && ai != 3) {
            do {
                try {
                    ai = BRBController.input.nextInt();
                } catch (Exception e) {
                    ai = -1;
                }
                if (ai != 1 && ai != 2 && ai != 3) System.out.println("Don't take me for a fool, son. That's not a real God! Try again.");
            } while (ai != 1 && ai != 2 && ai != 3);
        }

        if (ai == 1) {
            if (player == 1)
                BRBController.typeAI1 = 2;
            else
                BRBController.typeAI2 = 2;
            return "God Odin (SMART)";
        } else if (ai == 2) {
            if (player == 1)
                BRBController.typeAI1 = 3;
            else
                BRBController.typeAI2 = 3;
            return "God Loki (EAT)";
        } else if (ai == 3) {
            if (player == 1)
                BRBController.typeAI1 = 1;
            else
                BRBController.typeAI2 = 1;
            return "Goddess Frigg (RANDOM)";
        } else {
            System.out.println("Don't take me for a fool, son. That's not a real God! Abort.");
            return null;
        }
    }
    public static int chooseGameMode() {
        System.out.println(BLACK_BOLD+"Welcome, warrior, to Brandubh!");
        System.out.println("Oh! Say, son, do you know"+ PURPLE_BRIGHT+ " the rules of the Brandubh?"+BLACK);
        String answer = "";
        while (true) {
            try {
                answer = BRBController.input.nextLine();
                if (answer.toLowerCase().contains("yes")
                || answer.toLowerCase().equals("y")
                || answer.toLowerCase().contains("yeah")
                || answer.toLowerCase().contains("yep")
                || answer.toLowerCase().contains("yup")
                || answer.toLowerCase().equals("ye")
                || answer.toLowerCase().equals("yee")
                || answer.toLowerCase().contains("oui")
                || answer.toLowerCase().equals("1")) {
                    System.out.println("Fair. Let's head to the warzone, ey!");
                    System.out.println("=======================================");
                    break;
                } else if (answer.toLowerCase().contains("no")
                || answer.toLowerCase().equals("n")
                || answer.toLowerCase().contains("nope")
                || answer.toLowerCase().contains("nah")
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
                mode = Integer.parseInt(BRBController.input.nextLine());
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
}