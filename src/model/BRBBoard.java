package model;

import boardifier.model.GameElement;
import boardifier.model.GameStageModel;
import boardifier.model.GridElement;

import java.util.ArrayList;
import java.util.List;
import java.awt.*;

public class BRBBoard extends GridElement {
    public BRBBoard(int x, int y, GameStageModel gameStageModel) {
        // call the super-constructor to create a 7x7 grid, named "BRBboard", and in x,y in space
        super("BRBboard", x, y, 7 , 7, gameStageModel);
        resetReachableCells(false);
    }

    public void setValidCells(int row, int col, boolean isKing) {
        resetReachableCells(false);

        List<Point> valid = computeValidCells(row, col, isKing);
        //System.out.println("row: " + row + " col: " + col);
        // if the list is not empty, set the reachable cells to true
        if (valid != null) {
            for(Point p : valid) {
                reachableCells[p.y][p.x] = true;
            }
        }
    }
    public List<Point> computeValidCells(int row, int col, boolean isKing) {
        List<Point> lst = new ArrayList<>();
        Pawn p = null;
        // While the cell above row, col is empty and cell coordinates is not 3, 3 : add it to the list
        for(int i=row-1;i>=0;i--) {
            if (isEmptyAt(i,col) && !(i == 3 && col == 3)) {
                lst.add(new Point(col,i));
            }
            else {
                break;
            }
        }
        // While the cell under row, col is empty and cell coordinates is not 3, 3 : add it to the list
        for(int i=row+1;i<7;i++) {
            if (isEmptyAt(i,col) && !(i == 3 && col == 3)) {
                lst.add(new Point(col,i));
            }
            else {
                break;
            }
        }
        // While the cell on the left of row, col and cell coordinates is not 3, 3 : is empty add it to the list
        for(int i=col-1;i>=0;i--) {
            if (isEmptyAt(row,i) && !(row == 3 && i == 3)) {
                lst.add(new Point(i,row));
            }
            else {
                break;
            }
        }
        // While the cell on the right of row, col and cell coordinates is not 3, 3 : is empty add it to the list
        for(int i=col+1;i<7;i++) {
            if (isEmptyAt(row,i) && !(row == 3 && i == 3)) {
                lst.add(new Point(i,row));
            }
            else {
                break;
            }
        }

        // remove the corner points from the list
        if (!isKing) {
            lst.remove(new Point(0,0));
            lst.remove(new Point(0,6));
            lst.remove(new Point(6,0));
            lst.remove(new Point(6,6));
        }
        // remove center point
        lst.remove(new Point(3,3));

        // making it so a pawn can't jump over another pawn
        // print the selected row, col
        //System.out.println("row: " + row + " col: " + col);
        // print the list
        //System.out.println("List before: " + lst);
        // print length of list
        //System.out.println("Length of list: " + lst.size());
        return lst;
    }

    public List<GameElement> getPawns(int idPlayer) {
        List<GameElement> lst = new ArrayList<>();
        for (int i=0;i<7;i++) {
            for (int j=0;j<7;j++) {
                if (getElement(i,j) != null && getElement(i,j).getColor() == idPlayer) {
                    lst.add(getElement(i,j));
                } else if (getElement(i,j) != null && getElement(i,j).getColor() == idPlayer+2) {
                    // if the pawn is a king, add it to the list too
                    lst.add(getElement(i,j));
                }
            }
        }
        return lst;
    }

