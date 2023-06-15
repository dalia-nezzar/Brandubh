package control;

import boardifier.control.ActionPlayer;
import boardifier.control.Controller;
import boardifier.model.GameElement;
import boardifier.model.Model;
import boardifier.model.Player;
import boardifier.model.action.ActionList;
import boardifier.model.action.GameAction;
import boardifier.model.action.MoveAction;
import boardifier.view.*;
import model.BRBBoard;
import model.BRBStageModel;

import javax.naming.ldap.Control;
import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.*;

import static boardifier.view.ConsoleColor.*;

public class BRBController extends Controller {

    boolean firstPlayer;

    public static boolean drawRequested;
    public static boolean stopBool=false;
    public static int countDraw;
    public static int typeAI1;
    public static int typeAI2 = -1;
    public static int nbParties;
    public static Scanner input = new Scanner(System.in);
    ArrayList<String> storedData = new ArrayList<>(10);
    ArrayList<Character> storedDataColor = new ArrayList<>(10);

    public static HashMap<String, Data> dataMap = new HashMap<>();
    //public static HashMap<String, Data> dataMapRed = new HashMap<>();
    //public static HashMap<String, Data> dataMapBlack = new HashMap<>();

    public BRBController(Model model, View view) {
        super(model, view);
        firstPlayer = true;
        if (Controller.gVersion) {
            setControlKey(new BRBControllerKey(model, view, this));
            setControlMouse(new BRBControllerMouse(model, view, this));
            setControlAction(new BRBControllerAction(model, view, this));
        }
    }

