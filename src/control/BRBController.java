package control;

import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.GridElement;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.action.MoveAction;
import boardifier.view.View;
import model.BRBBoard;
import model.BRBStageModel;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static boardifier.view.ConsoleColor.*;

public class BRBController extends Controller {

    BufferedReader in;
    boolean firstPlayer;

    public static boolean drawRequested;
    public static int countDraw;

    public static int typeAI;

    ArrayList<String> storedData = new ArrayList<>(10);
    ArrayList<Character> storedDataColor = new ArrayList<>(10);

    //public static HashMap<String, Data> dataMapPawns = new HashMap<>();
    public static HashMap<String, Data> dataMapRed = new HashMap<>();
    public static HashMap<String, Data> dataMapBlack = new HashMap<>();

    public BRBController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    public BRBController(Model model, View view, String fileName) {
        super(model, view);
        firstPlayer = true;
        try {
            in = new BufferedReader(new FileReader(fileName));
        } catch (IOException e) {
            System.out.println("Cannot open file " + fileName);
            in = new BufferedReader(new InputStreamReader(System.in));
        }
    }

    /**
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        update();
        while (!model.isEndStage()) {
            nextPlayer();
            update();
            // get the color of the current player
            int hasPlayedPlayer = model.getIdPlayer();
            // if hasPlayedPlayer == 0, the current player is 'R', else it is 'B'
            char pastPlayer = (hasPlayedPlayer == 0) ? 'R' : 'B';
            storeData(pastPlayer);
        }
        // get the color of the winner
        int winner = model.getIdPlayer();
        // if winner == 0, the winner is 'R', else it is 'B'
        char winnerColor = (winner == 0) ? 'R' : 'B';
        takeData(winnerColor);
        // Print the number of wins for each player
        int[] score = BRBStageModel.getScore();
        // TODO REMOVE LATER
        System.setOut(System.out);
        System.setErr(System.out);

        System.out.println("========================================");
        System.out.println("Score : "+ BLACK_BOLD+"BLACK " + score[0] + BLUE+ " - "+ RED_BOLD +"RED " + score[1] + BLACK);
        System.out.println("========================================");
        firstPlayer= true;
        System.out.println("Score : BLACK " + score[0] + " - RED " + score[1]);
        System.out.println("NB of RDM moves : " + BRBDecider.nbRDM);
        System.out.println("NB of Smart moves : " + BRBDecider.nbSmart);
        System.out.println("NB of pawns eaten : " + BRBDecider.nbOfRemovedPawns);
        stopStage();
        endGame();
        // save the data into the files
        //savingFiles("dataRed.bin", dataMapRed);
        //savingFiles("dataBlack.bin", dataMapBlack);
    }

    /**
     *
     * Store the data of the current stage
     *
     * @param curPlayer the current player ('R' ou 'B')
     */
    public void storeData(char curPlayer) {
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        String result = board.toString();
        storedData.add(result);
        storedDataColor.add(curPlayer);
    }

    public void takeData(char winner) {
        int i = 0;
        // Pour chaque set de données récupéré par storeData
        for (String stateString : storedData) {
            switch (storedDataColor.get(i)) {
                case 'R':
                    if (dataMapRed.containsKey(stateString)) {
                        Data data = dataMapRed.get(stateString);
                        int WCountB = data.getWCountB();
                        int WCountR = data.getWCountR();
                        if (winner == 'R') {
                            data.setWCountR(WCountR + 1);
                            data.setWCountB(WCountB);
                        } else { // winner is equal to B
                            data.setWCountR(WCountR);
                            data.setWCountB(WCountB + 1);
                        }
                        dataMapRed.put(stateString, data);
                    } else { // first time situation
                        if (winner == 'R') { // red win count to 1
                            dataMapRed.put(stateString, new Data<>(1, 0));
                        } else { // blue win count to 1
                            dataMapRed.put(stateString, new Data<>(0, 1));
                        }
                    }
                    break;
                case 'B':
                    if (dataMapBlack.containsKey(stateString)) {
                        Data data = dataMapBlack.get(stateString);
                        int WCountB = data.getWCountB();
                        int WCountR = data.getWCountR();
                        if (winner == 'R') {
                            data.setWCountR(WCountR + 1);
                            data.setWCountB(WCountB);
                        } else { // winner is equal to B
                            data.setWCountR(WCountR);
                            data.setWCountB(WCountB + 1);
                        }
                        dataMapBlack.put(stateString, data);
                    } else { // first time situation
                        if (winner == 'R') { // red win count to 1
                            dataMapBlack.put(stateString, new Data<>(1, 0));
                        } else { // blue win count to 1
                            dataMapBlack.put(stateString, new Data<>(0, 1));
                        }
                    }
                    break;
                default:
                    // Throw error for now
                    System.out.println("Error in takeData");
                    break;
            }
            i++;
        }
        storedData.clear();
        storedDataColor.clear();
    }

