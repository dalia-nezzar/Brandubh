package model;

import boardifier.model.ElementTypes;
import boardifier.model.GameElement;
import boardifier.model.GameStageModel;
import boardifier.model.animation.AnimationStep;
import boardifier.view.GridGeometry;

public class Pawn extends GameElement {

    private int number;
    private int color;
    private char king;
    private boolean captured = false;
    public static int PAWN_BLACK = 0;
    public static int PAWN_RED = 1;
    public static int PAWN_BLACK_KING = 2;

    public Pawn(int number, int color, GameStageModel gameStageModel) {
        super(gameStageModel);
        // registering element types defined especially for this game
        ElementTypes.register("pawn",50);
        type = ElementTypes.getType("pawn");
        this.number = number;
        this.color = color;
        this.king = ' ';
    }

    public Pawn(int number, int color, GameStageModel gameStageModel, boolean king) {
        super(gameStageModel);
        // registering element types defined especially for this game
        ElementTypes.register("pawn",50);
        type = ElementTypes.getType("pawn");
        this.number = number;
        this.color = color;
        if (king) {
            this.king = 'K';
        } else {
            this.king = ' ';
        }
    }

    public int getNumber() {
        return number;
    }

    public int getColor() {
        return color;
    }

    public char getKing() {
        return king;
    }

    public boolean isCaptured() {
        return captured;
    }

    public void capture() {
        this.captured = true;
    }

    public boolean isKing() {
        return king == 'K';
    }
}
