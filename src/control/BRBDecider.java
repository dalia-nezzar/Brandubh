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
import java.util.*;
import java.util.List;

public class BRBDecider extends Decider {

    private static final Random loto = new Random(Calendar.getInstance().getTimeInMillis());

    public BRBDecider(Model model, Controller control) {
        super(model, control);
    }
    public static int nbRDM = 0;
    public static int nbSmart = 0;
    public static int nbOfRemovedPawns = 0;

    /**
     * Select the AI we want to play against
     * @param selected
     * @return the move given by the chosen AI
     */
    @Override
    public ActionList decider(int selected) {
        switch (selected) {
            case 1:
                return decideAleatoire();
            case 2:
                return decideSmart();
            case 3:
                return decideEAT();
            default:
                return null;
        }
    }

    /**
     * Play a move at random
     * @return the chosen move
     */
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
                stage.giveUp(model.getIdPlayer());
                return null;
                //throw new RuntimeException("Error: no valid cells");
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

    /**
     * Decide the next move for the current player (Attackers or Defenders) by searching for
     * datas in the files
     *
     * If you can eat a pawn, do it
     * Else, move a pawn
     * If it doesn't know what to move, move at random
     *
     * @return the move to do
     */
    public ActionList decideSmart() {
        BRBStageModel stage = (BRBStageModel) model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        GameElement pawn = null; // the pawn that is moved
        GameElement selectedPawn = null;
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        // get list of the pawns of the current player
        List<GameElement> pawns = board.getPawns(model.getIdPlayer());
        List<Point> valid = null;
        // array list of possibles moves
        ArrayList<Point> moves = new ArrayList<>();
        // array list of possibles pawns
        ArrayList<GameElement> pawnsToMove = new ArrayList<>();
        // toremove
        List<GameElement> toRemove = new ArrayList<>();
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
                // Can he eat something?
                toRemove.clear();
                if (!pawn.isKing()) toRemove = board.getPawnsToRemove(valid.get(j).y, valid.get(j).x, pawn.getColor());
                else toRemove = board.getPawnsToRemove(valid.get(j).y, valid.get(j).x, 2);
                if (toRemove.size() > 0) {
                    // ignore the rest, return now
                    rowDest = valid.get(j).y;
                    colDest = valid.get(j).x;
                    selectedPawn = pawn;
                    // remove them
                    board.removePawns(toRemove);
                    // create action list. After the last action, it is next player's turn.
                    ActionList actions = new ActionList(true);
                    // create the move action, without animation => the pawn will be put at the center of dest cell
                    GameAction move = new MoveAction(model, selectedPawn, "BRBboard", rowDest, colDest);
                    actions.addSingleAction(move);
                    return actions;
                }

                System.out.println("valid: " + valid.get(j).y + " " + valid.get(j).x);
                // create a representation of the move in String format
                String combinationToSearch = translator(coords[1], coords[0], valid.get(j).x, valid.get(j).y);
                char color;
                if (model.getIdPlayer() == 0) color = 'B';
                else color = 'R';
                String combinationToSearchCompressed = BRBController.compressData(combinationToSearch, color);
                System.out.println(combinationToSearchCompressed);
                // if player is red then search in dataRed.bin, else in dataBlack.bin
                Data data = null;
                int score = 0;
                if (model.getIdPlayer() == 0) {
                    data = searchValue("dataMapBlack.bin", combinationToSearchCompressed);
                    if (data != null) score = score(data.getWCountB(), data.getWCountR());
                } else {
                    data = searchValue("dataMapRed.bin", combinationToSearchCompressed);
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
                    pawnsToMove.add(pawn);
                    selectedPawn = pawn;
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
            selectedPawn = pawnsToMove.get(id);
            if (highestScore == 0) {
                nbRDM++;
            } else {
                nbSmart++;
            }
        } else {
            System.out.println("---------------CHOOSING THE BEST MOVE----------------");
            nbSmart++;
        }
        List<GameElement> toRemoveReal = board.getPawnsToRemove(rowDest, colDest, selectedPawn.getColor());
        // remove them
        board.removePawns(toRemoveReal);
        // create action list. After the last action, it is next player's turn.
        ActionList actions = new ActionList(true);
        // create the move action, without animation => the pawn will be put at the center of dest cell
        GameAction move = new MoveAction(model, selectedPawn, "BRBboard", rowDest, colDest);
        actions.addSingleAction(move);
        return actions;
    }

