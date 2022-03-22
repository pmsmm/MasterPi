package pt.ulisboa.tecnico.master_process.interactions;

import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;

import java.util.ArrayList;
import java.util.Arrays;

public class RGBLedInteraction extends Interaction{

    public RGBLedInteraction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        super(pi, interactionName, interactionSolution, phase);
        this.interactionType = InteractionType.SECONDARY;
    }

    @Override
    public void generateClues() {
        this.generatedClues = new ArrayList<>(Arrays.asList(this.interactionSolution.split(" ")));
    }
}