    /**
     * Returns the list of pawns to remove
     * @param row
     * @param col
     * @param idPlayer
     * @return List<GameElement>
     */
    public List<GameElement> getPawnsToRemove(int row, int col, int idPlayer) {
        // TODO UNIT TESTS
        BRBStageModel gameStage = (BRBStageModel) getGameStage();
        BRBBoard board = gameStage.getBoard();
        List<GameElement> pawnsToRemove = new ArrayList<>();
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

        // Check above
        if (row - 1 >= 0) pawn1 = board.getElement(row - 1, col);
        if (row - 2 >= 0) pawn1_1 = board.getElement(row - 2, col);

        // Check below
        if (row + 1 <= 6) pawn2 = board.getElement(row + 1, col);
        if (row + 2 <= 6) pawn2_1 = board.getElement(row + 2, col);

        // Check left
        if (col - 1 >= 0) pawn3 = board.getElement(row, col - 1);
        if (col - 2 >= 0) pawn3_1 = board.getElement(row, col - 2);

        // Check right
        if (col + 1 <= 6) pawn4 = board.getElement(row, col + 1);
        if (col + 2 <= 6) pawn4_1 = board.getElement(row, col + 2);

        GameElement king = board.getElement(3, 3);
        if (idPlayer == 2) idPlayer = 0;
        switch (idPlayer) {
            case 0: {
                // If pawn above is a red pawn
                // AND (pawn far above is a black pawn OR it's the king OR it's the corner)
                // OR It's the empty throne
                System.out.println("row-2: " + (row-2) + " col: " + col);
                if ((pawn1 != null && pawn1.getColor() == 1)
                        && ((pawn1_1 != null && (pawn1_1.getColor() == idPlayer || pawn1_1.getColor() == 2))
                        || ((row-2==0 && ((col==0) || (col==6))) || (king == null && (row-2==3 && col==3))))){
                    pawnsToRemove.add(pawn1);
                }
                // If pawn under is a red pawn
                // AND (pawn far under is a black pawn OR it's the king OR it's the corner)
                // OR It's the empty throne
                if ((pawn2 != null && pawn2.getColor() == 1)
                        && ((pawn2_1 != null && (pawn2_1.getColor() == idPlayer || pawn2_1.getColor() == 2))
                        || ((row+2==6 && ((col==0) || (col==6))) || (king == null && (row+2==3 && col==3))))) {
                    pawnsToRemove.add(pawn2);
                }
                // If pawn left is a red pawn
                // AND (pawn far left is a black pawn OR it's the king OR it's the corner)
                // OR It's the empty throne
                if ((pawn3 != null && pawn3.getColor() == 1)
                        && ((pawn3_1 != null && (pawn3_1.getColor() == idPlayer || pawn3_1.getColor() == 2))
                        || ((col-2==0 && ((row==0) || (row==6))) || (king == null && (row==3 && col-2==3))))) {
                    pawnsToRemove.add(pawn3);
                }
                // If pawn right is a red pawn
                // AND (pawn far right is a black pawn OR it's the king OR it's the corner)
                // OR It's the empty throne
                if ((pawn4 != null && pawn4.getColor() == 1)
                        && ((pawn4_1 != null && (pawn4_1.getColor() == idPlayer || pawn4_1.getColor() == 2))
                        || ((col+2==6 && ((row==0) || (row==6))) || (king == null && (row==3 && col+2==3))))) {
                    pawnsToRemove.add(pawn4);
                }
                break;
            }
            case 1: {
                // If pawn above is a black pawn OR it's the king
                // AND (pawn far above is a red pawn OR it's the corner)
                // OR It's the empty throne
                if ((pawn1 != null && (pawn1.getColor() == 0 || (pawn1.getColor() == 2)))
                        && ((pawn1_1 != null && pawn1_1.getColor() == idPlayer)
                        || ((row-2==0 && ((col==0) || (col==6))) || (king == null && (row-2==3 && col==3))))) {
                    pawnsToRemove.add(pawn1);
                }
                // If pawn under is a black pawn OR it's the king
                // AND (pawn far under is a red pawn OR it's the corner)
                // OR It's the empty throne
                if ((pawn2 != null && (pawn2.getColor() == 0 || (pawn2.getColor() == 2)))
                        && ((pawn2_1 != null && pawn2_1.getColor() == idPlayer)
                        || ((row+2==6 && ((col==0) || (col==6))) || (king == null && (row+2==3 && col==3))))) {
                    pawnsToRemove.add(pawn2);
                }
                // If pawn left is a black pawn OR it's the king
                // AND (pawn far left is a red pawn OR it's the corner)
                // OR It's the empty throne
                if ((pawn3 != null && (pawn3.getColor() == 0 || (pawn3.getColor() == 2)))
                        && ((pawn3_1 != null && pawn3_1.getColor() == idPlayer)
                        || ((col-2==0 && ((row==0) || (row==6))) || (king == null && (row==3 && col-2==3))))) {
                    pawnsToRemove.add(pawn3);
                }
                // If pawn right is a black pawn OR it's the king
                // AND (pawn far right is a red pawn OR it's the corner)
                // OR It's the empty throne
                if ((pawn4 != null && (pawn4.getColor() == 0 || (pawn4.getColor() == 2)))
                        && ((pawn4_1 != null && pawn4_1.getColor() == idPlayer)
                        || ((col+2==6 && ((row==0) || (row==6))) || (king == null && (row==3 && col+2==3))))) {
                    pawnsToRemove.add(pawn4);
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
                pawnsToRemove.add(king);
            } else {
                // remove the king from the list
                pawnsToRemove.remove(king);
            }
        }
        return pawnsToRemove;
    }

    public void removePawns(List<GameElement> pawnsToRemove) {
        BRBStageModel gameStage = (BRBStageModel) getGameStage();
        BRBBoard board = gameStage.getBoard();

        for (GameElement pawn : pawnsToRemove) {
            pawn.setCaptured(true);
            board.removeElement(pawn);
        }
    }
}