    /**
     * Translate the board in a String format
     * It also swaps the start and dest pawns
     *
     * @param startX
     * @param startY
     * @param destX
     * @param destY
     * @return the translated board
     */
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

    /**
     * Calculate the score of a board, this can be modified to ajust the AI
     *
     * @param currentPlayerWins
     * @param enemyWins
     * @return the score
     */
    public int score(int currentPlayerWins, int enemyWins) {
        int score = 0;
        // Give a higher score for a larger lead
        score += (currentPlayerWins - enemyWins) * 100;
        // Give a higher score for a larger number of total wins
        score += (currentPlayerWins + enemyWins) * 1.5;
        return score;
    }

    /**
     * Search a value in a file (value usually given by translator)
     *
     * @param namefile
     * @param key
     * @return the data found, or null
     */
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

    /**
     * AI : decide the next action to do
     * This AI works for both Attackers and Defenders
     * Objective 1 : Capture the king or go to the corner (Win)
     * Objective 2 : If the king can go the edges, go there (SUBJECT TO CHANGE)
     * Objective 3 : If you can eat a pawn, do it
     *
     * @return the move action to do
     */
    public ActionList decideEAT() {
        BRBStageModel stage = (BRBStageModel) model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        GameElement pawn = null; // the pawn that is moved
        GameElement selectedPawn = null; // the pawn pawn to move for now
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
        // get list of the possibles edges moves the king can do
        List<Point> kingLimitsMoves = getKingLimitsMoves();
        // create the list of corners in one line
        List<Point> corners = Arrays.asList(
                new Point(0, 0),
                new Point(0, 6),
                new Point(6, 0),
                new Point(6, 6)
        );

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
                System.out.println("pawn " + pawn.getNumber() + " of color " + pawn.getColor() + " can move to row: " + valid.get(j).y + " col: " + valid.get(j).x);
                //System.out.println("valid: " + valid.get(j).y + " " + valid.get(j).x);
                toRemove.clear();
                if (!pawn.isKing()) toRemove = board.getPawnsToRemove(valid.get(j).y, valid.get(j).x, pawn.getColor());
                else toRemove = board.getPawnsToRemove(valid.get(j).y, valid.get(j).x, 2);
                int score = toRemove.size();
                // get the edges where the king can move
                // for black that mean it has a chance to win
                // for red it means it is a spot to defend
                if (kingLimitsMoves.contains(valid.get(j))) score = 999; // if you can go to the edge, go to the edge
                if (corners.contains(valid.get(j))) score = 9999; // MmmMMMMm ~even better~ (if corner available go to corner)
                for (int k = 0; k < toRemove.size(); k++) {
                    if (toRemove.get(k).isKing()) score = 9999; // King is in the remove list, capture it now
                }
                if (score >= highestScore) {
                    toRemoveReal.clear();
                    //Make a copy of toRemove into toRemoveReal
                    toRemoveReal.addAll(toRemove);
                    highestScore = score;
                    selectedPawn = pawn;
                    rowDest = valid.get(j).y;
                    colDest = valid.get(j).x;
                } else {
                    //System.out.println("score: " + score);
                }
                //System.out.println("highestscore: " + highestScore);
                //print toRemoveReal
                for (int k = 0; k < toRemoveReal.size(); k++) {
                    System.out.println("toRemoveReal: " + toRemoveReal.get(k).getNumber());
                }
                //print toRemoveReal.size
                //System.out.println("toRemoveReal.size: " + toRemoveReal.size());
            }
        }
        // if toRemove.size == 0, then no pawn can be eaten
        // therefore, return execute decideAleatoire()
        if (toRemoveReal.size() == 0 && highestScore < 100) { // Dirty way of saying that don't know what to do
            nbRDM++;
            System.out.println("Random Move");
            return decideAleatoire();
        }
        System.out.println("Smart Move");
        nbSmart++;
        board.removePawns(toRemoveReal);
        nbOfRemovedPawns += toRemoveReal.size();
        // create action list. After the last action, it is next player's turn.
        ActionList actions = new ActionList(true);
        // create the move action, without animation => the pawn will be put at the center of dest cell
        GameAction move = new MoveAction(model, selectedPawn, "BRBboard", rowDest, colDest);
        actions.addSingleAction(move);
        return actions;
    }

    /**
     * Get all the places the king can move to
     * Why ? Because then the attacker can be in danger
     *
     * @return a list of the places (in edges) where the king can move to
     */
    public List<Point> getKingLimitsMoves() {
        List<Point> moves = new ArrayList<>();
        BRBStageModel stage = (BRBStageModel) model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        List<GameElement> redPawns = board.getPawns(1);

        int[] coords = board.getCoords(9, 2, 'K'); // the 2 First parameters don't matter here
        // if the king is already on the edge, this function is useless, so return null
        if (coords[0] == 0 || coords[0] == 7 || coords[1] == 0 || coords[1] == 7) return moves;
        // get all the places the king can move to
        moves = board.computeValidCells(coords[0], coords[1], true);
        // keep only the edges
        for (int i = 0; i < moves.size(); i++) {
            if (moves.get(i).x == 0 || moves.get(i).x == 7 || moves.get(i).y == 0 || moves.get(i).y == 7) {
                //System.out.println("King can move to: " + moves.get(i).y + " " + moves.get(i).x);
            } else {
                moves.remove(i);
                i--;
            }
        }

        // NORTH
        // if there is a red pawn at (0,3), (0,4) or (0,5), then remove (0,1) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[0] == 0 && (coords[1] == 3 || coords[1] == 4 || coords[1] == 5)) {
                moves.remove(new Point(0, 1));
                System.out.println("removed 0,1");
            }
        }
        // if there is a red pawn at (0,1), (0,2) or (0,3), then remove (0,5) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[0] == 0 && (coords[1] == 1 || coords[1] == 2 || coords[1] == 3)) {
                moves.remove(new Point(0, 5));
                System.out.println("removed 0,5");
            }
        }
        // WEST
        // if there is a red pawn at (3,0), (4,0) or (5,0), then remove (1,0) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[1] == 0 && (coords[0] == 3 || coords[0] == 4 || coords[0] == 5)) {
                moves.remove(new Point(1, 0));
                System.out.println("removed 1,0");
            }
        }
        // if there is a red pawn at (1,0), (2,0) or (3,0), then remove (5,0) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[1] == 0 && (coords[0] == 1 || coords[0] == 2 || coords[0] == 3)) {
                moves.remove(new Point(5, 0));
                System.out.println("removed 5,0");
            }
        }

        // SOUTH
        // if there is a red pawn at (6,1), (6,2) or (6,3), then remove (6,5) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[0] == 6 && (coords[1] == 1 || coords[1] == 2 || coords[1] == 3)) {
                moves.remove(new Point(6, 5));
                System.out.println("removed 6,5");
            }
        }
        // if there is a red pawn at (6,3), (6,4) or (6,5), then remove (6,1) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[0] == 6 && (coords[1] == 3 || coords[1] == 4 || coords[1] == 5)) {
                moves.remove(new Point(6, 1));
                System.out.println("removed 6,1");
            }
        }

        // EAST
        // if there is a red pawn at (1,6), (2,6) or (3,6), then remove (5,6) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[1] == 6 && (coords[0] == 1 || coords[0] == 2 || coords[0] == 3)) {
                moves.remove(new Point(5, 6));
                System.out.println("removed 5,6");
            }
        }
        // if there is a red pawn at (3,6), (4,6) or (5,6), then remove (1,6) from the list
        for (int i = 0; i < redPawns.size(); i++) {
            coords = board.getCoords(redPawns.get(i).getNumber(), redPawns.get(i).getColor(), ' ');
            if (coords[1] == 6 && (coords[0] == 3 || coords[0] == 4 || coords[0] == 5)) {
                moves.remove(new Point(1, 6));
                System.out.println("removed 1,6");
            }
        }
        return moves;
    }
}
