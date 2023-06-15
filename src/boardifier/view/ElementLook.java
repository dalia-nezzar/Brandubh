package boardifier.view;

import boardifier.model.Coord2D;
import boardifier.model.GameElement;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.List;

public abstract class ElementLook {
    /**
     * The nodes constituting that look must be gathered within a group.*
     */
    private final Group group;

    protected GameElement element;
    protected String[][] shape; // a buffer of String that is used to store the visual aspect of the element
    protected int width; // the width of the view port
    protected int height; // the height of the viewport
    /**
     * The list of the shapes that are used to define the look.
     * In order to have a better collision detection, all nodes that are shapes
     * should be added to the group with addShape(), instead of addNode().
     * Meanwhile, those added with addNode() imply to create a rectangle shape
     * that is added to shapes. This rectangle has the dimensions of the bounding box of the
     * node.
     *
     */
    private final List<Shape> shapes;
    /**
     * the depth to enforce a particular order when painting the looks associated to game elements.
     *
     * By default, all elements are at depth 0 but it can set to a negative value.
     * The behavior is to show the look of elements at depth -1 below those at depth 0, -2 below -1, ...
     * The look of elements at the same depth are painted in the order they are added to the root pane
     */
    protected int depth;
    public ElementLook(GameElement element, int width, int height, int depth) {
        this.element = element;
        group = new Group();
        shapes = new ArrayList<>();
        if (width < 0) width = 0;
        if (height < 0) height = 0;
        this.width = width;
        this.height = height;
        shape = new String[height][width];
        clearShape();
        this.depth = depth;
        // move the group to the x,y position of the element in the root pane
        onLocationChange();
    }

    public ElementLook(GameElement element, int depth) {
        this.element = element;
        //System.out.println("element = " + element);
        group = new Group();
        shapes = new ArrayList<>();
        this.width = 1;
        this.height = 1;
        shape = new String[height][width];
        clearShape();
        this.depth = depth;
        // move the group to the x,y position of the element in the root pane
        onLocationChange();
    }

    public ElementLook(GameElement element) {
        this(element, 0);
    }

    public GameElement getElement() {
        return element;
    }

    public int getDepth() {
        return depth;
    }
    public void setDepth(int depth) {
        this.depth = depth;
    }

    public void clearGroup() {
        group.getChildren().clear();
    }

    public void clearShapes() {
        shapes.clear();
    }
    public void addShape(Shape shape) {
        group.getChildren().add(shape);
        shapes.add(shape);
    }

    public void addNode(Node node) {
        // assuming that node IS NOT a Shape, create a rectangle that matches the bounds
        Bounds b = node.getBoundsInParent();
        Rectangle r = new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
        r.setFill(Color.TRANSPARENT);
        group.getChildren().add(node);
        group.getChildren().add(r);
        shapes.add(r);
    }

    /**
     * move the location of the group within the root pane space, and thus within the scene.
     * This method MUST NEVER be called directly. It is automatically called whenever
     * a game element is moved in space.
     */
    public void onLocationChange() {
        //System.out.println("onLocationChange");
        //System.out.println("element.getLocationType() = " + element.getLocationType());
        if (element.getLocationType() == GameElement.LOCATION_CENTER) {
            Bounds b = group.getBoundsInLocal();
            group.setTranslateX(element.getX() - b.getWidth() / 2);
            group.setTranslateY(element.getY() - b.getHeight() / 2);
        }
        else {
            group.setTranslateX(element.getX());
            group.setTranslateY(element.getY());
        }
    }

    /**
     * show or hide the nodes of this look.
     * This method MUST NEVER be called directly. It is automatically called whenever
     * the visibility of a game element is changed.
     */
    public void onVisibilityChange() {
        boolean visible = element.isVisible();
        for(Node node : group.getChildren()) {
            node.setVisible(visible);
        }
    }

    /**
     * Change the look if the associated game element is selected or unselected.
     * By default, this method does nothing but it can be overridden in subclasses to
     * change the aspect of node when the element is selected.
     */
    public void onSelectionChange() { }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setSize(int width, int height) {
        if (width < 0) width = 0;
        if (height < 0) height = 0;
        this.width = width;
        this.height = height;
        shape = new String[height][width];
        clearShape();
    }

    protected void clearShape() {
        for(int i=0;i<height;i++) {
            for(int j=0;j<width;j++) {
                shape[i][j] = " ";
            }
        }
    }

    public String getShapePoint(int x, int y) {
        if ((x>=0) && (x<width) && (y>=0) && (y<height)) return shape[y][x];
        return null;
    }

    /* must be defined in subclasses to tell what to do when the visual aspect of the element
       changes because of changes in the model associated.
       This method will be called automatically each time the game is displayed on screen
       but only if the attribute lookChanged of the element is true (cf. update() in GameStageView)
     */
    public abstract void onLookChange();

    /**
     * Determine if a point is within the bounds of one of the nodes of this look
     * @param point a point in the scene coordinate space.
     * @return <code>true</code> if it is within, otherwise <code>false</code>.
     */
    public boolean isPointWithin(Coord2D point) {
        for(Node node : group.getChildren()) {
            Bounds b = node.localToScene(node.getBoundsInParent());
            if ( (point.getX() >= b.getMinX()) &&  (point.getX() <= b.getMaxX()) && (point.getY() >= b.getMinY()) && (point.getY() <= b.getMaxY()) ) return true;
        }
        return false;
    }

    public Group getGroup() {
        return group;
    }

    /**
     * Update the look for any reason different from new location, change in visibility or selection.
     * This method MUST NOT be used to update the location, the visibility or the selected aspect.
     *
     */
    public abstract void onChange();
}
