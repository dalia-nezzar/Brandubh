package control;

import boardifier.model.Model;
import boardifier.view.View;
import model.BRBStageModel;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TestBRBController{
    // assuming we have a suitable class and method to set up the model and other dependencies
    Model model;
    BRBStageModel gameStage;
    BRBController controller;
    View view;

    @Before
    public void setup() {
        model = new Model();
        gameStage = new BRBStageModel("model",model);
        // Do any necessary setup here. Instantiate the model, the gameStage etc.
        view = new View(model);
        controller = new BRBController(model, view);
    }

    @Test
    public void testAnalyseAndPlayWithInvalidPawnIndex() {
        model.setIdPlayer(0);
        String line = "8A1"; // 8 is not a valid pawn index for idPlayer 0
        assertFalse(controller.startanalyseAndPlay(line));
    }

    @Test
    public void testAnalyseAndPlayWithInvalidCoordinates() {
        model.setIdPlayer(0);
        String line = "1H8"; // H8 is out of board
        assertFalse(controller.startanalyseAndPlay(line));
    }

    @Test
    public void testAnalyseAndPlayWithInvalidPlayerId() {
        model.setIdPlayer(1);
        String line = "kA1"; // 'k' is not valid for idPlayer 1
        assertFalse(controller.startanalyseAndPlay(line));
    }




}
