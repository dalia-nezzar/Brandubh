package boardifier.view;

import boardifier.model.GameElement;

import boardifier.model.Model;

public class View {

    /**
     * The model
     */
    protected Model model;

    protected GameStageView gameStageView;

    protected RootPane root;

    public View(Model model) {
        this.model = model;
        root = new RootPane();
    }

    public GameStageView getGameStageView() {
        return gameStageView;
    }

    public void setView(GameStageView gameStageView)  {
        this.gameStageView = gameStageView;
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
        root.print();
    }
}
