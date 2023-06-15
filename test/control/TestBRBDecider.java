package control;

import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.view.View;
import model.BRBBoard;
import model.BRBStageModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TestBRBDecider {
    @Test
    void testDecideEAT() {
        // Arrange
        Model mockModel = mock(Model.class);
        BRBStageModel mockStage = mock(BRBStageModel.class);
        BRBBoard mockBoard = mock(BRBBoard.class);
        GameElement mockPawn = mock(GameElement.class);
        Controller mockController = mock(Controller.class);

        int[] coords = {};  // ou toute autre valeur pertinente
        when(mockBoard.getCoords(anyInt(), anyInt(), anyChar())).thenReturn(coords);

        when(mockModel.getGameStage()).thenReturn(mockStage);
        when(mockStage.getBoard()).thenReturn(mockBoard);
        when(mockBoard.getPawns(anyInt())).thenReturn(Arrays.asList(mockPawn));
        when(mockBoard.computeValidCells(anyInt(), anyInt(), anyBoolean())).thenReturn(new ArrayList<Point>());
        when(mockBoard.getPawnsToRemove(anyInt(), anyInt(), anyInt())).thenReturn(new ArrayList<GameElement>());

        BRBDecider decider = new BRBDecider(mockModel, mockController);

        // Act
        ActionList result = decider.decideEAT();

        // Assert
        assertNotNull(result); // replace this with more specific checks based on your requirements
    }

}
