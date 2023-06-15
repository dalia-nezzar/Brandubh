package boardifier;
import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

class TestActionPlayer {

    @Test
    void testStart_withDecider() {
        // Arrange
        Model mockModel = Mockito.mock(Model.class);
        Controller mockController = Mockito.mock(Controller.class);
        Decider mockDecider = Mockito.mock(Decider.class);
        ActionList mockPreActions = Mockito.mock(ActionList.class);
        GameAction mockAction = Mockito.mock(GameAction.class);
        List<GameAction> actionPack = new ArrayList<>(Arrays.asList(mockAction));
        ActionList mockActions = new ActionList(false, actionPack);
        when(mockDecider.decider(anyInt())).thenReturn(mockActions);

        ActionPlayer actionPlayer = new ActionPlayer(mockModel, mockController, mockDecider, mockPreActions);

        // Act
        actionPlayer.start(1);

        // Assert
        verify(mockModel, times(1)).setCaptureEvents(false);
        verify(mockAction, times(1)).execute();
        verify(mockModel, times(1)).setCaptureEvents(true);
    }

    @Test
    void testStart_withoutDecider() {
        // Arrange
        Model mockModel = Mockito.mock(Model.class);
        Controller mockController = Mockito.mock(Controller.class);
        GameAction mockAction = Mockito.mock(GameAction.class);
        List<GameAction> actionPack = new ArrayList<>(Arrays.asList(mockAction));
        ActionList mockActions = new ActionList(false, actionPack);

        ActionPlayer actionPlayer = new ActionPlayer(mockModel, mockController, mockActions);

        // Act
        actionPlayer.start(1);

        // Assert
        verify(mockModel, times(1)).setCaptureEvents(false);
        verify(mockAction, times(1)).execute();
        verify(mockModel, times(1)).setCaptureEvents(true);
    }
}

