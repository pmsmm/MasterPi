package pt.ulisboa.tecnico.master_process.interactions;

import java.util.ArrayList;

public interface InteractionInterface {

    void generateClues();
    ArrayList<String> getGeneratedClues();
    void setCluesToReward(ArrayList<String> cluesToReward);
    void addCluesToReward(String clue);
    ArrayList<String> getCluesToReward();
    InteractionType getInteractionType();

}
