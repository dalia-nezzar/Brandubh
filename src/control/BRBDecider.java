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

    @Override
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

        // For EVERY pawn, check EVERY possible move and put them into an arrayList

        return null;
    }
}
