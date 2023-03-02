package boardifier.view;

import boardifier.model.GridElement;
import boardifier.model.Coord2D;

public class GridLook extends ElementLook {

    protected int cellWidth;
    protected int cellHeight;
    protected int borderWidth;
    protected GridGeometry geometry;
    protected boolean showCoords;

    public GridLook(int cellWidth, int cellHeight, GridElement gridElement, int depth, boolean showCoords) {
        // there is a +2 to be able to put the rigth/bottom border and the lettering of the cells
        super(gridElement);
        this.depth = depth;
        this.showCoords = showCoords;
        int margin = 1;
        if (showCoords) margin++;
        setSize(cellWidth*gridElement.getNbCols()+margin, cellHeight*gridElement.getNbRows()+margin);
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        geometry = new GridGeometry(this);
        createShape();
    }

    /**
     * createShape() provides a default look for the grid. It can be overriden in subclasses.
     */
    protected void createShape() {
        // draw cells
        GridElement gridElement = (GridElement) element;
        int nbRows = gridElement.getNbRows();
        int nbCols = gridElement.getNbCols();
        // start by drawing the border of each cell, which will be change after
        for(int i=0;i<nbRows;i++) {
            for(int j=0;j<nbCols;j++) {
                //top-left corner
                shape[i*cellHeight][j*cellWidth] = "\u2554";
                // top-right corner
                shape[i*cellHeight][(j+1)*cellWidth] = "\u2557";
                //bottom-left corner
                shape[(i+1)*cellHeight][j*cellWidth] = "\u255A";
                // bottom-right corner
                shape[(i+1)*cellHeight][(j+1)*cellWidth] = "\u255D";

                for(int k=1;k<cellWidth;k++) {
                    shape[i*cellHeight][j*cellWidth+k] = "\u2550";
                    shape[(i+1)*cellHeight][j*cellWidth+k] = "\u2550";
                }
                // draw left & righ vertical lines
                for(int k=1;k<cellHeight;k++) {
                    shape[i*cellHeight+k][j*cellWidth] = "\u2551";
                    shape[i*cellHeight+k][(j+1)*cellWidth] = "\u2551";
                }
            }
        }
        // change intersections on first & last hori. border
        for (int j = 1; j < nbCols; j++) {
            shape[0][j*cellWidth] = "\u2566";
            shape[nbRows*cellHeight][j*cellWidth] = "\u2569";
        }
        // change intersections on first & last vert. border
        for (int i = 1; i < nbRows; i++) {
            shape[i*cellHeight][0] = "\u2560";
            shape[i*cellHeight][nbCols*cellWidth] = "\u2563";
        }
        // change intersections within
        for (int i = 1; i < nbRows; i++) {
            for (int j = 1; j < nbCols; j++) {
                shape[i*cellHeight][j*cellWidth] = "\u256C";
            }
        }
        // draw the coords, if needed
        if (showCoords) {
            for (int i = 0; i < nbRows; i++) {
                shape[(int) ((i + 0.5) * cellHeight)][nbCols * cellWidth + 1] = String.valueOf(i+1);
            }
            for (int j = 0; j < nbCols; j++) {
                char c = (char) (j + 'A');
                shape[nbRows * cellHeight + 1][(int) ((j + 0.5) * cellWidth)] = String.valueOf(c);
            }
        }
    }

    public int getCellWidth() {
        return cellWidth;
    }

    public int getCellHeight() {
        return cellHeight;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    @Override
    public void onLookChange() {
        createShape();
    }

    /* *********************************************
           TRAMPOLINE METHODS
           NB: gain access to the current grid geometry
         ********************************************* */
    /*Law of Demeter*/
    public Coord2D getRootPaneLocationForCellCenter(int row, int col) {
        return geometry.getRootPaneLocationForCellCenter(row, col);
    }
    public Coord2D getRootPaneLocationForCell(int row, int col, int position) {
        return geometry.getRootPaneLocationForCell(row, col, position);
    }
}
