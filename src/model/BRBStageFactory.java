package model;

import boardifier.model.GameElement;
import boardifier.model.GameStageModel;
import boardifier.model.StageElementsFactory;
import boardifier.model.TextElement;

public class BRBStageFactory extends StageElementsFactory {
    private BRBStageModel stageModel;

    public BRBStageFactory(GameStageModel gameStageModel) {
        super(gameStageModel);
        stageModel = (BRBStageModel) gameStageModel;
    }

    @Override
    public void setup() {
        // create the board
        stageModel.setBoard(new BRBBoard(0, 0, stageModel));

        // create the pawns
        Pawn[] blackPawns = new Pawn[4];
        for(int i=0;i<4;i++) {
            blackPawns[i] = new Pawn(i + 1, Pawn.PAWN_BLACK, stageModel);
        }
        stageModel.setBlackPawns(blackPawns);

        Pawn[] redPawns = new Pawn[8];
        for(int i=0;i<8;i++) {
            redPawns[i] = new Pawn(i + 1, Pawn.PAWN_RED, stageModel);
        }
        stageModel.setRedPawns(redPawns);

        Pawn[] blackPawnsKing = new Pawn[1];
        blackPawnsKing[0] = new Pawn(1, Pawn.PAWN_BLACK_KING, stageModel, true);
        stageModel.setBlackKingPawns(blackPawnsKing);

        // put the pawns in the board
        stageModel.getBoard().putElement(blackPawns[0], 3, 2);
        stageModel.getBoard().putElement(blackPawns[1], 2, 3);
        stageModel.getBoard().putElement(blackPawns[2], 3, 4);
        stageModel.getBoard().putElement(blackPawns[3], 4, 3);


        stageModel.getBoard().putElement(redPawns[0], 3, 0);
        stageModel.getBoard().putElement(redPawns[1], 3, 1);

        stageModel.getBoard().putElement(redPawns[2], 0, 3);
        stageModel.getBoard().putElement(redPawns[3], 1, 3);

        stageModel.getBoard().putElement(redPawns[4], 3, 5);
        stageModel.getBoard().putElement(redPawns[5], 3, 6);

        stageModel.getBoard().putElement(redPawns[6], 5, 3);
        stageModel.getBoard().putElement(redPawns[7], 6, 3);

        stageModel.getBoard().putElement(blackPawnsKing[0], 3, 3);

        // create the text
        TextElement text = new TextElement(stageModel.getCurrentPlayerName(), stageModel);
        text.setLocation(10,30);
        text.setLocationType(GameElement.LOCATION_TOPLEFT);
        stageModel.setPlayerName(text);
    }
}
