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
import java.util.ArrayList;
import java.util.List;

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
        while (!model.isEndStage()) {
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
        } else {
            firstPlayer = false;
        }
        // get the new player
        Player p = model.getCurrentPlayer();
        // System.out.println("Player " + p.getName() + " plays");
        if (p.getType() == Player.COMPUTER) {
            System.out.println("COMPUTER PLAYS");
            BRBDecider decider = new BRBDecider(model, this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start();
        } else {
            boolean ok = false;
            while (!ok) {
                System.out.print(p.getName() + " > ");
                try {
                    String line = consoleIn.readLine();
                    if (line.length() == 3) {
                        ok = analyseAndPlay(line);
                    } else if (line.toLowerCase().contains("draw")) {
                        System.out.println(p.getName() + " offers a draw, accept ?");
                        // store players
                        List<Player> players = model.getPlayers();
                        if (players.get(0).getType() == Player.COMPUTER || players.get(1).getType() == Player.COMPUTER) {
                            System.out.print("Computer > Beep boop (he said no)");
                            line = "L(° O °L)";
                        } else if (players.get(0) == p) {
                            System.out.print(players.get(1).getName()+ " > ");
                            line = consoleIn.readLine();
                        } else {
                            System.out.print(players.get(0).getName()+ " > ");
                            line = consoleIn.readLine();
                        }
                        if (line.toLowerCase().contains("yes")
                                || line.toLowerCase().contains("ok") ||
                                line.toLowerCase().contains("accept") ||
                                line.toLowerCase().contains("oui") ||
                                line.toLowerCase().contains("draw")) {
                            System.out.println("Draw accepted");
                            stopStage();
                            endGame();
                            return;
                        } else {
                            System.out.println("Draw refused");
                        }
                    }
                    if (!ok) {
                        System.out.println("incorrect instruction. retry !");
                    }
                } catch (IOException e) {
                }
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
        int pawnIndex = 1;
        char firstChar = line.charAt(0);
        if (firstChar == 'K' || firstChar == 'k') {
            if (model.getIdPlayer() != 0) return false;
        } else {
            pawnIndex = (int) (line.charAt(0) - '1');
            // Check pawn color (red or black)
            if (model.getIdPlayer() == 0) {
                if ((pawnIndex < 0) || (pawnIndex > 3)) return false;
                // get the cords in the board
            } else {
                if ((pawnIndex < 0) || (pawnIndex > 7)) return false;
                // get the cords in the board
            }
        }

        int col = (int) (line.charAt(1) - 'A');
        int row = (int) (line.charAt(2) - '1');
        // check coords validity
        if ((row < 0) || (row > 6)) return false;
        if ((col < 0) || (col > 6)) return false;
        // check if the pawn is still in its pot

        int[] coords = gameStage.getBoard().getCoords(pawnIndex + 1, model.getIdPlayer(), firstChar);
        // System.out.println("coords[0]: " + coords[0] + " coords[1]: " + coords[1]);
        // System.out.println("row: " + (row) + " col: " + col);
        // System.out.println("gamestage.getBoard().getElement(row,col): " + gameStage.getBoard().getElement(coords[0], coords[1]));
        if (coords == null) return false;
        GameElement pawn = gameStage.getBoard().getElement(coords[0], coords[1]);
        // compute valid cells for the chosen pawn

        if (firstChar == 'K' || firstChar == 'k') {
            gameStage.getBoard().setValidCells(coords[0], coords[1], true);
        } else {
            gameStage.getBoard().setValidCells(coords[0], coords[1], false);
        }


        // System.out.println("Got HERE : " + gameStage.getBoard().canReachCell(row, col));
        if (!gameStage.getBoard().canReachCell(row, col)) return false;
        // System.out.println("Got HERE 2");
        ActionList actions = new ActionList(true);
        // System.out.println("Got HERE 3");
        GameAction move = new MoveAction(model, pawn, "BRBboard", row, col);
        // System.out.println("Got HERE 4");
        // add the action to the action list.
        actions.addSingleAction(move);
        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();
        setPawnCaptured(row, col, model.getIdPlayer());
        return true;
    }

    /**
     * Set pawn captured
     */
    public void setPawnCaptured(int row, int col, int idPlayer) {
        BRBStageModel gameStage = (BRBStageModel) model.getGameStage();
        BRBBoard board = gameStage.getBoard();
        // Check for the pawn above, under, left and right from row col
        // Also check if the row and col is valid before calling
        GameElement pawn1 = null;
        GameElement pawn1_1 = null;

        GameElement pawn2 = null;
        GameElement pawn2_1 = null;

        GameElement pawn3 = null;
        GameElement pawn3_1 = null;

        GameElement pawn4 = null;
        GameElement pawn4_1 = null;

        if (row - 1 >= 0) pawn1 = board.getElement(row - 1, col);
        if (row - 2 >= 0) pawn1_1 = board.getElement(row - 2, col);

        if (row + 1 <= 6) pawn2 = board.getElement(row + 1, col);
        if (row + 2 <= 6) pawn2_1 = board.getElement(row + 2, col);

        if (col - 1 >= 0) pawn3 = board.getElement(row, col - 1);
        if (col - 2 >= 0) pawn3_1 = board.getElement(row, col - 2);

        if (col + 1 <= 6) pawn4 = board.getElement(row, col + 1);
        if (col + 2 <= 6) pawn4_1 = board.getElement(row, col + 2);

        GameElement king = board.getElement(3, 3);

        // System.out.println("pawn1: " + pawn1 + " pawn2: " + pawn2 + " pawn3: " + pawn3 + " pawn4: " + pawn4);
        switch (idPlayer) {
            case 0: {
                // If pawn above is a black pawn
                // AND (pawn above is a red pawn OR coords is in the list of corners)
                if ((pawn1 != null && pawn1.getColor() == 1)
                        && ((pawn1_1 != null && pawn1_1.getColor() == idPlayer)
                        || (row-2==0 && ((col==0) || (col==6))) ) ) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn1.setCaptured(true);
                    board.removeElement(pawn1);
                    // remove the pawn from the board
                    //board.removeElement(board.getElement(row-1, col));
                }
                // If pawn under is a black pawn
                // AND (pawn under is a red pawn OR coords is in the list of corners)
                if ((pawn2 != null && pawn2.getColor() == 1)
                        && ((pawn2_1 != null && pawn2_1.getColor() == idPlayer)
                        || (row+2==6 && ((col==0) || (col==6))) )) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn2.setCaptured(true);
                    board.removeElement(pawn2);
                    //board.removeElement(board.getElement(row+1, col));
                }
                if ((pawn3 != null && pawn3.getColor() == 1)
                        && ((pawn3_1 != null && pawn3_1.getColor() == idPlayer)
                        || (col-2==0 && ((row==0) || (row==6))) )) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn3.setCaptured(true);
                    board.removeElement(pawn3);
                    //board.removeElement(board.getElement(row, col-1));
                }
                if ((pawn4 != null && pawn4.getColor() == 1)
                        && ((pawn4_1 != null && pawn4_1.getColor() == idPlayer)
                        || (col+2==6 && ((row==0) || (row==6))))) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn4.setCaptured(true);
                    board.removeElement(pawn4);
                    //board.removeElement(board.getElement(row, col+1));
                }
                break;
            }
            case 1: {
                // Get coords of the case at row-2, col
                if ((pawn1 != null && (pawn1.getColor() == 0 || (pawn1 != king)))
                        && ((pawn1_1 != null && pawn1_1.getColor() == idPlayer)
                        || (row-2==0 && ((col==0) || (col==6))) )) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn1.setCaptured(true);
                    board.removeElement(pawn1);
                    //board.removeElement(board.getElement(row-1, col));
                }
                if ((pawn2 != null && (pawn2.getColor() == 0 || (pawn2 != king)))
                        && ((pawn2_1 != null && pawn2_1.getColor() == idPlayer)
                        || (row+2==6 && ((col==0) || (col==6))) )) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn2.setCaptured(true);
                    board.removeElement(pawn2);
                    //board.removeElement(board.getElement(row+1, col));
                }
                // Get coords of the case at row, col-2
                if ((pawn3 != null && (pawn3.getColor() == 0 || (pawn3 != king)))
                        && ((pawn3_1 != null && pawn3_1.getColor() == idPlayer)
                        || (col-2==0 && ((row==0) || (row==6))) )) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn3.setCaptured(true);
                    board.removeElement(pawn3);
                    //board.removeElement(board.getElement(row, col-1));
                }
                if ((pawn4 != null && (pawn4.getColor() == 0 || (pawn4 != king)))
                        && ((pawn4_1 != null && pawn4_1.getColor() == idPlayer)
                        || (col+2==6 && ((row==0) || (row==6))))) {
                    // System.out.println("PAWN GETTING CAPTURED");
                    pawn4.setCaptured(true);
                    board.removeElement(pawn4);
                    //board.removeElement(board.getElement(row, col+1));
                }
                break;
            }
        }
        // if the king is on 3,3, and is surrounded by 4 red pawns, then the king is captured
        if (king != null && king.getColor() == 2) {
            if (board.getElement(2, 3) != null && board.getElement(2, 3).getColor() == 1
                    && board.getElement(4,3) != null && board.getElement(4, 3).getColor() == 1
                    && board.getElement(3, 2) != null && board.getElement(3, 2).getColor() == 1
                    && board.getElement(3, 4) != null && board.getElement(3, 4).getColor() == 1) {
                // System.out.println("KING GETTING CAPTURED");
                board.getElement(3, 3).setCaptured(true);
                king.setCaptured(true);
                board.removeElement(king);
            }
        }
    }
}
