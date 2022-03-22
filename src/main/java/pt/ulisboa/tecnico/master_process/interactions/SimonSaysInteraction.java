package pt.ulisboa.tecnico.master_process.interactions;

import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;

public class SimonSaysInteraction extends Interaction{

    public SimonSaysInteraction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        super(pi, interactionName, interactionSolution, phase);
        this.interactionType = InteractionType.PRIMARY;
    }
}
