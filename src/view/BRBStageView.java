package view;

import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;
import boardifier.view.ElementLook;
import boardifier.view.GameStageView;
import boardifier.view.GridLook;
import boardifier.view.TextLook;
import model.BRBStageModel;

import javax.lang.model.element.Element;
import javax.naming.ldap.Control;

public class BRBStageView extends GameStageView {
    public BRBStageView(String name, GameStageModel gameStageModel) {
        super(name, gameStageModel);
    }

    @Override
    public void createLooks() {
        BRBStageModel model = (BRBStageModel)gameStageModel;

        //addLook(new GridLook(4, 2, model.getBoard(), -4, true));
        //Todo i have really not idea where what how
        //addLook(new BRBBoardLook(320, model.getBoard()));
        if (!Controller.gVersion) addLook(new GridLook(4, 2, model.getBoard(), -4, true));
        else addLook(new BRBBoardLook(320, model.getBoard()));

        if (!Controller.gVersion) addLook(new GridLook(4, 2, model.getBoard(), -4, true));
        else addLook(new BRBBoardLook(320, model.getBoard()));

        for(int i=0;i<4;i++) {
            if (!Controller.gVersion) addLook(new PawnLook(model.getBlackPawns()[i]));
            else addLook(new PawnLook(25, model.getBlackPawns()[i]));
        }
        for(int i=0;i<8;i++) {
            if (!Controller.gVersion) addLook(new PawnLook(model.getRedPawns()[i]));
            else addLook(new PawnLook(25, model.getRedPawns()[i]));
        }

        if (!Controller.gVersion) addLook(new PawnLook(model.getBlackKingPawns()[0]));
        else {
            addLook(new PawnLook(25, model.getBlackKingPawns()[0]));
            addLook(new TextLook(24, "0x000000", model.getPlayerName()));
        }
    }
}
