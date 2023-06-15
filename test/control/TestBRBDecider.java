package control;
import boardifier.model.TextElement;
import model.BRBStageModel;
import model.Pawn;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
// mockito
import org.mockito.Mockito;
import model.BRBBoard;

public class TestBRBDecider{

    @Test
    public void stageModelTest() {
        BRBStageModel stage = Mockito.mock(BRBStageModel.class);
        BRBBoard board = Mockito.mock(BRBBoard.class);
        Pawn[] pawns = new Pawn[3];
        Pawn[] pawns2 = new Pawn[3];
        Pawn[] pawns3 = new Pawn[2];
        TextElement playerName = Mockito.mock(TextElement.class);
        Mockito.when(stage.getBoard()).thenReturn(board);
        Mockito.when(stage.getBlackPawns()).thenReturn(pawns);
        Mockito.when(stage.getRedPawns()).thenReturn(pawns2);
        Mockito.when(stage.getBlackKingPawns()).thenReturn(pawns3);
        Mockito.when(stage.getPlayerName()).thenReturn(playerName);
    }
}
