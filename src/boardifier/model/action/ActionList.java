package boardifier.model.action;

import boardifier.model.GameElement;

import java.util.ArrayList;
import java.util.List;

public class ActionList {

    protected  List<List<GameAction>> actions;
    protected List<GameAction> currentPack;
    protected boolean doNextPlayer; // if true ActionPlayer will trigger a nextPlayer event after all action have been played

    public ActionList(boolean doNextPlayer) {
        actions = new ArrayList<>();
        currentPack = null;
        this.doNextPlayer = doNextPlayer;
    }

    public void addActionPack() {
        currentPack = new ArrayList<>();
        actions.add(currentPack);
    }

    public void addSingleAction(GameAction gameAction) {
        List<GameAction> list = new ArrayList<>();
        list.add(gameAction);
        actions.add(list);
    }

    public void addPackAction(GameAction gameAction) {
        currentPack.add(gameAction);
    }

    public List<List<GameAction>> getActions() {
        return actions;
    }

    public boolean mustDoNextPlayer() {
        return doNextPlayer;
    }
}
