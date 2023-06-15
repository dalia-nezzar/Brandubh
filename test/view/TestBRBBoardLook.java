package view;
import boardifier.model.GameElement;
import org.junit.Test;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import model.BRBBoard;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class TestBRBBoardLook {

    @Test
    public void testOnChange() {
        // Créer une instance de BRBBoardLook avec une taille de 100 et un GameElement fictif
        int size = 100;
        GameElement element = mock(BRBBoard.class);
        BRBBoardLook boardLook = new BRBBoardLook(size, element);

        // Créer une matrice de booléens pour les cellules atteignables
        boolean[][] reachableCells = {
                {true, false, false},
                {false, true, false},
                {false, false, true}
        };

        // Définir le comportement de l'élément mock pour retourner la matrice de cellules atteignables
        when(((BRBBoard) element).getReachableCells()).thenReturn(reachableCells);

        // Appeler la méthode onChange()
        boardLook.onChange();

        // Vérifier que les cellules atteignables ont les propriétés correctement mises à jour
        Rectangle[][] cells = boardLook.getCells();
        assertEquals(7, cells.length);
        assertEquals(7, cells[0].length);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (reachableCells[i][j]) {
                    assertEquals(3, cells[i][j].getStrokeWidth(), 0.001);
                    assertEquals(10, cells[i][j].getStrokeMiterLimit(), 0.001);
                    assertEquals(StrokeType.CENTERED, cells[i][j].getStrokeType());
                    assertEquals(Color.valueOf("0x333333"), cells[i][j].getStroke());
                } else {
                    assertEquals(0, cells[i][j].getStrokeWidth(), 0.001);
                }
            }
        }
    }
}

