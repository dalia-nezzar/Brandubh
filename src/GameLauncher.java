import boardifier.control.StageFactory;
import boardifier.model.Model;
import control.BRBController;
import javafx.application.Application;
import javafx.stage.Stage;
import control.BRBController;
import boardifier.control.StageFactory;
import boardifier.model.Model;
import javafx.application.Application;
import javafx.stage.Stage;
//import view.HoleRootPane;
//import view.HoleView;
import view.BRBStageView;
import java.util.Scanner;

public class GameLauncher extends Application {
    public static void main(String[] args) {
        System.out.println("Choose the game version:");
        System.out.println("1. Console Version");
        System.out.println("2. Graphical Version");

        String choice = BRBController.input.nextLine().replaceAll("\\D+", "");
        int choiceInt = Integer.parseInt(choice);

        if (choiceInt == 1) {
            // Execute the console version
            BrandubhConsole game = new BrandubhConsole();
            game.start(args);
        } else if (choiceInt == 2) {
            // Execute the graphical version
            launch(args);
        } else {
            System.out.println("Invalid choice. Exiting the game.");
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

        // create the global model
        Model model = new Model();
        int mode = 0;
        // add some players taking mode into account
        if (mode == 0) {
            model.addHumanPlayer("player1");
            model.addHumanPlayer("player2");
        }
        else if (mode == 1) {
            model.addHumanPlayer("player");
            model.addComputerPlayer("computer");
        }
        else if (mode == 2) {
            model.addComputerPlayer("computer1");
            model.addComputerPlayer("computer2");
        }
        // register a single stage for the game, called hole
        StageFactory.registerModelAndView("hole", "model.HoleStageModel", "view.HoleStageView");
        // create the root pane, using the subclass HoleRootPane
        //HoleRootPane rootPane = new HoleRootPane();
        // create the global view.
        //HoleView view = new HoleView(model, stage, rootPane);
        // create the controllers.
        //HoleController control = new HoleController(model,view);
        // set the name of the first stage to create when the game is started
        //control.setFirstStageName("hole");
        // set the stage title
        stage.setTitle("The Hole");
        // show the JavaFx main stage
        stage.show();
        //view.resetView();
    }
}
