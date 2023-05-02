import boardifier.control.StageFactory;
import boardifier.model.GameException;
import boardifier.model.Model;
import boardifier.view.View;
import control.BRBController;
import control.BRBDecider;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;



public class BrandubhConsole {
    public static void main(String[] args) {
        int mode = 0;
        try {
            System.out.println("args : "+args[0]);
        }
        catch(Exception e) {
            System.out.println("No args");
        }
        if (args.length == 1) {
            try {
                mode = Integer.parseInt(args[0]);
                if ((mode <0) || (mode>2)) mode = 0;
            }
            catch(NumberFormatException e) {
                mode = 0;
            }
        }
        Model model = new Model();
        if (mode == 0) {
            model.addHumanPlayer("player1");
            model.addHumanPlayer("player2");
        }
        else if (mode == 1) {
            model.addHumanPlayer("player");
            model.addComputerPlayer("computer");
        }
        else if (mode == 2) {
            model.addComputerPlayer("computer2");
            model.addComputerPlayer("computer1");
        }

        StageFactory.registerModelAndView("BRB", "model.BRBStageModel", "view.BRBStageView");
        View BRBView = new View(model);
        BRBController control = new BRBController(model,BRBView);
        control.setFirstStageName("BRB");
        try {
            for (int i=0;i<1000;i++) {
                control.startGame();
                control.stageLoop();
            }
        }
        catch(GameException e) {
            System.out.println("Cannot start the game. Abort");
        }
        control.saveAllFiles();
    }
}