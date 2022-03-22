package pt.ulisboa.tecnico.master_process.interactions;

import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;

import java.util.ArrayList;
import java.util.Arrays;

public class UltrasonicDistanceInteraction extends Interaction{

    public UltrasonicDistanceInteraction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        super(pi, interactionName, interactionSolution, phase);
    }

    @Override
    public void generateClues() {
        this.generatedClues = new ArrayList<>(Arrays.asList(this.interactionSolution.split(" ")));
    }
}
