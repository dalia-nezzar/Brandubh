package boardifier.model;

import boardifier.model.animation.Animation;

import java.util.ArrayList;
import java.util.List;


/**
 * abstract class that describes an element of the game, in the largest sense.
 * <p>
 * It can be the board, a sprite, a background, ... It contains the data needed to
 * manage the state and the behaviour of an element of the game. In order to be taken into account
 * during the game, a GameElement must be put in a GameStage (see GameStage.addElement() ). The best way
 * is to instantiate all game elements needed by a game stage, within the method GameStage.createElements().
 * <p>
 * Even if this class is not intended to define the visual aspect (i.e. the look), some of the data are directly used to customize the look:
 * the location in space, the visibility, if it is selected, ... Thus, as long as a GameElement is not associated to an
 * ElementLook, it has no representation in the window game. The "normal" way to do that is:*
 */
public abstract class GameElement {

    // coordinates of the element
    protected double x;
    protected double y;

    public static final int LOCATION_CENTER = 0;
    public static final int LOCATION_TOPLEFT = 1;
    /**
     * define to what point of the bounding box x,y are associated
     * By default, locationType is 0, which corresponds to the bounding box center. Nevertheless,
     * there are some element that are easier to locate using their top-left pixel (e.g. walls).
     * For such element, locationType can be set to 1, using setLocationType()
     */
    protected int locationType;

    /**
     * the game stage that owns this element
     */
    protected GameStageModel gameStageModel;
    /**
     * the visibility of this element
     */
    protected boolean visible;
    /**
     * defines if this element is selected or not
     */
    protected boolean selected;
    /**
     * defines if the element can taken into account when a mouse click occurs on it.
     * If there is a click on the element and clickable is false, then Controller.elementsAt()
     * will not use this element.
     * It is set to true by default.
     */
    protected boolean clickable;
    /**
     * the type of this element.
     * <p>
     * It must be a type that is registered in ElementTypes.
     */
    protected int type;

    /**
     * the grid element that contains this element.
     * <p>
     * It is not mandatory to put an element within a cell of a GridElement. It is more useful
     * for board games to trigger some actions when an element is moved from a cell to another one.
     */
    protected GridElement grid;
    /**
     * the current animation effect associated to this element.
     * <p>
     * It is normally <code>null</code>. But if an animation, like moving the element, changing its look, ...
     * is needed, this attribute allows to retrieve the AnimationStep objects that are created all along the animation
     * time.
     *
     * @see Animation#next()
     */
    protected Animation animation;

    protected boolean locationChanged; // true if x,y changed explcitely (e.g. by an animation)
    protected boolean selectedChanged; // tue if selected changed
    protected boolean visibleChanged; // true if visible changed
    protected boolean lookChanged; // true if the look must change
    protected boolean autoLocChanged; // true if the element is put/moved in a grid and its new localisation at a center of a grid cell must be computed during controller update.

    /**
     * Basic constructor.
     * <p>
     * It calls the general constructor but with a location at 0,0 and a
     * type set to basic.
     *
     * @param gameStageModel The game stage that owns this element.
     */
    public GameElement(GameStageModel gameStageModel) {
        this(0, 0, gameStageModel, ElementTypes.getType("basic"));
    }

    /**
     * It calls the general constructor but with a location at 0,0 and the given type.
     *
     * @param gameStageModel The game stage that owns this element.
     * @param type           The type of this element. Must be a registered type in ElementTypes
     */
    public GameElement(GameStageModel gameStageModel, int type) {
        this(0, 0, gameStageModel, type);
    }

    /**
     * It calls the general constructor but with the location at x,y and a
     * type set to basic.
     *
     * @param x              The x location in space
     * @param y              The y location in space
     * @param gameStageModel The game stage that owns this element.
     */
    public GameElement(double x, double y, GameStageModel gameStageModel) {
        this(x, y, gameStageModel, ElementTypes.getType("basic"));
    }

    /**
     * Set all attributes of the gameElement.
     *
     * @param x              The x location in space
     * @param y              The y location in space
     * @param gameStageModel The game stage that owns this element.
     * @param type           The type of this element. Must be a registered type in ElementTypes
     */
    public GameElement(double x, double y, GameStageModel gameStageModel, int type) {
        this.x = x;
        this.y = y;
        this.gameStageModel = gameStageModel;

        if (ElementTypes.isValid(type)) {
            this.type = type;
        } else {
            this.type = ElementTypes.getType("basic");
        }
        locationType = LOCATION_CENTER;
        visible = true;
        clickable = true;
        grid = null;
        animation = null;

        locationChanged = false;
        selectedChanged = false;
        visibleChanged = false;
        lookChanged = false;
        autoLocChanged = false;
    }

    public void resetChanged() {
        locationChanged = false;
        selectedChanged = false;
        visibleChanged = false;
        lookChanged = false;
        autoLocChanged = false;
    }

    // getters/setters

    /**
     * Get the x position of the element in space
     *
     * @return the x position in space
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y position of the element in space
     *
     * @return the y position in space
     */
    public double getY() {
        return y;
    }

    /**
     * @return the current location in space
     */
    public Coord2D getLocation() {
        return new Coord2D(x, y);
    }

