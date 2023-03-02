package view;

import boardifier.model.GameElement;
import boardifier.view.ConsoleColor;
import boardifier.view.ElementLook;
import model.Pawn;

public class PawnLook extends ElementLook {

    public PawnLook(GameElement element) {
        super(element, 1, 1);
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

    @Override
    public void onLookChange() {
        // do nothing since a pawn never change of aspect
    }
}
