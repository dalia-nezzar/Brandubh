package boardifier.model.action;

import boardifier.model.GameElement;
import boardifier.model.Model;

public class RemoveAction extends GameAction {

    public RemoveAction(Model model, GameElement element) {
        super(model, element, "none");

        createAnimation();
    }

    public void execute() {
        element.removeFromStage();
        onEndCallback.execute();
    }

    public void createAnimation() {
    }
}
