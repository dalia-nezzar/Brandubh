package control;

import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.control.ControllerAction;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.view.View;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.stage.StageStyle;
import view.BRBView;

/**
 * A basic action controller that only manages menu actions
 * Action events are mostly generated when there are user interactions with widgets like
 * buttons, checkboxes, menus, ...
 */
public class BRBControllerAction extends ControllerAction implements EventHandler<ActionEvent> {

    // to avoid lots of casts, create an attribute that matches the instance type.
    private BRBView BRBView;
    public int mode = 0;

    public BRBControllerAction(Model model, View view, Controller control) {
        super(model, view, control);
        // take the view parameter ot define a local view attribute with the real instance type, i.e. HoleView.
        BRBView = (BRBView) view;
        // set handlers dedicated to menu items
        setMenuHandlers();

        // If needed, set the general handler for widgets that may be included within the scene.
        // In this case, the current gamestage view must be retrieved and casted to the right type
        // in order to have an access to the widgets, and finally use setOnAction(this).
        // For example, assuming the current gamestage view is an instance of MyGameStageView, which
        // creates a Button myButton :
        // ((MyGameStageView)view.getCurrentGameStageView()).getMyButton().setOnAction(this).

    }

    private void setMenuHandlers() {
        // set event handler on the MenuIntro item
        BRBView.getMenuIntro().setOnAction(e -> {
            control.stopGame();
            BRBView.resetView();
        });
        // set event handler on the MenuQuit item
        BRBView.getMenuQuit().setOnAction(e -> {
            System.exit(0);
        });
        // set event handler on the MenuPvP item
        BRBView.getMenuPvP().setOnAction(e -> {
            mode = 0;
            ActionPlayer.specialAI1 = -1;
            ActionPlayer.specialAI2 = -1;
            startNewGame();
        });
        // set event handler on the MenuPvE item
        BRBView.getMenuPvE().setOnAction(e -> {
            mode = 2;
            ActionPlayer.specialAI1 = -1;
            ActionPlayer.specialAI2 = -1;
            startNewGame();
        });
        // set event handler on the MenuEvP item
        BRBView.getMenuEvP().setOnAction(e -> {
            mode = 1;
            ActionPlayer.specialAI1 = -1;
            ActionPlayer.specialAI2 = -1;
            model.setNextPlayer();
            startNewGame();
        });
        // set event handler on the MenuEvE item
        BRBView.getMenuEvE().setOnAction(e -> {
            mode = 3;
            // Create a dialog box to prompt the user for AI choice for defenders
            Alert defenderDialog = new Alert(Alert.AlertType.CONFIRMATION);
            defenderDialog.setTitle("AI Choice");
            defenderDialog.setHeaderText("You selected AI vs AI mode.");
            defenderDialog.setContentText("Please select an AI that will play as defender:");

            // Add AI options as buttons for defenders
            ButtonType defenderAI1Button = new ButtonType("Random");
            ButtonType defenderAI2Button = new ButtonType("EAT");
            ButtonType defenderAI3Button = new ButtonType("SMART");
            ButtonType defenderCancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

            defenderDialog.getButtonTypes().setAll(defenderAI1Button, defenderAI2Button, defenderAI3Button, defenderCancelButton);

            // Show the defender dialog and wait for user input
            defenderDialog.showAndWait().ifPresent(defenderResult -> {
                int defenderTypeAI = -1;
                if (defenderResult == defenderAI1Button) {
                    defenderTypeAI = 1;
                } else if (defenderResult == defenderAI2Button) {
                    defenderTypeAI = 3;
                } else if (defenderResult == defenderAI3Button) {
                    defenderTypeAI = 2;
                } else {
                    // User clicked Cancel or closed the dialog
                    return; // Exit the method without starting a new game
                }
                ActionPlayer.specialAI1 = defenderTypeAI;
                // Create a dialog box to prompt the user for AI choice for attackers
                Alert attackerDialog = new Alert(Alert.AlertType.CONFIRMATION);
                attackerDialog.setTitle("AI Choice");
                attackerDialog.setHeaderText("You selected AI vs AI mode.");
                attackerDialog.setContentText("Please select an AI that will play as attacker:");

                // Add AI options as buttons for attackers
                ButtonType attackerAI1Button = new ButtonType("Random");
                ButtonType attackerAI2Button = new ButtonType("EAT");
                ButtonType attackerAI3Button = new ButtonType("SMART");
                ButtonType attackerCancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

                attackerDialog.getButtonTypes().setAll(attackerAI1Button, attackerAI2Button, attackerAI3Button, attackerCancelButton);

                // Show the attacker dialog and wait for user input
                attackerDialog.showAndWait().ifPresent(attackerResult -> {
                    int attackerTypeAI = -1;
                    if (attackerResult == attackerAI1Button) {
                        attackerTypeAI = 1;
                    } else if (attackerResult == attackerAI2Button) {
                        attackerTypeAI = 3;
                    } else if (attackerResult == attackerAI3Button) {
                        attackerTypeAI = 2;
                    } else {
                        // User clicked Cancel or closed the dialog
                        return; // Exit the method without starting a new game
                    }
                    ActionPlayer.specialAI2 = attackerTypeAI;
                    ActionPlayer.switchAI = false;
                    // Start a new game based on the selected AI modes for defenders and attackers

                    startNewGame();
                });
            });
        });
        // set event handler on the MenuAI1 item
        BRBView.getMenuAI1().setOnAction(e -> {
            ActionPlayer.typeAI = 1;
            String message = "AI successfully set to Random";
            createAlert(message);
        });
        // set event handler on the MenuAI2 item
        BRBView.getMenuAI2().setOnAction(e -> {
            ActionPlayer.typeAI = 2;
            String message = "AI successfully set to Smart";
            createAlert(message);
        });
        // set event handler on the MenuAI3 item
        BRBView.getMenuAI3().setOnAction(e -> {
            ActionPlayer.typeAI = 3;
            String message = "AI successfully set to EAT";
            createAlert(message);
        });
    }

    public void createAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        // remove the frame around the dialog
        alert.initStyle(StageStyle.UNDECORATED);
        // make it a children of the main game window => it appears centered
        // make it appear on the top right
        alert.setX(view.getStage().getX() + view.getStage().getWidth() - alert.getWidth());
        alert.initOwner(view.getStage());
        // set the message displayed
        alert.setHeaderText(message);
        // display the dialog and wait for the user to close it
        alert.showAndWait();
    }

    public void startNewGame() {
        model.removeAllPlayers();
        if (mode == 0) {
            model.addHumanPlayer("player1");
            model.addHumanPlayer("player2");
        }
        else if (mode == 1) {
            model.addHumanPlayer("player");
            model.addComputerPlayer("computer");
        }
        else if (mode == 2) {
            model.addComputerPlayer("computer");
            model.addHumanPlayer("player");
        }
        else if (mode == 3) {
            model.addComputerPlayer("computer1");
            model.addComputerPlayer("computer2");
        }
        try {
            control.startGame();
            control.nextPlayer();
        }
        catch(GameException err) {
            System.err.println(err.getMessage());
            System.exit(1);
        }
    }

    /**
     * The general handler for action events.
     * this handler should be used if the code to process a particular action event is too long
     * to fit in an arrow function (like with menu items above). In this case, this handler must be
     * associated to a widget w, by calling w.setOnAction(this) (see constructor).
     *
     * @param event An action event generated by a widget of the scene.
     */
    public void handle(ActionEvent event) {

        if (!model.isCaptureActionEvent()) return;
    }
}

