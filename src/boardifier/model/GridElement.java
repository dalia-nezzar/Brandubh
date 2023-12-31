package boardifier.model;

import model.BRBBoard;
import model.BRBStageModel;
import model.Pawn;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GridElement extends StaticElement {
    protected String name;
    protected int nbRows;
    protected int nbCols;
    protected List<GameElement>[][] grid;
    protected boolean[][] reachableCells;

    public GridElement(String name, int x, int y, int nbRows, int nbCols, GameStageModel gameStageModel) {
        this(name, x, y, nbRows, nbCols, gameStageModel, ElementTypes.getType("grid"));
    }

    public GridElement(String name, int x, int y, int nbRows, int nbCols, GameStageModel gameStageModel, int type) {
        super(x, y, gameStageModel, type);
        this.name = name;
        this.nbRows = nbRows;
        this.nbCols = nbCols;
        grid = new List[nbRows][nbCols];
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
        reachableCells = new boolean[nbRows][nbCols];
        resetReachableCells(true);
    }

    // getters/setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNbRows() {
        return nbRows;
    }

    public int getNbCols() {
        return nbCols;
    }

    public void resetReachableCells(boolean state) {
        for (int i = 0; i < nbRows; i++) {
            Arrays.fill(reachableCells[i], state);
        }
        // in some games, changing the reachable state has an impact on the grid look
        lookChanged = true;
    }

    public boolean[][] getReachableCells() {
        return reachableCells;
    }

    public void setCellReachable(int row, int col, boolean reachable) {
        if ((row >= 0) && (row < nbRows) && (col >= 0) && (col < nbCols)) {
            reachableCells[row][col] = reachable;
            // in some games, changing the reachable state has an impact on the grid look
            lookChanged = true;
        }
    }

    public boolean canReachCell(int row, int col) {
        if ((row >= 0) && (row < nbRows) && (col >= 0) && (col < nbCols)) {
            // Print reachable cells
            /*
            for (int i = 0; i < nbRows; i++) {
                for (int j = 0; j < nbCols; j++) {
                    System.out.print(reachableCells[i][j] ? "1" : "0");
                }
                System.out.println();
            }
             */
            return reachableCells[row][col];
        }
        return false;
    }

    // reset the board by removing elements from the board AND from the elements in model
    public void reset() {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                grid[i][j] = new ArrayList<>();
            }
        }
    }

    public void putElement(GameElement element, int row, int col) {
        putElement(element, row, col, true);
    }

    public void putElement(GameElement element, int row, int col, boolean autoLoc) {
        grid[row][col].add(element);
        element.setGrid(this);
        if (autoLoc) element.setAutoLocChanged(true);
        // signal the stage model that element is put in grid so that callback can be executed
        gameStageModel.putInGrid(element, this, row, col);
    }

    public void removeElement(GameElement element) {
        int[] coords = getElementCell(element);
        if (coords != null) {
            removeElement(element, coords[0], coords[1]);
        }
    }

    private void removeElement(GameElement element, int row, int col) {
        grid[row][col].remove(element);
        element.setGrid(null);
        // signal the stage model that element is removed from grid so that callback can be executed
        gameStageModel.removedFromGrid(element, this, row, col);
    }

    /**
     * Move an element from a grid cell to another one.
     * This method has 2 modes to process the move:
     * 1) removes the element from its current cell and use putElement() to assign it to the new cell. It leads to call the callbacks once again,
     * and to set the location of the element, by default at the cell center
     * 2) just reassign the cell that owns the element. No callbacks are called and the lcoation is not updated. Useful for moving sprites owned by a grid.
     *
     * @param element the element to move
     * @param rowDest the new grid row of the element
     * @param colDest the new grid column of the element
     */
    public void moveElement(GameElement element, int rowDest, int colDest, boolean autoLoc) {
        // I have to get the coords of the GameElement in the grid
        int[] coords;
        coords = getElementCell(element);
        if (coords == null) {
            //System.out.println("NO CELL FOR MOVE");
        }
        // if the element is in the grid, and not already in rowDest,colDest cell: move it.
        if ((coords != null) && ((rowDest != coords[0]) || (colDest != coords[1]))) {
            // WARNING : do not call removeElement() to do the job
            // because it would call the onRemove() callback for nothing
            grid[coords[0]][coords[1]].remove(element);
            grid[rowDest][colDest].add(element);
            if (autoLoc) element.setAutoLocChanged(true);
            // signal the stage model that element is moved within the same grid so that callback can be exectued
            gameStageModel.movedInGrid(element, this, rowDest, colDest);
        }
    }

    public void moveElement(GameElement element, int rowDest, int colDest) {
        moveElement(element, rowDest, colDest, true);
    }

    public int[] getElementCell(GameElement element) {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                if (grid[i][j].contains(element)) {
                    return new int[]{i, j};
                }
            }
        }
        return null;
    }

    public List<GameElement> getElements(int row, int col) {
        return grid[row][col];
    }


    /**
     * get the first element that is stored in cell row,col.
     *
     * @param row the cell row
     * @param col the cell col
     * @return the first element stored or null if there are no elements
     */
    public GameElement getFirstElement(int row, int col) {
        // print grid element
        // System.out.println("get first element size " + grid[row][col].size() + " " + row + " " + col);
        if (grid[row][col].size() > 0) {
            return grid[row][col].get(0);
        }
        return null;
    }

    /**
     * get the last element that is stored in cell row,col.
     *
     * @param row the cell row
     * @param col the cell col
     * @return the last element stored or null if there are no elements
     */
    public GameElement getLastElement(int row, int col) {
        if (grid[row][col].size() > 0) {
            return grid[row][col].get(grid[row][col].size() - 1);
        }
        return null;
    }

    /**
     * get the first element that is stored in cell row,col.
     * It is a "convenience" method that is an alias for getFirstElement, in case of a game
     * enforce the fact that there is no more than a single element in a cell at any time.
     *
     * @param row the cell row
     * @param col the cell col
     * @return the first element stored or null if there are no elements
     * @see GridElement#getFirstElement(int, int)
     */
    public GameElement getElement(int row, int col) {
        return getFirstElement(row, col);
    }

    public GameElement getElement(int pawnId, int color, boolean isPawn) {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                if (grid[i][j].size() > 0) {
                    GameElement element = grid[i][j].get(0);
                    if (element instanceof Pawn pawn) {
                        if (pawn.getNumber() == pawnId && pawn.getColor() == color) {
                            return pawn;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * get the element
     *
     * @param row   the cell row
     * @param col   the cell col
     * @param index the index of the element to retrieve in the list store in cell row,col
     * @return the first element stored or null if there are no elements
     */

    public GameElement getElement(int row, int col, int index) {
        if ((grid[row][col].size() == 0) || (index < 0) || (index >= grid[row][col].size())) return null;
        return grid[row][col].get(index);
    }

    /**
     * get the coordinates of the first element that has the specified number and color
     *
     * @param number the number of the element
     * @param color the color of the element
     * @return the coordinates of the element or null if not found
     */
    public int[] getCoords(int number, int color, char king) {
        if (king == 'K' || king == 'k') {
            for (int i = 0; i < nbRows; i++) {
                for (int j = 0; j < nbCols; j++) {
                    if (grid[i][j].size() > 0) {
                        if (grid[i][j].get(0).getKing() == 'K') {
                            // System.out.println("get coords King " + i + " " + j);
                            return new int[]{i, j};
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < nbRows; i++) {
                for (int j = 0; j < nbCols; j++) {
                    if (grid[i][j].size() > 0) {
                        if (grid[i][j].get(0).getNumber() == number && grid[i][j].get(0).getColor() == color) {
                            // System.out.println("get coords Other " + i + " " + j);
                            return new int[]{i, j};
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * determine if there is at least one element stored in cell row,col
     *
     * @param row the cell row
     * @param col the cell col
     * @return true if there is at least one element, otherwise false
     */
    public boolean isElementAt(int row, int col) {
        return grid[row][col].size() > 0;
    }

    /**
     * determine if there is no element stored in the grid
     * @return true if there is no element, otherwise false
     */
    public boolean isEmpty() {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                if (grid[i][j].size() > 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * determine if there is no element stored in cell row,col
     *
     * @param row the cell row
     * @param col the cell col
     * @return true if there is no element, otherwise false
     */
    public boolean isEmptyAt(int row, int col) {
        return grid[row][col].isEmpty();
    }

    // test if element is within this grid
    public boolean contains(GameElement element) {
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                if (grid[i][j].contains(element)) {
                    return true;
                }
            }
        }
        return false;
    }

    // Turns the board into a String
    public String toString() {
        StringBuilder str = new StringBuilder();
        for (int i = 0; i < nbRows; i++) {
            for (int j = 0; j < nbCols; j++) {
                if (grid[i][j].size() > 0) {
                    Pawn pawn = (Pawn) grid[i][j].get(0);
                    switch (pawn.getColor()) {
                        case 0 -> str.append("B");
                        case 1 -> str.append("R");
                        case 2 -> str.append("K");
                        default -> throw new IllegalArgumentException("Invalid color");
                    }
                } else {
                    str.append("_");
                }
            }
        }
        return str.toString();
    }
}
