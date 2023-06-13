package boardifier.control;

import boardifier.model.*;
import boardifier.view.ElementLook;
import boardifier.view.GameStageView;
import boardifier.view.GridLook;
import boardifier.view.*;
import javafx.application.Platform;
import javafx.geometry.Bounds;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static boardifier.view.ConsoleColor.*;

public abstract class Controller {
    protected Model model;
    protected View view;
    protected String firstStageName;
    protected Map<GameElement, ElementLook> mapElementLook;
    protected int typeDefenseur = 0;
    protected int typeAttaquant = 1;
    protected ControllerAnimation controlAnimation;
    protected ControllerKey controlKey;
    protected ControllerMouse controlMouse;
    protected ControllerAction controlAction;
    public static boolean gVersion = false;
    private boolean inUpdate;

    public Controller(Model model, View view) {
        this.model = model;
        this.view = view;
        if (Controller.gVersion) controlAnimation = new ControllerAnimation(model,view, this);
        firstStageName = "";
        inUpdate = false;
    }

    public void setControlKey(ControllerKey controlKey) {
        this.controlKey = controlKey;
    }

    public void setControlMouse(ControllerMouse controlMouse) {
        this.controlMouse = controlMouse;
    }

    public void setControlAction(ControllerAction controlAction) {
        this.controlAction = controlAction;
    }

    public void setFirstStageName(String firstStageName) {
        this.firstStageName = firstStageName;
    }

    public void startGame() throws GameException {
        if (firstStageName.isEmpty()) throw new GameException("The name of the first stage have not been set. Abort");
        System.out.println("======================");
        System.out.println(BLUE_BOLD + "THE WAR HAS STARTED" + BLACK);
        System.out.println("======================");
        startStage(firstStageName);
        // Make the second player play first
        model.setNextPlayer();
    }

    /**
     * defines what must be done during a stage
     */
    public abstract void stageLoop();

    /**
     * Start a stage of the game.
     * This method MUST NOT BE called directly, except in the endStage() overrideen method.*
     * @param stageName The name of the stage, as registered in the StageFactory.
     * @throws GameException
     */
    protected void startStage(String stageName) throws GameException {
        if (model.isStageStarted()) { stopStage(); }
        model.reset();
        System.out.println("START STAGE "+stageName);
        // create the model of the stage by using the StageFactory
        GameStageModel gameStageModel = StageFactory.createStageModel(stageName, model);
        // create the elements of the stage by getting the default factory of this stage and giving it to createElements()
        gameStageModel.createElements(gameStageModel.getDefaultElementFactory());
        // create the view of the stage by using the StageFactory
        GameStageView gameStageView = StageFactory.createStageView(stageName, gameStageModel);
        // create the looks of the stage (NB: no factory this time !)
        gameStageView.createLooks();
        // start the game, from the model point of view.
        model.startGame(gameStageModel);
        // set the view so that the current pane view can integrate all the looks of the current game stage view.
        view.setView(gameStageView);
        if (Controller.gVersion) {
            /* CAUTION: since starting the game implies to
               remove the intro pane from root, then root has no more
               children. It seems that this removal causes a focus lost
               which must be set once again in order to catch keyboard events.
            */
            view.getRootPane().setFocusTraversable(true);
            view.getRootPane().requestFocus();
        }
        // create a map of GameElement <-> ElementLook, that helps the controller in its update() method
        mapElementLook = new HashMap<>();
        for(GameElement element : model.getElements()) {
            ElementLook look = gameStageView.getElementLook(element);
            mapElementLook.put(element, look);
        }

        if (Controller.gVersion) {
            // start the animation controller
            controlAnimation.startAnimation();
        }
    }

    public void stopStage() {
        model.stopStage();
        model.reset();
    }

    /**
     * Execute actions when it is the next player to play
     * By default, this method does nothing because it is useless in sprite games.
     * In board games, it can be overridden in subclasses to compute who is the
     * next player, and then to take actions if needed. For example, a method of the model can be called to update who is the current player.
     * Then, if it is a computer, a Decider object can be used to determine what to play and then to play it.
     */
    public void nextPlayer() {};

