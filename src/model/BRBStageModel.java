package model;

import boardifier.model.*;
import control.BRBController;

import java.awt.*;
import java.util.List;

public class BRBStageModel extends GameStageModel {

    private BRBBoard board;
    private Pawn[] blackPawns;
    private Pawn[] redPawns;
    private Pawn[] blackKingPawns;
    static int nbWinBlack = 0;
    static int nbWinRed = 0;
    static int pastScore = 0;
    public BRBStageModel(String name, Model model) {
        super(name, model);
        setupCallbacks();
    }

    public BRBBoard getBoard() {
        return board;
    }

    public static int[] getScore() {
        int[] score = new int[2];
        score[0] = nbWinBlack;
        score[1] = nbWinRed;
        int currScore = nbWinBlack+nbWinRed;
        // Fix score if needed
        int nbToRemove = currScore - pastScore - 1;
        nbWinRed -= nbToRemove;
        System.out.println(BRBController.countDraw+" "+BRBController.drawRequested);
        if (BRBController.countDraw== 1 || BRBController.drawRequested) {
            nbWinRed-=1;
            pastScore--;
        }
        BRBController.countDraw = 0;
        System.out.println(pastScore+" "+currScore);
        BRBController.drawRequested = false;
        currScore = nbWinBlack+nbWinRed;
        if (pastScore+1 != currScore) {
            // When a king is captured and another pawn is captured at the same time, the score is incremented by 2
            // Why ? : Because when a pawn is removed it triggers computePartyResult() which increments the score by 1
            // Solution : Just remove 1 from the score of red, that what is above
            throw new RuntimeException("Error in score");
        }
        pastScore = currScore;
        return score;
    }


    public void setBoard(BRBBoard board) {
        this.board = board;
        addGrid(board);
    }

    public Pawn[] getBlackPawns() {
        return blackPawns;
    }
    public void setBlackPawns(Pawn[] blackPawns) {
        this.blackPawns = blackPawns;
        for(int i=0;i<blackPawns.length;i++) {
            addElement(blackPawns[i]);
        }
    }

    public Pawn[] getRedPawns() {
        return redPawns;
    }
    public void setRedPawns(Pawn[] redPawns) {
        this.redPawns = redPawns;
        for(int i=0;i<redPawns.length;i++) {
            addElement(redPawns[i]);
        }
    }

    public Pawn[] getBlackKingPawns() {
        return blackKingPawns;
    }

    public void setBlackKingPawns(Pawn[] blackKingPawns) {
        this.blackKingPawns = blackKingPawns;
        for(int i=0;i<blackKingPawns.length;i++) {
            addElement(blackKingPawns[i]);
        }
    }

