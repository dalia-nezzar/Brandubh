package model;

import boardifier.model.*;

public class BRBStageModel extends GameStageModel {

    private BRBBoard board;
    private Pawn[] blackPawns;
    private Pawn[] redPawns;
    private Pawn[] blackKingPawns;

    public BRBStageModel(String name, Model model) {
        super(name, model);
        setupCallbacks();
    }

    public BRBBoard getBoard() {
        return board;
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
        // SI le king est dans un coin alors le joueur noir gagne
        if (board.getElement(0, 0) == blackKingPawns[0]
                || board.getElement(0, 6) == blackKingPawns[0]
                || board.getElement(6, 0) == blackKingPawns[0]
                || board.getElement(6, 6) == blackKingPawns[0]) {
            int idWinner = 0;
            System.out.println("The winner is player "+idWinner);
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
        int nbRouge = 0;
        int nbRoiNoir = 0;
        for (int row=0;row<7;row++) {
            for (int col=0;col<7;col++) {
                Pawn p = (Pawn) board.getElement(row, col);
                if (p != null) {
                    if (p.isKing()) {
                        nbRoiNoir++;
                    }
                    else if (p.getColor() == Pawn.PAWN_RED) {
                        nbRouge++;
                    }
                }
            }
        }
        //System.out.println("nbRoiNoir="+nbRoiNoir);
        //System.out.println("nbRouge="+nbRouge);
        if (nbRoiNoir == 0) {
            idWinner = 1;
        }
        if (nbRouge == 0) {
            idWinner = 0;
        }

        if (idWinner != -1) {
            //System.out.println("The winner is player "+idWinner);
            // set the winner
            model.setIdWinner(idWinner);
            // stop de the game
            model.stopStage();
        }
        else {
            //System.out.println("No winner yet");
        }
    }

    private void computePartyDraw() {
        // stop the game
        model.stopStage();
    }

    @Override
    public StageElementsFactory getDefaultElementFactory() {
        return new BRBStageFactory(this);
    }
}
