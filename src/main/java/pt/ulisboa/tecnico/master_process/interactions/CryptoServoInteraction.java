package pt.ulisboa.tecnico.master_process.interactions;

import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;

import java.util.ArrayList;
import java.util.Arrays;

public class CryptoServoInteraction extends Interaction{

    public CryptoServoInteraction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        super(pi, interactionName, interactionSolution, phase);
        this.interactionType = InteractionType.SECONDARY;
    }

    @Override
    public void generateClues() {
        //Position 1 - Clear Text || Position 2 - Shift Factor || Position 3 - Encrypted Text
        //Index 0: CT-XYZ           || Index 1: SF-##              || Index 2: ET-XYZ
        ArrayList<String> solution = new ArrayList<>(Arrays.asList(this.interactionSolution.split(" ")));
        this.generatedClues = new ArrayList<>();
        this.generatedClues.add(solution.get(1));
        this.generatedClues.add(solution.get(2));
    }
}
