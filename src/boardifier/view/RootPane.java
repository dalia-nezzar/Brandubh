package boardifier.view;

import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.Collections;
import java.util.List;

public class RootPane extends Pane {
    protected GameStageView gameStageView;
    private String[][] viewPort;
    private int width;
    private int height;
    protected Group group; // the group that contains all game elements of the current stage

    public RootPane(int width, int height) {
        this.width = width;
        this.height = height;
        viewPort = new String[height][width];
        clearViewPort();

        this.gameStageView = null;
        group = new Group();
        //setBackground(Background.EMPTY);
        resetToDefault();
    }

    public final void resetToDefault() {
        createDefaultGroup();
        // add the group to the pane
        getChildren().clear();
        getChildren().add(group);
    }

    /**
     * Initialize the content of the group.
     * It takes the elements of the model, which are initialized when starting a game stage.
     * It sorts them so that the element with the highest depth are put in first in the group.
     * So they will be hidden by elements with a lower depth.
     */
    public final void init(GameStageView gameStageView) {
        if (gameStageView != null) {
            this.gameStageView = gameStageView;
            // first sort element by their depth
            Collections.sort(gameStageView.getLooks(), (a, b) -> a.getDepth() - b.getDepth());
            // remove existing children
            group.getChildren().clear();
            // add game element looks
            for (ElementLook look : gameStageView.getLooks()) {
                Group group = look.getGroup();
                this.group.getChildren().add(group);
            }
            // add the group to the pane
            getChildren().clear();
            getChildren().add(group);
        }
    }

    /**
     * create the element of the default group
     * This method can be overriden to define a different visual aspect.
     */
    protected void createDefaultGroup() {
        Rectangle frame = new Rectangle(100, 100, Color.LIGHTGREY);
        // remove existing children
        group.getChildren().clear();
        // adding default ones
        group.getChildren().addAll(frame);
    }

    public RootPane() {
        this(1,1);
    }

    public void clearViewPort() {
        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++) {
                viewPort[i][j] = " ";
            }
        }
    }

    public void udpate(GameStageView gameStageView) {
        // first, determine the size of the view
        int w = 0;
        int h = 0;
        List<ElementLook> looks = gameStageView.getLooks();
        for (ElementLook look : looks) {
            if ((look.width+look.getElement().getX()) > w) {
                w = (int)(look.width + look.getElement().getX());
            }
            if ((look.height+look.getElement().getY()) > h) {
                h = (int)(look.height + look.getElement().getY());
            }
        }
        if ((w != width) || (h != height)) {
            width = w;
            height = h;
            viewPort = new String[height][width];
            clearViewPort();
        }
        // now put looks on the pane
        for (ElementLook look : looks) {
            for(int i=0; i<look.height; i++) {
                for(int j=0; j<look.width; j++) {
                    int x = (int) (look.getElement().getX() + j);
                    int y = (int) (look.getElement().getY() + i);
                    if (x >= 0 && x < viewPort[0].length && y >= 0 && y < viewPort.length) {
                        viewPort[y][x] = look.getShapePoint(j, i);
                    }
                }
            }
        }

    }

    public void print() {
        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++) {
                System.out.print(viewPort[i][j]);
            }
            System.out.println();
        }
    }

    public void update() {
        if (gameStageView == null) return;
        gameStageView.update();
    }
}
