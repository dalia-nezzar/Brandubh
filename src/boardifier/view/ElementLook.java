package boardifier.view;

import boardifier.model.GameElement;

public abstract class ElementLook {

    protected GameElement element;
    protected String[][] shape; // a buffer of String that is used to store the visual aspect of the element
    protected int width; // the width of the view port
    protected int height; // the height of the viewport
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
        if (width < 0) width = 0;
        if (height < 0) height = 0;
        this.width = width;
        this.height = height;
        shape = new String[height][width];
        clearShape();
        this.depth = depth;
    }

    public ElementLook(GameElement element, int width, int height) {
        this(element, width, height, 0);
    }
    public ElementLook(GameElement element) {
        this(element, 0,0, 0);
    }

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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public GameElement getElement() {
        return element;
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

    // by default, just clear the shape if the element is not visible.
    public void onVisibilityChange() {
        boolean visible = element.isVisible();
        if (!visible) {
            clearShape();
        }
        else {
            onLookChange();
        }
    }

    /* must be defined in subclasses to tell what to do when the visual aspect of the element
       changes because of changes in the model associated.
       This method will be called automatically each time the game is displayed on screen
       but only if the attribute lookChanged of the element is true (cf. update() in GameStageView)
     */
    public abstract void onLookChange();

}
