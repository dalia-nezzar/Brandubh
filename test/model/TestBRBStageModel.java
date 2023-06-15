package model;
import boardifier.model.Model;
import boardifier.model.TextElement;
import control.BRBController;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.awt.*;

public class TestBRBStageModel {

    private BRBStageModel brbStageModel;

    @BeforeEach
    public void setup() {
        brbStageModel = new BRBStageModel("Test Stage", Mockito.mock(Model.class));
    }

    @Test
    public void testGetBoard() {
        BRBBoard board = Mockito.mock(BRBBoard.class);
        brbStageModel.setBoard(board);
        BRBBoard result = brbStageModel.getBoard();
        Assertions.assertEquals(board, result);
    }

    @Test
    public void testGetBlackPawns() {
        Pawn[] pawns = new Pawn[3];
        brbStageModel.setBlackPawns(pawns);
        Pawn[] result = brbStageModel.getBlackPawns();
        Assertions.assertEquals(pawns, result);
    }

    @Test
    public void testGetRedPawns() {
        Pawn[] pawns = new Pawn[3];
        brbStageModel.setRedPawns(pawns);
        Pawn[] result = brbStageModel.getRedPawns();
        Assertions.assertEquals(pawns, result);
    }

    @Test
    public void testGetBlackKingPawns() {
        Pawn[] pawns = new Pawn[2];
        brbStageModel.setBlackKingPawns(pawns);
        Pawn[] result = brbStageModel.getBlackKingPawns();
        Assertions.assertEquals(pawns, result);
    }

    @Test
    public void testGetPlayerName() {
        TextElement playerName = Mockito.mock(TextElement.class);
        brbStageModel.setPlayerName(playerName);
        TextElement result = brbStageModel.getPlayerName();
        Assertions.assertEquals(playerName, result);
    }
}