    /**
     * Defines what to do within the single stage of the single party
     * It is pretty straight forward to write :
     */
    public void stageLoop() {
        System.out.println("StageLoop");
        update();
        while (!model.isEndStage()) {
            nextPlayer();
            if (!Controller.gVersion) update();
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
            //System.out.println("stateString : " + stateString);
            //System.out.println("RotatedString : " + rotateArray(stateString));
            String rotatedString90 = rotateArray(stateString);
            String rotatedString180 = rotateArray(rotatedString90);
            String rotatedString270 = rotateArray(rotatedString180);
            Data data0 = dataMap.get(stateString);
            Data data90 = dataMap.get(rotatedString90);
            Data data180 = dataMap.get(rotatedString180);
            Data data270 = dataMap.get(rotatedString270);

            //System.out.println("stateString: "+ stateString + " mirroredString: " + mirrorArray(stateString));
            String mirroredString = mirrorArray(stateString);
            String mirroredString90 = rotateArray(mirroredString);
            String mirroredString180 = rotateArray(mirroredString90);
            String mirroredString270 = rotateArray(mirroredString180);
            Data mirroredData0 = dataMap.get(mirroredString);
            Data mirroredData90 = dataMap.get(mirroredString90);
            Data mirroredData180 = dataMap.get(mirroredString180);
            Data mirroredData270 = dataMap.get(mirroredString270);
            Data selectedData = null;
            if (data0   != null) selectedData = data0;
            else if (data90  != null) selectedData = data90;
            else if (data180 != null) selectedData = data180;
            else if (data270 != null) selectedData = data270;
            else if (mirroredData0   != null) selectedData = mirroredData0;
            else if (mirroredData90  != null) selectedData = mirroredData90;
            else if (mirroredData180 != null) selectedData = mirroredData180;
            else if (mirroredData270 != null) selectedData = mirroredData270;

            Data dataList[] = {data0, data90, data180, data270, mirroredData0, mirroredData90, mirroredData180, mirroredData270};
            String stateList[] = {stateString, rotatedString90, rotatedString180, rotatedString270, mirroredString, mirroredString90, mirroredString180, mirroredString270};
            //selectedData = data0; // TODO remove
            switch (storedDataColor.get(i)) {
                case 'R':
                    putOrUpdate(winner, dataList, stateList, selectedData, dataMap);
                    break;
                case 'B':
                    putOrUpdate(winner, dataList, stateList, selectedData, dataMap);
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

    private void putOrUpdate(char winner, Data dataList[], String stateList[], Data selectedData, HashMap<String, Data> dataMap) {
        if (selectedData != null) {
            int WCountB = selectedData.getWCountB();
            int WCountR = selectedData.getWCountR();
            if (winner == 'R') {
                selectedData.setWCountR(WCountR + 1);
                selectedData.setWCountB(WCountB);
            } else { // winner is equal to B
                selectedData.setWCountR(WCountR);
                selectedData.setWCountB(WCountB + 1);
            }
            // for i in range len of list
            for (int i = 0; i < dataList.length; i++) {
                if (dataList[i] != null) {
                    dataMap.put(stateList[i], selectedData);
                }
            }
            //dataMap.put(stateList[0], selectedData); // TODO remove and uncomment the for loop
        } else { // first time situation
            if (winner == 'R') dataMap.put(stateList[0], new Data<>(1, 0)); // Red win count to 1
            else               dataMap.put(stateList[0], new Data<>(0, 1)); // Blue win count to 1
        }
    }

    public void saveAllFiles() {
        // remove the files if they exist first
        File file = new File("dataMap.bin");
        file.delete();
        savingFiles("dataMap.bin", dataMap, 'X');

        /*
        File file = new File("dataMapRed.bin");
        File file2 = new File("dataMapBlack.bin");
        file.delete();
        file2.delete();
        savingFiles("dataMapRed.bin", dataMapRed, 'R');
        savingFiles("dataMapBlack.bin", dataMapBlack, 'B');
         */
    }

    public static String rotateArray(String inputArray) {
        int n = 7; // 7x7 board
        char[] rotatedArray = new char[inputArray.length()];

        for (int i = 0; i < n; i++) {
            for (int j = n - 1; j >= 0; j--) {
                rotatedArray[i * n + (n - j - 1)] = inputArray.charAt(j * n + i);
            }
        }
        return new String(rotatedArray);
    }

    public static String mirrorArray(String inputArray) {
        int n = 7; // 7x7 board
        char[] mirroredArray = new char[inputArray.length()];

        for (int i = 0; i < n; i++) {
            for (int j = n - 1; j >= 0; j--) {
                mirroredArray[i * n + (n - j - 1)] = inputArray.charAt(i * n + j);
            }
        }
        return new String(mirroredArray);
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
        } else {
            // do nothing;
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
        if (Controller.gVersion) {
            // use the default method to compute next player
            model.setNextPlayer();
            // get the new player
            Player p = model.getCurrentPlayer();
            // change the text of the TextElement
            BRBStageModel stageModel = (BRBStageModel) model.getGameStage();
            stageModel.getPlayerName().setText(p.getName());
            if (p.getType() == Player.COMPUTER) {
                System.out.println("COMPUTER PLAYS");
                BRBDecider decider = new BRBDecider(model,this);
                ActionPlayer play = new ActionPlayer(model, this, decider, null);
                play.start();
            }
        } else {
            nextPlayerConsole();
        }
    }

    public void nextPlayerConsole() {
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
            if (nbParties < 1000) System.out.println("GOD [COMPUTER] PLAYS");
            BRBDecider decider = new BRBDecider(model, this);
            ActionPlayer play = new ActionPlayer(model, this, decider, null);
            //System.out.println("typeAI1 = " + typeAI1 + " typeAI2 = " + typeAI2 + " idPlayer = " + model.getIdPlayer());
            if (typeAI2 == -1) {
                play.play(typeAI1);
            } else if (model.getIdPlayer() == 1) {
                play.play(typeAI1);
            } else {
                play.play(typeAI2);
            }
        } else {
            boolean ok = false;
            while (!ok) {
                if (model.getIdPlayer()==1) {
                    System.out.print(RED_BOLD + p.getName() + BLACK + " > ");
                } else {
                    System.out.print(BLACK_BOLD + p.getName() + BLACK + " > ");
                }
                try {
                    String line = input.nextLine();
                    if (line.length() == 3 && (!line.equals(null))) {
                        ok = analyseAndPlay(line);
                    } else if(line.toLowerCase().contains("stop")
                            || (line.toLowerCase().contains("exit"))) {
                        stopBool=true;
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
                            line = input.nextLine();
                        } else {
                            System.out.print(players.get(0).getName()+ " > ");
                            line = input.nextLine();
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
                } catch (Exception e) {
                    firstPlayer= true;
                    e.printStackTrace();
                    input.nextLine();
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
        play.play(-1);// Number here doesn't matter since this function is only used by the player
        // get the pawns to remove
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard(); // get the board
        List<GameElement> pawnsToRemove = board.getPawnsToRemove(row, col, model.getIdPlayer());
        // remove the pawns
        board.removePawns(pawnsToRemove);
        return true;
    }

    public boolean startanalyseAndPlay (String line){
        return analyseAndPlay(line);
    }

}
