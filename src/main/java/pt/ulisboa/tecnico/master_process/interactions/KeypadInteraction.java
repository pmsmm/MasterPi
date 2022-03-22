package pt.ulisboa.tecnico.master_process.interactions;

import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;

import java.util.ArrayList;

public class KeypadInteraction extends Interaction{

    public KeypadInteraction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        super(pi, interactionName, interactionSolution, phase);
        this.interactionType = InteractionType.SECONDARY;
    }

    @Override
    public void generateClues() {
        this.generatedClues = new ArrayList<>();
        for (int i = 0; i < this.interactionSolution.length(); i++) {
            this.generatedClues.add(String.valueOf(this.interactionSolution.charAt(i)));
        }
    }
}
