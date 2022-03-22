package pt.ulisboa.tecnico.master_process.interactionpies.service;

import pt.ulisboa.tecnico.master_process.interactions.InteractionInterface;
import pt.ulisboa.tecnico.master_process.interactions.InteractionType;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Representation of an Interaction
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class Interaction implements InteractionInterface {

    protected static Logger LOGGER = Logger.getLogger(Interaction.class.getName());

    protected final InteractionPi interactionPi;
    protected final String interactionName;
    protected final String interactionSolution;
    protected InteractionStatus status;
    protected int acquiredPoints = 0;
    protected final int phase;

    protected ArrayList<String> generatedClues;
    protected ArrayList<String> cluesToReward = new ArrayList<>();

    protected InteractionType interactionType;

    public Interaction(InteractionPi pi, String interactionName, String interactionSolution, int phase) {
        this.interactionPi = pi;
        this.interactionName = interactionName;
        this.status = InteractionStatus.UNSOLVED;
        this.interactionSolution = interactionSolution;
        this.phase = phase;
    }

    public InteractionPi getInteractionPi() {
        return interactionPi;
    }

    public String getInteractionName() {
        return interactionName;
    }

    public InteractionStatus getInteractionStatus() {
        return this.status;
    }

    public String getInteractionSolution() {
        return interactionSolution;
    }

    public int getAcquiredPoints() {
        return acquiredPoints;
    }

    public void setAcquiredPoints(int acquiredPoints) {
        this.acquiredPoints = acquiredPoints;
        this.status = InteractionStatus.SOLVED;
    }

    public int getPhase() {
        return phase;
    }

    public void generateClues() {}

    public ArrayList<String> getGeneratedClues() {
        return generatedClues;
    }

    public void setCluesToReward(ArrayList<String> cluesToReward) {
        this.cluesToReward = cluesToReward;
    }

    public void addCluesToReward(String clue) {
        cluesToReward.add(clue);
    }

    public ArrayList<String> getCluesToReward() {
        return this.cluesToReward;
    }

    public InteractionType getInteractionType() {
        return this.interactionType;
    }
}

