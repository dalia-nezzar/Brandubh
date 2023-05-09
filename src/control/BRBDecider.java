package control;

import boardifier.control.Controller;
import boardifier.control.Decider;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.action.MoveAction;
import model.BRBBoard;
import model.BRBStageModel;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class BRBDecider extends Decider {

    private static final Random loto = new Random(Calendar.getInstance().getTimeInMillis());

    public BRBDecider(Model model, Controller control) {
        super(model, control);
    }
    public static int nbRDM = 0;
    public static int nbSmart = 0;
    public static int nbOfRemovedPawns = 0;
    @Override
    public ActionList decider(int selected) {
        switch (selected) {
            case 1:
                return decideAleatoire();
            case 2:
                return decideSmart();
            case 3:
                return decide2();
            case 4:
                return decideEAT();
            default:
                return null;
        }
    }

    //@Override
    public ActionList decide2() {
        System.out.println("ActionList is getting executed");
        // do a cast get a variable of the real type to get access to the attributes of HoleStageModel
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        //BRBPawnPot pot = null; // the pot where to take a pawn
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        /*
        if (model.getIdPlayer() == Pawn.PAWN_BLACK) {
            pot = stage.getBlackPot();
        }
        else {
            pot = stage.getRedPot();
        }

        for(int i=0;i<4;i++) {
            Pawn p = (Pawn)pot.getElement(i,0);
            // if there is a pawn in i.
            if (p != null) {
                // get the valid cells
                List<Point> valid = board.computeValidCells(p.getNumber());
                if (valid.size() != 0) {
                    // choose at random one of the valid cells
                    int id = loto.nextInt(valid.size());
                    pawn = p;
                    rowDest = valid.get(id).y;
                    colDest = valid.get(id).x;
                    break; // stop the loop
                }
            }
        }
         */

        // create action list. After the last action, it is next player's turn.
        ActionList actions = new ActionList(true);
        // create the move action, without animation => the pawn will be put at the center of dest cell
        GameAction move = new MoveAction(model, pawn, "BRBboard", rowDest, colDest);
        actions.addSingleAction(move);
        return actions;
    }

    //@Override
    public ActionList decideAleatoire() {
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        // print the board
        //System.out.println(board);
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        // get list of the pawns of the current player
        List<GameElement> pawns = board.getPawns(model.getIdPlayer());
        List<Point> valid = null;
        int id = 0;
        int counter = 0;
        do {
            // Select a pawn at random
            id = loto.nextInt(pawns.size());
            // get the selected pawn
            pawn = pawns.get(id);
            // if isKing(), then char is 'K', else ' '
            char isKing = pawn.isKing() ? 'K' : ' ';
            // get the coords of the given pawn
            int[] coords = board.getCoords(pawn.getNumber(), pawn.getColor(), isKing);
            // get list of valid cells for the given pawn
            valid = board.computeValidCells(coords[0], coords[1], pawn.isKing());
            if (counter > 100) {
                System.out.println("Error: no valid cells");
                throw new RuntimeException("Error: no valid cells");
            }
            counter++;
        } while (valid.size() == 0);
        // choose at random one of the valid cells
        id = loto.nextInt(valid.size());
        rowDest = valid.get(id).y;
        colDest = valid.get(id).x;
        System.out.println("pawn: " + pawn.getNumber() + " rowDest: " + rowDest + " colDest: " + colDest);
        // get pawns to remove
        List<GameElement> toRemove = board.getPawnsToRemove(rowDest, colDest, pawn.getColor());
        // remove them
        board.removePawns(toRemove);

        // create action list. After the last action, it is next player's turn.
        ActionList actions = new ActionList(true);
        // create the move action, without animation => the pawn will be put at the center of dest cell
        GameAction move = new MoveAction(model, pawn, "BRBboard", rowDest, colDest);
        actions.addSingleAction(move);
        return actions;
    }

    //@Override
    public ActionList decideSmart() {
        BRBStageModel stage = (BRBStageModel) model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        // get list of the pawns of the current player
        List<GameElement> pawns = board.getPawns(model.getIdPlayer());
        List<Point> valid = null;
        // array list of possibles moves
        ArrayList<Point> moves = new ArrayList<>();
        // array list of possibles pawns
        ArrayList<GameElement> pawnsToMove = new ArrayList<>();
        int id = 0;
        int highestScore = -1;

        // Get all the pawns for the given player
        for (int i = 0; i < pawns.size(); i++) {
            // get the selected pawn
            pawn = pawns.get(i);
            // if isKing(), then char is 'K', else ' '
            char isKing = pawn.isKing() ? 'K' : ' ';
            // get the coords of the given pawn
            int[] coords = board.getCoords(pawn.getNumber(), pawn.getColor(), isKing);
            // print the coords
            System.out.println("pawn: " + pawn.getNumber() + " row: " + coords[0] + " col: " + coords[1]);
            // get list of valid cells for the given pawn
            valid = board.computeValidCells(coords[0], coords[1], pawn.isKing());
            // print the valid cells one by one
            for (int j = 0; j < valid.size(); j++) {
                System.out.println("valid: " + valid.get(j).y + " " + valid.get(j).x);
                // create a representation of the move in String format
                String combinationToSearch = translator(coords[1], coords[0], valid.get(j).x, valid.get(j).y);
                String combinationToSearchCompressed = BRBController.compressData(combinationToSearch);
                System.out.println(combinationToSearchCompressed);
                // if player is red then search in dataRed.bin, else in dataBlack.bin
                Data data = null;
                int score = 0;
                if (model.getIdPlayer() == 0) {
                    data = searchValue("dataMapPawns.bin", combinationToSearchCompressed);
                    if (data != null) score = score(data.getWCountB(), data.getWCountR());
                } else {
                    data = searchValue("dataMapPawns.bin", combinationToSearchCompressed);
                    if (data != null) score = score(data.getWCountR(), data.getWCountB());
                }
                if (score == highestScore) {
                    // if the score is the same, add the move to the list of possible moves
                    moves.add(new Point(valid.get(j).x, valid.get(j).y));
                    pawnsToMove.add(pawn);

                }
                if (score > highestScore) {
                    pawnsToMove.clear();
                    moves.clear();
                    highestScore = score;
                    rowDest = valid.get(j).y;
                    colDest = valid.get(j).x;
                }
                // print data info
                System.out.println(data);
            }
        }
        // if there is more than one move with the same score, choose one at random
        if (moves.size() > 1) {
            System.out.println("---------------CHOOSING A MOVE AT RANDOM----------------");
            id = loto.nextInt(moves.size());
            rowDest = moves.get(id).y;
            colDest = moves.get(id).x;
            pawn = pawnsToMove.get(id);
            if (highestScore == 0) {
                nbRDM++;
            } else {
                nbSmart++;
            }
        } else {
            System.out.println("---------------CHOOSING THE BEST MOVE----------------");
            nbSmart++;
        }
        List<GameElement> toRemove = board.getPawnsToRemove(rowDest, colDest, pawn.getColor());
        // remove them
        board.removePawns(toRemove);
        // create action list. After the last action, it is next player's turn.
        ActionList actions = new ActionList(true);
        // create the move action, without animation => the pawn will be put at the center of dest cell
        GameAction move = new MoveAction(model, pawn, "BRBboard", rowDest, colDest);
        actions.addSingleAction(move);
        return actions;
    }

    public String translator(int startX, int startY, int destX, int destY) {
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        int startID = startY * 7 + startX;
        int destID = destY * 7 + destX;
        String actualBoard = board.toString();

        char startChar = actualBoard.charAt(startID);
        char destChar = actualBoard.charAt(destID);

        StringBuilder sb = new StringBuilder(actualBoard);
        sb.setCharAt(startID, destChar);
        sb.setCharAt(destID, startChar);

        String translatedBoard = sb.toString();
        //System.out.println(actualBoard);
        //System.out.println(translatedBoard);
        return translatedBoard;
    }

    public int score(int currentPlayerWins, int enemyWins) {
        int score = 0;
        // Give a higher score for a larger lead
        score += (currentPlayerWins - enemyWins) * 100;
        // Give a higher score for a larger number of total wins
        score += (currentPlayerWins + enemyWins) * 1.5;
        return score;
    }

    public static Data searchValue(String namefile, String key) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(namefile);
            File file = new File(namefile);
            long fileSize = file.length();
            int bufferSize = (int) Math.min(fileSize, Integer.MAX_VALUE * 0.75);
            byte[] buffer = new byte[bufferSize];
            int bytesRead;
            Data value;

            while ((bytesRead = fis.read(buffer)) != -1) {
                ByteBuffer bb = ByteBuffer.wrap(buffer, 0, bytesRead);
                while (bb.hasRemaining()) {
                    if (bb.remaining() < 4) {
                        System.out.println("Error: Not enough data in buffer to read the key length");
                        return null;
                    }
                    int keyLength = bb.getInt();
                    byte[] keyBytes = null;
                    if (keyLength <= bb.remaining()) {
                        keyBytes = new byte[keyLength];
                        bb.get(keyBytes);
                    } else {
                        System.out.println("Error: Not enough data in buffer to read the key");
                        return null;
                    }
                    String readKey = new String(keyBytes);
                    if (readKey.equals(key)) {
                        if (bb.remaining() < 4) {
                            System.out.println("Error: Not enough data in buffer to read the value length");
                            return null;
                        }
                        int valueLength = bb.getInt();
                        byte[] valueBytes = null;
                        if (valueLength <= bb.remaining()) {
                            valueBytes = new byte[valueLength];
                            bb.get(valueBytes);
                        } else {
                            System.out.println("Error: Not enough data in buffer to read the value");
                            return null;
                        }
                        ByteArrayInputStream bais = new ByteArrayInputStream(valueBytes);
                        ObjectInputStream ois = new ObjectInputStream(bais);
                        value = (Data) ois.readObject();
                        ois.close();
                        System.out.println("Value found !");
                        return value;
                        // Value found !
                    }
                    if (bb.remaining() < 4) {
                        System.out.println("Error: Not enough data in buffer to skip the value");
                        return null;
                    }
                    int valueLength = bb.getInt();
                    if (valueLength <= bb.remaining()) {
                        bb.position(bb.position() + valueLength);
                    } else {
                        System.out.println("Error: Not enough data in buffer to skip the value");
                        return null;
                    }
                }
            }
            System.out.println("Key not found in map");
            // Key not found in map
            return null;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public ActionList decideEAT() {
        BRBStageModel stage = (BRBStageModel) model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        // get list of the pawns of the current player
        List<GameElement> pawns = board.getPawns(model.getIdPlayer());
        List<Point> valid = null;
        // array list of possibles moves
        ArrayList<Point> moves = new ArrayList<>();
        // array list of possibles pawns
        ArrayList<GameElement> pawnsToMove = new ArrayList<>();
        List<GameElement> toRemove = new ArrayList<>();
        List<GameElement> toRemoveReal = new ArrayList<>();
        int id = 0;
        int highestScore = -1;

        // Get all the pawns for the given player
        for (int i = 0; i < pawns.size(); i++) {
            // get the selected pawn
            pawn = pawns.get(i);
            // if isKing(), then char is 'K', else ' '
            char isKing = pawn.isKing() ? 'K' : ' ';
            // get the coords of the given pawn
            int[] coords = board.getCoords(pawn.getNumber(), pawn.getColor(), isKing);
            // print the coords
            //System.out.println("pawn: " + pawn.getNumber() + " row: " + coords[0] + " col: " + coords[1]);
            // get list of valid cells for the given pawn
            valid = board.computeValidCells(coords[0], coords[1], pawn.isKing());
            // print the valid cells one by one
            for (int j = 0; j < valid.size(); j++) {
                //System.out.println("valid: " + valid.get(j).y + " " + valid.get(j).x);
                toRemove.clear();
                toRemove = board.getPawnsToRemove(valid.get(j).y, valid.get(j).x, pawn.getColor());
                int score = toRemove.size();
                if (score >= highestScore) {
                    toRemoveReal.clear();
                    //Make a copy of toRemove into toRemoveReal
                    toRemoveReal.addAll(toRemove);
                    highestScore = score;
                    pawn = pawns.get(i);
                    rowDest = valid.get(j).y;
                    colDest = valid.get(j).x;
                } else {
                    //System.out.println("score: " + score);
                }
                //System.out.println("highestscore: " + highestScore);
                //print toRemoveReal
                for (int k = 0; k < toRemoveReal.size(); k++) {
                    //System.out.println("toRemoveReal: " + toRemoveReal.get(k).getNumber());
                }
                //print toRemoveReal.size
                //System.out.println("toRemoveReal.size: " + toRemoveReal.size());
            }
        }
        // if toRemove.size == 0, then no pawn can be eaten
        // therefore, return execute decideAleatoire()
        if (toRemoveReal.size() == 0) {
            nbRDM++;
            return decideAleatoire();
        }
        nbSmart++;
        board.removePawns(toRemoveReal);
        nbOfRemovedPawns += toRemoveReal.size();
        // create action list. After the last action, it is next player's turn.
        ActionList actions = new ActionList(true);
        // create the move action, without animation => the pawn will be put at the center of dest cell
        GameAction move = new MoveAction(model, pawn, "BRBboard", rowDest, colDest);
        actions.addSingleAction(move);
        return actions;
    }
}
