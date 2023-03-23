package view;

import boardifier.model.GameElement;
import boardifier.model.GameStageModel;
import boardifier.view.ElementLook;
import boardifier.view.GameStageView;
import boardifier.view.GridLook;
import model.BRBStageModel;

import javax.lang.model.element.Element;

public class BRBStageView extends GameStageView {
    public BRBStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        BRBStageModel model = (BRBStageModel)gameStageModel;

        addLook(new GridLook(4, 2, model.getBoard(), -4, true));

        for(int i=0;i<4;i++) {
            addLook(new PawnLook(model.getBlackPawns()[i]));
        }
        for(int i=0;i<8;i++) {
            addLook(new PawnLook(model.getRedPawns()[i]));
        }

        addLook(new PawnLook(model.getBlackKingPawns()[0]));
    }
}