    public void saveAllFiles() {
        // remove the files if they exist first
        /*
        File file = new File("dataMapPawns.bin");
        file.delete();
        savingFiles("dataMapPawns.bin", dataMapPawns);
         */
        File file = new File("dataMapRed.bin");
        File file2 = new File("dataMapBlack.bin");
        file.delete();
        file2.delete();
        savingFiles("dataMapRed.bin", dataMapRed, 'R');
        savingFiles("dataMapBlack.bin", dataMapBlack, 'B');
    }

    /**
     * Permet de sauvegarder les données des parties dans un fichier
     *
     * @param namefile nom du fichier dans lequel on souhaite chercher une valeur
     * @param hashMap valeurs que l'on souhaite ajouter au fichier
     */
    public void savingFiles(String namefile, HashMap<String, Data> hashMap, char color){
        long numberOfKeys = hashMap.size();
        int bufferSize = (int) Math.min(500 * numberOfKeys + 1024, Integer.MAX_VALUE*0.75);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(namefile, true);
            FileChannel fc = fos.getChannel();

            // Create a byte buffer and write the map to it
            ByteBuffer bb = ByteBuffer.allocate(bufferSize);
            for (Map.Entry<String, Data> entry : hashMap.entrySet()) {
                // Compress the key
                String compressedKeyStr = compressData(entry.getKey(), color);

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);
                oos.writeObject(entry.getValue());
                oos.close();
                byte[] dataBytes = baos.toByteArray();
                bb.putInt(compressedKeyStr.length());
                bb.put(compressedKeyStr.getBytes());
                bb.putInt(dataBytes.length);
                bb.put(dataBytes);
            }
            bb.flip();
            fc.write(bb);

            // Close the file
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String compressData(String someData, char color) {
        StringBuilder compressed = new StringBuilder();
        int count = 1;
        char last = someData.charAt(0);
        // if color = 'R' then we replace all the 'B' by '_'
        if (color == 'R') {
            someData = someData.replace('B', '_');
        } else if (color == 'B'){
            someData = someData.replace('R', '_');
        }

        for (int i = 1; i < someData.length(); i++) {
            if (someData.charAt(i) == last) {
                count++;
            } else {
                if (count > 1) {
                    compressed.append(count);
                }
                compressed.append(last);
                last = someData.charAt(i);
                count = 1;
            }
        }
        if (count > 1) {
            compressed.append(count);
        }
        compressed.append(last);

        return compressed.toString();
    }