    /**
     * Execute actions at the end of the game.
     * This method defines a default behaviour, which is to display a dialog box with the name of the
     * winner and that proposes to start a new game or to quit.
     */
    public void endGame() {

        System.out.println("======================");
        System.out.println(RED_BOLD + "THE WAR HAS ENDED" +BLACK);
        System.out.println("======================");
        if (model.getIdWinner() != -1) {
            System.out.println(model.getPlayers().get(model.getIdWinner()).getName() + " wins! Shall the land of the defeated be in the hands of the true warrior!");
        }
        else {
            System.out.println("Game has been drawn! No victory, nor loss. Just a draw.");
        }
    }

    /**
     * Update model and view.
     * This method MUST NOT BE called directly, and is only called by the ControllerAnimation
     * at each frame. It is used to update the model and then the view.
     * It must be noticed that the process of updating follows a fixed scheme :
     *   - update all game element of the current game stage,
     *   - update the grid cell of element that are in a grid and that have moved in space, and thus may have changed of cell,
     *   - update the looks of all elements, calling dedicated methods according the type indicators of change (location, look, selection, ...),
     *   - reset the change indicators in elements,
     *   - check if the sage is finished,
     *   - check if the game is finished.
     */
    public void update() {
        if (inUpdate) {
            System.err.println("Abnormal case: concurrent updates");
            return;
        }
        inUpdate = true;

        // update the model of all elements :
        if (Controller.gVersion) {
            mapElementLook.forEach((k,v) -> {
                // get the bounds of the look
                Bounds b = v.getGroup().getBoundsInParent();
                // get the geometry of the grid that owns the element, if it exists
                if (k.getGrid() != null) {
                    GridLook look = getElementGridLook(k);
                    k.update(b.getWidth(), b.getHeight(), look.getGeometry());
                }
                else {
                    k.update(b.getWidth(), b.getHeight(), null);
                }
                // if the element must be auto-localized in its cell center
                if (k.isAutoLocChanged()) {
                    setElementLocationToCellCenter(k);
                }
            });
        } else {
            // update the model of all elements :
            mapElementLook.forEach((k,v) -> {
                // update the element if needed
                k.update();
                // if the element was moved within the same grid
                if (k.isAutoLocChanged()) {
                    setElementLocationToCellCenter(k);
                }
            });
        }

        // update the looks
        view.update();
        // reset changed indicators
        mapElementLook.forEach((k,v) -> {
            k.resetChanged();
        });

        if (Controller.gVersion && model.isEndStage()) {
            controlAnimation.stopAnimation();
            Platform.runLater( () -> {
                stopStage();});
        }
        else if (Controller.gVersion && model.isEndGame()) {
            controlAnimation.stopAnimation();
            Platform.runLater( () -> {endGame();});
        }

        inUpdate = false;
    }

    /* ***************************************
       HELPERS METHODS
    **************************************** */

    /**
     * Get the look of a given element
     * @param element the element for which the look is asked.
     * @return an ElementLook object that is the look of the element
     *
     */
    public ElementLook getElementLook(GameElement element) {
        return mapElementLook.get(element);
    }
    /**
     * Get the look of the grid that owns an element
     * @param element the element for which the grid llok is asked.
     * @return an ElementLook object that is the look of the grid that owns the element.
     */
    public GridLook getElementGridLook(GameElement element) {
        return (GridLook) (view.getElementGridLook(element));
    }

    /**
     * Set the location of an element at the center of the cell it is placed.
     * @param element
     */
    public void setElementLocationToCellCenter(GameElement element) {
        if (element.getGrid() == null) return;
        int[] coords = element.getGrid().getElementCell(element); // RECALL: grid is the current grid this element is within
        GridLook gridLook = getElementGridLook(element);
        // get the center of the current cell because we can at least reach this center if Me is not already on it.
        Coord2D center = gridLook.getRootPaneLocationForCellCenter(coords[0], coords[1]);
        element.setLocation(center.getX(), center.getY());
    }

    public void stopGame() {
        controlAnimation.stopAnimation();
        model.reset();
    }

    /**
     * Get all visible and clickable elements that are at a given point in the scene coordinate space.
     * @param point the coordinate of a point
     * @return A list of game element
     */
    public List<GameElement> elementsAt(Coord2D point) {
        List<GameElement> list = new ArrayList<>();
        for(GameElement element : model.getElements()) {
            if ((element.isVisible()) && (element.isClickable())) {
                ElementLook look = mapElementLook.get(element);
                if ((look != null) && (look.isPointWithin(point))) {
                    list.add(element);
                }
            }
        }
        return list;
    }
}