    /**
     * Set the new location in space.
     * <p>
     * If a look is associated to this element (i.e. look is not null), then
     * the main group of the look is moved to the same location. It implies that
     * the scene will be repainted with the new location of the look.
     *
     * @param x The x location in space
     * @param y The y location in space
     */
    public void setLocation(double x, double y) {
        if ((this.x != x) || (this.y != y)) {
            this.x = x;
            this.y = y;
            // if the element in a grid, determine the cell of the new position and assign it with the element.
            locationChanged = true;
        }
    }

    /**
     * Set the location with a relative move of dx,dy.
     * This method can be used when a sprite with a speed expressed in dx,dy must be moved.
     *
     * @param dx
     * @param dy
     */
    public void relativeMove(double dx, double dy) {
        setLocation(x + dx, y + dy);
    }

    /**
     * get the location type of the element
     *
     * @return the location type, 0 for center, and 1 for top-left
     */
    public int getLocationType() {
        return locationType;
    }

    /**
     * set the location type (center or top-left)
     *
     * @param locationType
     */
    public void setLocationType(int locationType) {
        this.locationType = locationType;
    }

    /**
     * @return The game stage that owns this element
     */
    public GameStageModel getGameStage() {
        return gameStageModel;
    }

    /**
     * @return if the element is visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Set the visibility of the element.
     * <p>
     * If a look is associated to this element (i.e. look is not null), then
     * the calls <code>setVisible()</code> on that look. It implies that
     * the scene will be repainted if the visibility of the look changed.
     *
     * @param visible pass <code>true</code> to make the element visible, otherwise <code>false</code>
     */
    public void setVisible(boolean visible) {
        if (this.visible != visible) {
            this.visible = visible;
            visibleChanged = true;
        }
    }

    /**
     * @return <code>true</code> if the element is selected, otherwise <code>false</code>
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * reverse the selected state of the element.
     */
    public void toggleSelected() {
        if (this.selected) {
            unselect();
        } else {
            select();
        }
    }

    /**
     * Set the element as selected.
     * It calls GameStage.setSelected() in order to update its list of selected elements.
     * If a look is associated to this element (i.e. look is not null),
     * then calls <code>setSelected(true)</code> on that look. It implies that the scene may be
     * repainted if the look is different when selected and unselected.
     */
    public void select() {
        if (!this.selected) {
            selectedChanged = true;
        }

        // update game stage model set of selected elements
        gameStageModel.setSelected(this, true);
        this.selected = true;
    }

    /**
     * Set the element as unselected.
     * It calls GameStage.setSelected() in order to update its list of selected elements.
     * If a look is associated to this element (i.e. look is not null),
     * then calls <code>setSelected(true)</code> on that look. It implies that the scene may be
     * repainted if the look is different when selected and unselected.
     */
    public void unselect() {
        if (this.selected) {
            selectedChanged = true;
        }
        // update game stage model set of selected elements
        gameStageModel.setSelected(this, false);
        this.selected = false;
    }

    /**
     * get the clickable state.
     *
     * @return <code>true</code> if the element is clickable, otherwise <code>false</code>
     */
    public boolean isClickable() {
        return clickable;
    }

    /**
     * Set the element to be clickable, or not.
     *
     * @param clickable <code>true</code> if the element must be set as clickable, otherwise <code>false</code>
     */
    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        if (ElementTypes.isValid(type)) {
            this.type = type;
        } else {
            this.type = ElementTypes.getType("basic");
        }
    }

    public GridElement getGrid() {
        return grid;
    }

    public void setGrid(GridElement grid) {
        if (this.grid != grid) {
            this.grid = grid;
        }
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) {
        this.animation = animation;
    }

    /**
     * Remove this element from the current stage.
     * <p>
     * It just calls the method that does the job in GameStage. As a recall:
     * instead of really removing the element from the stage and the nodes of its associated look from the scene
     * it just hides the element and move it to un unreachable location.
     */
    public void removeFromStage() {
        gameStageModel.removeElement(this);
    }

    /**
     * Allows the element to stop itself its current animation.
     * It can be useful if the element reached a state for which it is necessary to stop
     * the current animation. For example, it could be a sprite that is temporarily animated
     * but the animation must be stopped because of a collision.
     */
    public void stopAnimation() {
        if (animation != null) {
            animation.stop();
            animation = null;
        }
    }

    /**
     * Update the element.
     * This method will be called at each frame. It can be used to update the location of the element,
     * its state, or any other attribute that is defined in its class.
     */
    public void update() {
    }

    public boolean isLocationChanged() {
        return locationChanged;
    }

    public boolean isSelectedChanged() {
        return selectedChanged;
    }

    public boolean isVisibleChanged() {
        return visibleChanged;
    }

    public boolean isLookChanged() {
        return lookChanged;
    }

    public void setAutoLocChanged(boolean autoLocChanged) {
        this.autoLocChanged = autoLocChanged;
    }

    public boolean isAutoLocChanged() {
        return autoLocChanged;
    }

    /* *********************************************
       TRAMPOLINE METHODS
       NB: gain access to the current model
     ********************************************* */
    public Model getModel() {
        return gameStageModel.getModel();
    }
}