    /**
     * Defines what to do when the stage is over
     */
    public void nextPlayer() {
        // for the first player, the id of the player is already set, so do not compute it
        if (!firstPlayer) {
            model.setNextPlayer();
        } else {
            firstPlayer = false;
        }
        // get the new player
        Player p = model.getCurrentPlayer();
        // System.out.println("Player " + p.getName() + " plays");
        if (p.getType() == Player.COMPUTER) {
            System.out.println("GOD [COMPUTER] PLAYS");
            BRBDecider decider = new BRBDecider(model, this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            play.start(typeAI);
        } else {
            boolean ok = false;
            while (!ok) {
                if (model.getIdPlayer()==1) {
                    System.out.print(RED_BOLD + p.getName() + BLACK + " > ");
                } else {
                    System.out.print(BLACK_BOLD + p.getName() + BLACK + " > ");
                }
                try {
                    String line = in.readLine();
                    if (line.length() == 3) {
                        ok = analyseAndPlay(line);
                    } else if(line.toLowerCase().contains("stop")
                    || (line.toLowerCase().contains("exit"))) {
                        stopStage();
                        endGame();
                        break;
                    } else if (line.toLowerCase().contains("draw")) {
                        System.out.println(p.getName() + ", this coward, offers a draw, do you accept ?");
                        // store players
                        List<Player> players = model.getPlayers();
                        if (players.get(0).getType() == Player.COMPUTER || players.get(1).getType() == Player.COMPUTER) {
                            System.out.print("God [Computer] > Beep boop (he said no)");
                            line = "L(° O °L)";
                        } else if (players.get(0) == p) {
                            System.out.print(players.get(1).getName()+ " > ");
                            line = in.readLine();
                        } else {
                            System.out.print(players.get(0).getName()+ " > ");
                            line = in.readLine();
                        }
                        if (line.toLowerCase().contains("yes")
                                || line.toLowerCase().contains("ok")
                                || line.toLowerCase().contains("accept")
                                || line.toLowerCase().contains("oui")
                                || line.toLowerCase().contains("draw")
                                || line.toLowerCase().contains("y")) {
                            System.out.println("Haizz, the draw was accepted.");
                            drawRequested=true;
                            countDraw++;
                            firstPlayer= true;
                            stopStage();
                            endGame();
                            return;
                        } else {
                            System.out.println("The draw is refused ! Now is not the time to yield, son !");
                        }
                    }
                    if (!ok) {
                        System.out.println("Incorrect instruction. Retry, son !");
                    }
                } catch (IOException e) {
                    firstPlayer= true;
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param line the line read from the console
     * @return true if the line is correct and the action has been played
     */
    private boolean analyseAndPlay(String line) {
        System.out.println("The army verifies if " + line + " is a correct order.");
        BRBStageModel gameStage = (BRBStageModel) model.getGameStage();
        // get the pawn value from the first char
        int pawnIndex = 1;
        char firstChar = line.charAt(0);
        if (firstChar == 'K' || firstChar == 'k') {
            if (model.getIdPlayer() != 0) return false;
        } else {
            pawnIndex = (int) (line.charAt(0) - '1');
            // Check pawn color (red or black)
            if (model.getIdPlayer() == 0) {
                if ((pawnIndex < 0) || (pawnIndex > 3)) return false;
                // get the cords in the board
            } else {
                if ((pawnIndex < 0) || (pawnIndex > 7)) return false;
                // get the cords in the board
            }
        }

        int col = (int) (line.charAt(1) - 'A');
        int row = (int) (line.charAt(2) - '1');
        // check coords validity
        if ((row < 0) || (row > 6)) return false;
        if ((col < 0) || (col > 6)) return false;
        // check if the pawn is still in its pot

        int[] coords = gameStage.getBoard().getCoords(pawnIndex + 1, model.getIdPlayer(), firstChar);
        if (coords == null) return false;
        GameElement pawn = gameStage.getBoard().getElement(coords[0], coords[1]);
        // compute valid cells for the chosen pawn

        if (firstChar == 'K' || firstChar == 'k') {
            gameStage.getBoard().setValidCells(coords[0], coords[1], true);
        } else {
            gameStage.getBoard().setValidCells(coords[0], coords[1], false);
        }
        // check if the cell is valid
        if (!gameStage.getBoard().canReachCell(row, col)) return false;
        ActionList actions = new ActionList(true);
        GameAction move = new MoveAction(model, pawn, "BRBboard", row, col);
        // add the action to the action list.
        actions.addSingleAction(move);

        System.out.println("Player " + model.getCurrentPlayer().getName() + " plays " + line);

        ActionPlayer play = new ActionPlayer(model, this, actions);
        play.start();
        // get the pawns to remove
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        List<GameElement> pawnsToRemove = board.getPawnsToRemove(row, col, model.getIdPlayer());
        // remove the pawns
        board.removePawns(pawnsToRemove);
        return true;
    }
}
