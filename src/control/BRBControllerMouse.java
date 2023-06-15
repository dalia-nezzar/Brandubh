package control;

import boardifier.control.ActionPlayer;
import boardifier.control.*;
import boardifier.control.ControllerMouse;
import boardifier.model.Coord2D;
import boardifier.model.ElementTypes;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.action.MoveAction;
import boardifier.model.animation.Animation;
import boardifier.model.animation.AnimationTypes;
import boardifier.view.GridLook;
import boardifier.view.*;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import model.BRBBoard;
import model.BRBStageModel;
import model.Pawn;

import java.awt.*;
import java.util.List;

/**
 * A basic mouse controller that just grabs the mouse clicks and prints out some informations.
 * It gets the elements of the scene that are at the clicked position and prints them.
 */
public class BRBControllerMouse extends ControllerMouse implements EventHandler<MouseEvent> {

    public BRBControllerMouse(Model model, View view, Controller control) {
        super(model, view, control);
    }

    public void handle(MouseEvent event) {
        // if mouse event capture is disabled in the model, just return
        if (!model.isCaptureMouseEvent()) return;

        // get the clic x,y in the whole scene (this includes the menu bar if it exists)
        Coord2D clic = new Coord2D(event.getSceneX(),event.getSceneY());
        // get elements at that position
        List<GameElement> list = control.elementsAt(clic);
        // for debug, uncomment next instructions to display x,y and elements at that position
        /*
        System.out.println("click in "+event.getSceneX()+","+event.getSceneY());
        for(GameElement element : list) {
            System.out.println(element);
        }
         */
        BRBStageModel stageModel = (BRBStageModel) model.getGameStage();

        if (stageModel.getState() == BRBStageModel.STATE_SELECTPAWN) { // if we are in the state where we select a pawn
            for (GameElement element : list) {
                if (element.getType() == ElementTypes.getType("pawn")) {
                    Pawn pawn = (Pawn)element;
                    // check if color of the pawn corresponds to the current player id
                    if (pawn.getColor() == model.getIdPlayer() || (pawn.getColor() == 2 && model.getIdPlayer() == 0)) {
                        element.toggleSelected();
                        stageModel.setState(BRBStageModel.STATE_SELECTDEST);
                        return; // do not allow another element to be selected
                    }
                }
            }
        }
        else if (stageModel.getState() == BRBStageModel.STATE_SELECTDEST) { // if we are in the state where we select a destination
            // first check if the click is on the current selected pawn. In this case, unselect it
            for (GameElement element : list) {
                if (element.isSelected()) {
                    element.toggleSelected();
                    stageModel.setState(BRBStageModel.STATE_SELECTPAWN);
                    return;
                }
            }
            // secondly, check if the click is on another pawn from the same color. In this case, select it
            for (GameElement element : list) {
                if (element.getType() == ElementTypes.getType("pawn")) {
                    Pawn pawn = (Pawn)element;
                    // check if color of the pawn corresponds to the current player id
                    if (pawn.getColor() == model.getIdPlayer() || (pawn.getColor() == 2 && model.getIdPlayer() == 0)) {
                        // unselect the previous selected pawn
                        model.getSelected().get(0).toggleSelected();
                        element.toggleSelected();
                        return; // do not allow another element to be selected
                    }
                }
            }
            // thirdly, search if the board has been clicked. If not just return
            boolean boardClicked = false;
            for (GameElement element : list) {
                if (element == stageModel.getBoard()) {
                    boardClicked = true; break;
                }
            }
            if (!boardClicked) return;
            // get the board, pot,  and the selected pawn to simplify code in the following
            BRBBoard board = stageModel.getBoard();
            // by default get black pot
            //HolePawnPot pot = stageModel.getBlackPot();
            // but if it's player2 that plays, get red pot
            /*
            if (model.getIdPlayer() == 1) {
                pot = stageModel.getRedPot();
            }
             */
            GameElement pawn = model.getSelected().get(0);
            //System.out.println("pawn "+pawn);

            // thirdly, get the clicked cell in the 7x7 board
            GridLook lookBoard = (GridLook) control.getElementLook(board);
            int[] dest = lookBoard.getCellFromSceneLocation(clic);
            //System.out.println("dest "+dest[0]+","+dest[1]);
            // get the cell in the pot that owns the selected pawn
            //int[] from = pot.getElementCell(pawn);
            //System.out.println("try to move pawn " + pawn.getNumber() + " to " + dest[0]+","+dest[1]);
            // if the destination cell is valid for the selected pawn
            //System.out.println("can reach cell "+board.canReachCell(dest[0], dest[1]));
            // if isKing(), then char is 'K', else ' '
            char isKing = pawn.isKing() ? 'K' : ' ';
            // get the coords of the given pawn
            int[] coords = board.getCoords(pawn.getNumber(), pawn.getColor(), isKing);
            // get list of valid cells for the given pawn
            List<Point> valid = null;
            valid = board.computeValidCells(coords[0], coords[1], pawn.isKing());
            // if dest[0],dest[1] is in the list of valid cells, then move the pawn
            //System.out.println("valid contains "+valid.contains(new Point(dest[0], dest[1])));
            if (valid.contains(new Point(dest[0], dest[1]))) {
                // build the list of actions to do, and pass to the next player when done
                ActionList actions = new ActionList(true);
                // determine the destination point in the root pane
                Coord2D center = lookBoard.getRootPaneLocationForCellCenter(dest[1], dest[0]);
                // create an action with a linear move animation, with 10 pixel/frame
                //System.out.println("pawn "+pawn);
                //System.out.println("center "+center.getX()+","+center.getY());
                GameAction move = new MoveAction(model, pawn, "BRBboard", dest[1], dest[0], AnimationTypes.MOVE_LINEARPROP, center.getX(), center.getY(), 10);
                // add the action to the action list.
                actions.addSingleAction(move);
                //System.out.println("move " + move);
                stageModel.unselectAll();
                stageModel.setState(BRBStageModel.STATE_SELECTPAWN);
                ActionPlayer play = new ActionPlayer(model, control, actions);
                //System.out.println("play " + play);
                play.start();
                // check if there are pawns to remove
                List<GameElement> toRemove = board.getPawnsToRemove(dest[1], dest[0], pawn.getColor());
                // remove them
                board.removePawns(toRemove);
            }
        }
    }
}

