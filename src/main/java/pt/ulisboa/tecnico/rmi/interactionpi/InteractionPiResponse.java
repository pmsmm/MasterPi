package pt.ulisboa.tecnico.rmi.interactionpi;

import pt.ulisboa.tecnico.rmi.games.Games;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class InteractionPiResponse implements Serializable {

    private ArrayList<InteractionResponse> interactions;
    private final Games game;
    private final String gameID;

    public InteractionPiResponse(String gameID, Games game) {
        this.interactions = new ArrayList<>();
        this.game = game;
        this.gameID = gameID;
    }

    public void setInteractions(ArrayList<InteractionResponse> responses) {
        this.interactions = responses;
    }

    public ArrayList<InteractionResponse> getInteractions() {
        return interactions;
    }

    public Games getGame() {
        return game;
    }

    public String getGameID() {
        return gameID;
    }
}
