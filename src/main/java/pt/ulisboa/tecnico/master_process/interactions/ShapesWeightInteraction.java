package pt.ulisboa.tecnico.master_process.interactions;

import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;

import java.util.ArrayList;

public class ShapesWeightInteraction extends Interaction{

    public ShapesWeightInteraction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        super(pi, interactionName, interactionSolution, phase);
        this.interactionType = InteractionType.SECONDARY;
    }

    @Override
    public void generateClues() {
        //TODO: After fully implementing Interaction
        this.generatedClues = new ArrayList<>();
        this.generatedClues.add("Give Shape 1");
        this.generatedClues.add("Give Shape 2");
        this.generatedClues.add("Give Shape 3");
    }
}
