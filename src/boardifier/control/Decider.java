package boardifier.control;

import boardifier.model.action.ActionList;
import boardifier.model.Model;
import org.encog.neural.networks.BasicNetwork;

public abstract class Decider {
    protected Model model;
    protected Controller control;

    public Decider(Model model, Controller control) {
        this.model = model;
        this.control = control;
    }

    public abstract ActionList decide(BasicNetwork neuralNetwork);

    public abstract BasicNetwork createNeuralNetwork();
}
