package model;

import control.BRBController;

import java.util.Scanner;

import static boardifier.view.ConsoleColor.BLACK;

public class GameSettings {
    private static int numberGame;

    public static int setNumberGame(String args[]){
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println();
        System.out.println(BLACK + "How many wars will there be?");
        if (args.length == 4) {
            System.out.println("You have chosen " + args[3] + " as the number of wars.");
            numberGame = Integer.parseInt(args[3]);
            return numberGame;
        }
        if (args.length == 5) {
            System.out.println("You have chosen " + args[4] + " as the number of wars.");
            numberGame = Integer.parseInt(args[4]);
            return numberGame;
        }
        try{
            numberGame = BRBController.input.nextInt();
            return numberGame;
        } catch(NumberFormatException e){
            System.out.println("Arf, that's an invalid input. Please enter a number, son.");
        }
        return 0;
    }

    public static int getNumberGame(){
        return numberGame;
    }
}
