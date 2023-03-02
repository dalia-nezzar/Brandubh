package view;

import boardifier.model.GridElement;
import boardifier.view.GridLook;

public class PawnPotLook extends GridLook {

    public PawnPotLook(int cellWidth, int cellHeight, GridElement gridElement) {
        super(cellWidth, cellHeight, gridElement, -1, false);
    }

    protected void createShape() {
        // draw cells
        GridElement gridElement = (GridElement) element;
        int nbRows = gridElement.getNbRows();
        // start by drawing the border of each cell, which will be change after
        for (int i = 0; i < nbRows; i++) {
            //top-left corner
            shape[i * cellHeight][0] = "\u250F";
            // top-right corner
            shape[i * cellHeight][cellWidth] = "\u2513";
            //bottom-left corner
            shape[(i + 1) * cellHeight][0] = "\u2517";
            // bottom-right corner
            shape[(i + 1) * cellHeight][cellWidth] = "\u251B";

            for (int k = 1; k < cellWidth; k++) {
                shape[i * cellHeight][k] = "\u2501";
                shape[(i + 1) * cellHeight][k] = "\u2501";
            }
            // draw left & righ vertical lines
            for (int k = 1; k < cellHeight; k++) {
                shape[i * cellHeight + k][0] = "\u2503";
                shape[i * cellHeight + k][cellWidth] = "\u2503";
            }
        }
        // change intersections on first & last vert. border
        for (int i = 1; i < nbRows; i++) {
            shape[i * cellHeight][0] = "\u2523";
            shape[i * cellHeight][cellWidth] = "\u252B";
        }
    }
}