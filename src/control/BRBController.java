package control;

import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.GridElement;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.action.MoveAction;
import boardifier.view.View;
import model.BRBBoard;
import model.BRBStageModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class BRBController extends Controller {

    BufferedReader consoleIn;
    boolean firstPlayer;

    public BRBController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
    }

    /**
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        consoleIn = new BufferedReader(new InputStreamReader(System.in));
        update();
        while(! model.isEndStage()) {
            nextPlayer();
            update();
        }
        stopStage();
        endGame();
    }

    /**
     * Defines what to do when the stage is over
     */
    public void nextPlayer() {
        // for the first player, the id of the player is already set, so do not compute it
        if (!firstPlayer) {
            model.setNextPlayer();
        }
        else {
            firstPlayer = false;
        }
        // get the new player
        Player p = model.getCurrentPlayer();
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER PLAYS");
            BRBDecider decider = new BRBDecider(model,this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        }
        else {
            boolean ok = false;
            while (!ok) {
                System.out.print(p.getName()+ " > ");
                try {
                    String line = consoleIn.readLine();
                    if (line.length() == 3) {
                        ok = analyseAndPlay(line);
                    }
                    if (!ok) {
                        System.out.println("incorrect instruction. retry !");
                    }
                }
                catch(IOException e) {}
            }
        }
    }

    /**
     * @param line the line read from the console
     * @return true if the line is correct and the action has been played
     */
    private boolean analyseAndPlay(String line) {
        BRBStageModel gameStage = (BRBStageModel) model.getGameStage();
        // get the pawn value from the first char
        char firstChar = line.charAt(0);
        if (firstChar == 'K') {
            if (model.getIdPlayer() != 0) return false;
        } else {
            int pawnIndex = (int) (line.charAt(0) - '1');
            // Check pawn color (red or black)
            if (model.getIdPlayer() == 0) {
                if ((pawnIndex<0)||(pawnIndex>3)) return false;
                // get the cords in the board
            } else {
                if ((pawnIndex<0)||(pawnIndex>7)) return false;
                // get the cords in the board
            }
        }

        int col = (int) (line.charAt(1) - 'A');
        int row = (int) (line.charAt(2) - '1');
        // check coords validity
        if ((row<0)||(row>6)) return false;
        if ((col<0)||(col>6)) return false;
        // check if the pawn is still in its pot

        int[] coords = gameStage.getBoard().getCoords(pawnIndex+1, model.getIdPlayer());
        System.out.println("row: "+ (row) +" col: "+col);
        System.out.println("gamestage.getBoard().getElement(row,col): "+gameStage.getBoard().getElement(coords[0],coords[1]));
        GameElement pawn = gameStage.getBoard().getElement(coords[0],coords[1]);
        // GameElement pawn = pot.getElement(pawnIndex,0);
        // compute valid cells for the chosen pawn
        gameStage.getBoard().setValidCells(coords[0], coords[1]);
        if (!gameStage.getBoard().canReachCell(row,col)) return false;

        ActionList actions = new ActionList(true);
        GameAction move = new MoveAction(model, pawn, "BRBboard", row, col);
        // add the action to the action list.
        actions.addSingleAction(move);
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();
        return true;
    }
}
