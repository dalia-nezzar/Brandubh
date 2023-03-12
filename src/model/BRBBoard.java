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

    public void setValidCells(int row, int col) {
        resetReachableCells(false);

        List<Point> valid = computeValidCells(row, col);
        //System.out.println("row: " + row + " col: " + col);
        // if the list is not empty, set the reachable cells to true
        if (valid != null) {
            for(Point p : valid) {
                reachableCells[p.y][p.x] = true;
            }
        }
    }
    public List<Point> computeValidCells(int row, int col) {
        //TODO La faut tout refaire, les murs tt ça
        List<Point> lst = new ArrayList<>();
        Pawn p = null;
        // Check the row and col of the actual pawn, if a cell is empty on the same row or col, it is valid
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                if (isEmptyAt(i,j)) {
                    // check if the cell is on the same row or col
                    if (i == row || j == col) {
                        lst.add(new Point(j,i));
                    }
                }
            }
        }
        // remove the corner points from the list
        lst.remove(new Point(0,0));
        lst.remove(new Point(0,6));
        lst.remove(new Point(6,0));
        lst.remove(new Point(6,6));
        // remove center point
        lst.remove(new Point(3,3));

        // making it so a pawn can't jump over another pawn

        return lst;
    }
}
