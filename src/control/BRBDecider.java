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
import model.Pawn;
import control.BRBController;

import java.awt.*;
import java.io.*;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.MLRegression;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.ml.data.specific.BiPolarNeuralData;
import org.encog.ml.train.strategy.ResetStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.Train;
import org.encog.neural.networks.training.anneal.NeuralSimulatedAnnealing;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.ml.data.MLData;


public class BRBDecider extends Decider {

    private static final Random loto = new Random(Calendar.getInstance().getTimeInMillis());

    public BRBDecider(Model model, Controller control) {
        super(model, control);
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
    public ActionList decide(BasicNetwork neuralNetwork) {
        // Create an environment object to represent the current game state
        BRBStageModel stage = (BRBStageModel)model.getGameStage();
        BRBBoard board = stage.getBoard();
        GameElement pawn = null; // the pawn that is moved
        int rowDest = 0; // the dest. row in board
        int colDest = 0; // the dest. col in board

        // TODO: Define inputs for the neural network based on the game state
        double[] inputs;
        if (model.getIdPlayer() == Pawn.PAWN_BLACK) {
            inputs = stage.getInputs(1);
        }
        else {
            inputs = stage.getInputs(-1);
        }

        // TODO: Call the neural network to get the decision
        MLData inputData = new BasicMLData(inputs);
        MLData outputData = neuralNetwork.compute(inputData);
        double[] output = outputData.getData();
        System.out.println("output: " + output[0] + " " + output[1] + " " + output[2] + " " + output[3] + " " + output[4] + " " + output[5] + " " + output[6] + " " + output[7]);
        Pawn p = (Pawn) board.getElement(8, Pawn.PAWN_RED, true);
        System.out.println("pawn: " + p);
        // TODO: Translate the output of the neural network into a game action
        ActionList actions = new ActionList(true);
        //GameAction move = ... // Translate output to game action
        //actions.addSingleAction(move);
        return actions;
    }

    public BasicNetwork createNeuralNetwork() {
        // créer un réseau de neurones avec 50 entrées, une couche cachée de 20 neurones, et une couche de sortie de 8 neurones
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, 50));
        network.addLayer(new BasicLayer(new ActivationReLU(), true, 20));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, 8));
        network.getStructure().finalizeStructure();
        network.reset();

        // créer un ensemble de données d'apprentissage
        BasicMLDataSet trainingSet = new BasicMLDataSet();
        return network;
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
        int id = 0;

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
                if (model.getIdPlayer() == 0) {
                    data = searchValue("dataBlack.bin", combinationToSearchCompressed);
                } else {
                    data = searchValue("dataRed.bin", combinationToSearchCompressed);
                }
                // print data info
                System.out.println(data);
            }
        }
        return null;
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
}
