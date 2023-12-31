package boardifier.view;

import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.GridElement;
import boardifier.model.Coord2D;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class GridLook extends ElementLook {

    protected int cellWidth;
    protected int cellHeight;
    protected int borderWidth;
    protected GridGeometry geometry;
    protected String borderColor;
    protected boolean showCoords;

    public GridLook(int width, int height, int cellWidth, int cellHeight, int borderWidth, String borderColor, GameElement element) {
        super(element);
        this.width = width;
        this.height = height;
        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.borderWidth = borderWidth;
        this.borderColor = borderColor;

        if (borderWidth > 0) {
            Rectangle back = new Rectangle(width, height, Color.valueOf(borderColor));
            addNode(back);
        }

        geometry = new GridGeometry(this);
        //System.out.println("GridLook height " + height + " width " + width);
    }

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
        //System.out.println("GridLook.createShape");
        //System.out.println("shape " + shape);
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
        // change the color of the corners to green
        for(int i=0;i<shape.length;i++) {
            for(int j=0;j<shape[i].length;j++) {
                // Select only the corners
                if ((i < 2 &&  j < 4)
                        || (i < 2 && j > 24 && j < 29)
                        || (i > 12 && i < 15 && j < 4)
                        || (i > 12 && i < 15 && j > 24 && j < 29)) {
                    shape[i][j] = "\u001B[32m" + shape[i][j] + "\u001B[0m";
                }
            }
        }
        // change the color of ONLY the central border to yellow
        for(int i=0;i<shape.length;i++) {
            for(int j=0;j<shape[i].length;j++) {
                // Select only the middle cell
                if ((i > 5 && i < 9)
                        && (j > 11 && j < 17)
                        && i != 7
                        && j != 13 && j != 14 && j != 15) {
                    shape[i][j] = "\u001B[33m" + shape[i][j] + "\u001B[0m";
                }
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

    @Override
    public void onChange() {

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

    /**
     * Get the row,col of a cell in this grid from a location in the whole scene.
     * @param p the location in the scene (including a menubar if it exists)
     * @return the cell row and col in the grid
     */
    public int[] getCellFromSceneLocation(Coord2D p) {
        // get the group node that contains the shapes/nodes of this grid and get the coordinates of p within this group
        Point2D inMyGroup = getGroup().sceneToLocal(p.getX(), p.getY());

        double cellWidth = getGroup().getBoundsInLocal().getWidth() / 7; // Assuming 7 columns
        double cellHeight = getGroup().getBoundsInLocal().getHeight() / 7; // Assuming 7 rows

        int col = (int) (inMyGroup.getX() / cellWidth);
        int row = (int) (inMyGroup.getY() / cellHeight);

        // Check if the clicked location is outside the grid
        if (row < 0 || row >= 7 || col < 0 || col >= 7) {
            return null; // Return null or handle the out-of-bounds case appropriately
        }

        return new int[]{col, row};
    }

    /* default computation, may be overridden in subclasses :
           just divide the width,height by  cellWidth,cellHeight to find the correct cell
         */
    public int[] getCellFromLocalLocation(double x, double y) {
        if ((x < 0) || (x >= width) || (y < 0) || (y >= height)) return null;
        // must remove the border width
        x = x-borderWidth;
        y = y-borderWidth;
        int[] tab = {(int)y / cellHeight, (int)x / cellWidth}; // row first, columns in second
        return tab;
    }

    public GridGeometry getGeometry() {
        return geometry;
    }
}
