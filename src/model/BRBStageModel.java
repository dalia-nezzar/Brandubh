package model;

import boardifier.model.*;

public class BRBStageModel extends GameStageModel {

    private BRBBoard board;
    //private BRBPawnPot blackPot;
    //private BRBPawnPot redPot;
    private Pawn[] blackPawns;
    private Pawn[] redPawns;
    private Pawn[] blackKingPawns;
    private int blackPawnsToPlay;
    private int redPawnsToPlay;
    private int blackKingPawnsToPlay;

    public BRBStageModel(String name, Model model) {
        super(name, model);
        blackPawnsToPlay = 4;
        blackKingPawnsToPlay = 1;
        redPawnsToPlay = 8;
        setupCallbacks();
        //computePartyResult();
    }

    public BRBBoard getBoard() {
        return board;
    }
    public void setBoard(BRBBoard board) {
        this.board = board;
        addGrid(board);
    }

    /*
    public BRBPawnPot getBlackPot() {
        return blackPot;
    }
    public void setBlackPot(BRBPawnPot blackPot) {
        this.blackPot = blackPot;
        addGrid(blackPot);


    public BRBPawnPot getRedPot() {
        return redPot;
    }
    public void setRedPot(BRBPawnPot redPot) {
        this.redPot = redPot;
        addGrid(redPot);
    }
     */

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
            // just check when pawns are put in 3x3 board
            if (gridDest != board) return;
            Pawn p = (Pawn) element;
            //removeEliminatedPawns();
            computePartyResult();
        });
    }
    private void removeEliminatedPawns() {
        // Check every cell, if there is a pawn, check if it is eliminated
        for (int row=0;row<7;row++) {
            for (int col=0;col<7;col++) {
                Pawn p = (Pawn) board.getElement(row, col);
                if (p != null) {
                    if (p.isCaptured()) {
                        // remove the pawn
                        board.removeElement(p);
                        // put it in the pot
                        if (p.getColor() == Pawn.PAWN_BLACK) {
                            //blackPot.putElement(p, 0,0);
                            blackPawnsToPlay--;
                        }
                        else {
                            //redPot.putElement(p, 0,0);
                            redPawnsToPlay--;
                        }
                    }
                }
            }
        }
    }
    private void computePartyResult() {
        System.out.println("Computing party result...");
        //TODO Ici on peut tester si la partie est fini ou non
        int idWinner = -1;
        // SI le king est dans un coin alors le joueur noir gagne
        if (board.getElement(0, 0) == blackKingPawns[0]
                || board.getElement(0, 6) == blackKingPawns[0]
                || board.getElement(6, 0) == blackKingPawns[0]
                || board.getElement(6, 6) == blackKingPawns[0]) {
            idWinner = 0;
        }
        //TODO SI il n'y a plus aucun pion rouge alors le joueur noir gagne
        //TODO SI le king est entouré de pions rouges alors le joueur rouge gagne
        //TODO ouais faudra faire la fonction isCaptured() je sais

        if (idWinner != -1) {
            System.out.println("The winner is player "+idWinner);
            // set the winner
            model.setIdWinner(idWinner);
            // stop de the game
            model.stopStage();
        }
        else {
            System.out.println("No winner yet");
        }
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new BRBStageFactory(this);
    }
}