    private void setupCallbacks() {
        onMoveInGrid( (element, gridDest, rowDest, colDest) -> {
            // just check when pawns are put in the board
            if (gridDest != board) return;
            Pawn p = (Pawn) element;
            computeKingCorner();
        });
        onRemoveFromGrid( (element, gridSrc, rowSrc, colSrc) -> {
            // just check when pawns are removed from the board
            if (gridSrc != board) return;
            Pawn p = (Pawn) element;
            computePartyResult();
        });
    }
    private void computeKingCorner() {
        // If the king is in a corner, then the black player wins
        if (board.getElement(0, 0) == blackKingPawns[0]
                || board.getElement(0, 6) == blackKingPawns[0]
                || board.getElement(6, 0) == blackKingPawns[0]
                || board.getElement(6, 6) == blackKingPawns[0]) {
            int idWinner = 0;
            //System.out.println("The winner of the war is player "+idWinner);
            nbWinBlack++;
            // set the winner
            model.setIdWinner(idWinner);
            // stop de the game
            model.stopStage();
        }
    }
    private void computePartyResult() {
        // System.out.println("Computing party result...");
        int idWinner = -1;
        // Parcourir le board et compter le nombre de pions
        int nbRed = 0;
        int nbBlackKing = 0;
        for (int row=0;row<7;row++) {
            for (int col=0;col<7;col++) {
                Pawn p = (Pawn) board.getElement(row, col);
                if (p != null) {
                    if (p.isKing()) {
                        nbBlackKing++;
                    }
                    else if (p.getColor() == Pawn.PAWN_RED) {
                        nbRed++;
                    }
                }
            }
        }
        //System.out.println("nbBlackKing="+nbBlackKing);
        //System.out.println("nbRed="+nbRed);
        if (nbBlackKing == 0) {
            idWinner = 1;
        }
        if (nbRed == 0) {
            idWinner = 0;
        }

        //test if the current player can move
        BRBStageModel stage = (BRBStageModel) model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        // get list of the pawns of the current player
        List<GameElement> pawns = board.getPawns(model.getIdPlayer());
        List<Point> valid = null;
        Pawn pawn = null;
        int totalValid = 0;
        for (int i = 0; i < pawns.size(); i++) {
            // get the selected pawn
            pawn = (Pawn) pawns.get(i);
            // if isKing(), then char is 'K', else ' '
            char isKing = pawn.isKing() ? 'K' : ' ';
            // get the coords of the given pawn
            int[] coords = board.getCoords(pawn.getNumber(), pawn.getColor(), isKing);
            // print the coords
            //System.out.println("pawn: " + pawn.getNumber() + " row: " + coords[0] + " col: " + coords[1]);
            // get list of valid cells for the given pawn
            valid = board.computeValidCells(coords[0], coords[1], pawn.isKing());
            totalValid += valid.size();
        }
        if (totalValid == 0) {
            idWinner = 1 - model.getIdPlayer();
        }
        //System.out.println("totalValid: " + totalValid);


        if (idWinner != -1) {
            //System.out.println("The winner is player "+idWinner);
            // set the winner
            model.setIdWinner(idWinner);
            // if idWinner == 0, then black wins
            if (idWinner == 0) nbWinBlack++;
            else if (idWinner == 1) nbWinRed++;
            // stop de the game
            model.stopStage();
        }
        else {
            //System.out.println("No winner yet");
        }
    }

    public void giveUp(int player) {
        //System.out.println("Player "+player+" gives up");
        // set the winner
        if (player == 0) model.setIdWinner(1);
        else model.setIdWinner(0);

        if (model.getIdWinner() == 0) nbWinBlack++;
        else if (model.getIdWinner() == 1) nbWinRed++;
        // stop de the game
        model.stopStage();
    }

    private void computePartyDraw() {
        // stop the game
        model.stopStage();
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new BRBStageFactory(this);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row=0;row<7;row++) {
            for (int col=0;col<7;col++) {
                Pawn p = (Pawn) board.getElement(row, col);
                if (p != null) {
                    if (p.isKing()) {
                        sb.append("K");
                    }
                    else if (p.getColor() == Pawn.PAWN_RED) {
                        sb.append("A");
                    }
                    else {
                        sb.append("D");
                    }
                }
                else {
                    sb.append("-");
                }
            }
            sb.append(" ");
        }
        return sb.toString();
    }

    public double[] getInputs(int currentPlayer) {
        // Return the board in a 2D array
        double[] inputs = new double[50];
        int i=0;
        for (int row=0;row<7;row++) {
            for (int col=0;col<7;col++) {
                Pawn p = (Pawn) board.getElement(row, col);
                if (p != null) {
                    if (p.isKing()) {
                        inputs[i] = -1;
                    }
                    else if (p.getColor() == Pawn.PAWN_RED) {
                        inputs[i] = -0.5;
                    }
                    else {
                        inputs[i] = 0.5;
                    }
                }
                else {
                    inputs[i] = 0;
                }
                i++;
            }
        }
        // Add the current player to the input vector
        inputs[49] = currentPlayer;
        return inputs;
    }
}
