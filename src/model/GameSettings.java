package model;

import control.BRBController;

import java.util.Scanner;

public class GameSettings {
    private static int numberGame;

    public static int setNumberGame(){
        //BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("How many wars will there be?");
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
