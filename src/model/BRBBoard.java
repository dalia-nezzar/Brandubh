package model;

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
        //TODO La faut tout refaire, les murs tt ça
        List<Point> lst = new ArrayList<>();
        Pawn p = null;
        // While the cell above row, col is empty add it to the list
        for(int i=row-1;i>=0;i--) {
            if (isEmptyAt(i,col)) {
                lst.add(new Point(col,i));
            }
            else {
                break;
            }
        }
        // While the cell under row, col is empty add it to the list
        for(int i=row+1;i<7;i++) {
            if (isEmptyAt(i,col)) {
                lst.add(new Point(col,i));
            }
            else {
                break;
            }
        }
        // While the cell on the left of row, col is empty add it to the list
        for(int i=col-1;i>=0;i--) {
            if (isEmptyAt(row,i)) {
                lst.add(new Point(i,row));
            }
            else {
                break;
            }
        }
        // While the cell on the right of row, col is empty add it to the list
        for(int i=col+1;i<7;i++) {
            if (isEmptyAt(row,i)) {
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

        return lst;
    }
}
