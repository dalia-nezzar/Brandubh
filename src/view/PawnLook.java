package view;

import boardifier.model.GameElement;
import boardifier.view.ConsoleColor;
import boardifier.view.ElementLook;
import javafx.geometry.Bounds;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.Pawn;

public class PawnLook extends ElementLook {
    private Circle circle;
    /*
    public PawnLook(GameElement element) {
        //super(element, 1, 1);
        super(element);
        Pawn pawn = (Pawn)element;
        if (pawn.getColor() == Pawn.PAWN_BLACK) {
            shape[0][0] = ConsoleColor.WHITE + ConsoleColor.BLACK_BACKGROUND + pawn.getNumber() + ConsoleColor.RESET;
        }
        else if (pawn.getColor() == Pawn.PAWN_BLACK_KING){
            shape[0][0] = ConsoleColor.BLACK_BACKGROUND_BRIGHT + ConsoleColor.BLACK_BACKGROUND + "K" + ConsoleColor.RESET;
        }
        else {
            shape[0][0] = ConsoleColor.BLACK + ConsoleColor.RED_BACKGROUND + pawn.getNumber() + ConsoleColor.RESET;
        }
    }

     */

    public PawnLook(int radius, GameElement element) {
        super(element);
        Pawn pawn = (Pawn)element;
        circle = new Circle();
        circle.setRadius(radius);
        if (pawn.getColor() == Pawn.PAWN_BLACK) {
            circle.setFill(Color.BLACK);
        }
        else {
            circle.setFill(Color.RED);
        }

        circle.setCenterX(radius);
        circle.setCenterY(radius);
        addShape(circle);
        // NB: text won't change so no need to put it as an attribute
        Text text = new Text(String.valueOf(pawn.getNumber()));
        text.setFont(new Font(24));
        if (pawn.getColor() == Pawn.PAWN_BLACK) {
            text.setFill(Color.valueOf("0xFFFFFF"));
        }
        else {
            text.setFill(Color.valueOf("0x000000"));
        }
        Bounds bt = text.getBoundsInLocal();
        text.setX(radius - bt.getWidth()/2);
        text.setY(text.getBaselineOffset()+ bt.getHeight()/2-3);
        addShape(text);
    }

    @Override
    public void onLookChange() {
        // do nothing since a pawn never change of aspect
    }

    @Override
    public void onSelectionChange() {
        Pawn pawn = (Pawn)getElement();
        if (pawn.isSelected()) {
            circle.setStrokeWidth(3);
            circle.setStrokeMiterLimit(10);
            circle.setStrokeType(StrokeType.CENTERED);
            circle.setStroke(Color.valueOf("0x333333"));
        }
        else {
            circle.setStrokeWidth(0);
        }
    }

    @Override
    public void onChange() {
    }
}
