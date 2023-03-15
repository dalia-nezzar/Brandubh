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
        onPutInGrid( (element, gridDest, rowDest, colDest) -> {
            // just check when pawns are put in 3x3 board
            if (gridDest != board) return;
            Pawn p = (Pawn) element;
            computePartyResult();
        });
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
        //if (blackKingPawns.isCaptured()) {
        //    idWinner = 1;
        //}

        // get the empty cell, which should be in 2D at [0,0], [0,2], [1,1], [2,0] or [2,2]
        // i.e. or in 1D at index 0, 2, 4, 6 or 8
        /*
        int i = 0;
        int nbBlack = 0;
        int nbRed = 0;
        int countBlack = 0;
        int countRed = 0;
        Pawn p = null;
        int row, col;
        for (i = 0; i < 9; i+=2) {
            if (board.isEmptyAt(i / 3, i % 3)) break;
        }
        // get the 4 adjacent cells (if they exist) starting by the upper one
        row = (i / 3) - 1;
        col = i % 3;
        for (int j = 0; j < 4; j++) {
            // skip invalid cells
            if ((row >= 0) && (row <= 2) && (col >= 0) && (col <= 2)) {
                p = (Pawn) (board.getElement(row, col));
                if (p.getColor() == Pawn.PAWN_BLACK) {
                    nbBlack++;
                    countBlack += p.getNumber();
                } else {
                    nbRed++;
                    countRed += p.getNumber();
                }
            }
            // change row & col to set them at the correct value for the next iteration
            if ((j==0) || (j==2)) {
                row++;
                col--;
            }
            else if (j==1) {
                col += 2;
            }
        }

        System.out.println("nb black: "+nbBlack+", nb red: "+nbRed+", count black: "+countBlack+", count red: "+countRed);
        // decide whose winning
        if (nbBlack < nbRed) {
            idWinner = 0;
        }
        else if (nbBlack > nbRed) {
            idWinner = 1;
        }
        else {
            if (countBlack < countRed) {
                idWinner = 0;
            }
            else if (countBlack > countRed) {
                idWinner = 1;
            }
        }
         */
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
