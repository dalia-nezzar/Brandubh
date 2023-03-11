package control;

import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.action.MoveAction;
import model.BRBBoard;
//import model.BRBPawnPot;
import model.BRBStageModel;
import model.Pawn;

import java.awt.*;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class BRBDecider extends Decider {

    private static final Random loto = new Random(Calendar.getInstance().getTimeInMillis());

    public BRBDecider(Model model, Controller control) {
        super(model, control);
    }

    @Override
    public ActionList decide() {
        System.out.println("ActionList is getting executed");
        // do a cast get a variable of the real type to get access to the attributes of HoleStageModel
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        //BRBPawnPot pot = null; // the pot where to take a pawn
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        /*
        if (model.getIdPlayer() == Pawn.PAWN_BLACK) {
            pot = stage.getBlackPot();
        }
        else {
            pot = stage.getRedPot();
        }

        for(int i=0;i<4;i++) {
            Pawn p = (Pawn)pot.getElement(i,0);
            // if there is a pawn in i.
            if (p != null) {
                // get the valid cells
                List<Point> valid = board.computeValidCells(p.getNumber());
                if (valid.size() != 0) {
                    // choose at random one of the valid cells
                    int id = loto.nextInt(valid.size());
                    pawn = p;
                    rowDest = valid.get(id).y;
                    colDest = valid.get(id).x;
                    break; // stop the loop
                }
            }
        }
         */

        // create action list. After the last action, it is next player's turn.
        ActionList actions = new ActionList(true);
        // create the move action, without animation => the pawn will be put at the center of dest cell
        GameAction move = new MoveAction(model, pawn, "BRBboard", rowDest, colDest);
        actions.addSingleAction(move);
        return actions;
    }
}
