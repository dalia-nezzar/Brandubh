package boardifier.view;

import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.GameException;
import boardifier.model.Model;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import static model.GameSettings.getNumberGame;

public class View {

    /**
     * The model
     */
    protected Model model;

    protected GameStageView gameStageView;

    protected RootPane root;

    /**
     * The menu bar of the game.
     * It MUST BE created in the createMenuBar() method, which by default does nothing.
     * Thus, dev. must create a subclass of View to override createMenuBar().
     */
    protected MenuBar menuBar;

    /**
     * The primary stage.
     *
     * It is used to resize the stage to the current scene size.
     */
    protected Stage stage;
    /**
     * The root pane
     */
    /**
     * The vertical box that contains a menu bar (if needed) and the root pane.
     */
    protected VBox vbox;
    protected RootPane rootPane;
    /**
     * The current scene assigned to the primary stage.
     */
    protected Scene scene;

    public View(Model model) {
        this.model = model;
        root = new RootPane();
    }

    public View(Model model, Stage stage, RootPane rootPane) {
        this.model = model;
        this.stage = stage;
        this.rootPane = rootPane;
        // create the vbox that will be the root node of the scene
        vbox = new VBox();

        // create the menu bar if needed
        createMenuBar();
        if (menuBar != null) {
            vbox.getChildren().add(menuBar);
        }
        vbox.getChildren().add(rootPane);

        // create the scene with the default content
        scene = new Scene(vbox);
        // WARNING: must set the scene and resize the stage BEFORE defining the clipping.
        // Otherwise, dimensions won't be correct.
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
    }
    public Pane getRootPane() {
        return rootPane;
    }

    protected void createMenuBar() {
        menuBar = null;
    }

    public GameStageView getGameStageView() {
        return gameStageView;
    }
    public void resetView() {
        rootPane.resetToDefault();
        if (scene != null) {
            // detach the current vbox as a root node of the current scene
            // so that it can be reused for the new scene.
            scene.setRoot(new Group());
        }
        // create the scene
        scene = new Scene(vbox);
        // WARNING: must set the scene and resize the stage BEFORE defining the clipping.
        // Otherwise, dimensions won't be correct.
        stage.setScene(scene);
        stage.sizeToScene();
        stage.setResizable(false);
        // set the clipping area with the boundaries of root pane.
        Rectangle r = new Rectangle(rootPane.getWidth(), rootPane.getHeight());
        rootPane.setClip(r);
    }

    /* ***************************************
       TRAMPOLINE METHODS
    **************************************** */
    public ElementLook getElementLook(GameElement element) {
        if (gameStageView == null) return null;
        return gameStageView.getElementLook(element);
    }
    public GridLook getElementGridLook(GameElement element) {
        return (GridLook)getElementLook(element.getGrid());
    }

    public void update() {
        gameStageView.update();
        // by default, would update the root pane and then print it
        root.udpate(gameStageView);
        int nbPartie = getNumberGame();
        if (nbPartie <= 1000) root.print();
    }

    public void setView(GameStageView gameStageView) {
        if (Controller.gVersion) rootPane.init(gameStageView);
        //NB: gameStageView may be null if there is no game stage view to draw (cf. SimpleTextView)
        this.gameStageView = gameStageView;
        if (!Controller.gVersion) return;
        // detach the current vbox as a root node of the current scene
        // so that it can be reused for the new scene.
        scene.setRoot(new Group());
                /* create the new scene with vbox as a root node, and if specified the
                   dimensions.
                 */
        if ((this.gameStageView != null) && (this.gameStageView.getWidth() != -1) && (this.gameStageView.getHeight() != -1)) {
            double h = 0;
            if (menuBar != null) h = menuBar.getHeight();
            scene = new Scene(vbox, this.gameStageView.getWidth(), h+ this.gameStageView.getHeight());
            // set the clipping area of the root pane with the given size of the stage
            // So, if there are shape that overlap with these boundaries, they won't show totally.
            Rectangle r = new Rectangle(this.gameStageView.getWidth(), this.gameStageView.getHeight());
            rootPane.setClip(r);
            stage.setScene(scene);
            stage.sizeToScene();
        }
        else {
            scene = new Scene(vbox);
            // WARNING: must set the scene and resize the stage BEFORE defining the clipping.
            // Otherwise, dimensions won't be correct.
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setResizable(false);
            // set the clipping area with the boundaries of root pane.
            Rectangle r = new Rectangle(rootPane.getWidth(), rootPane.getHeight());
            rootPane.setClip(r);
        }
    }

    public Object getView() {
        return gameStageView;
    }
}
