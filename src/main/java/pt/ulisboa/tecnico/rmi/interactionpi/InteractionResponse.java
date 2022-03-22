package pt.ulisboa.tecnico.rmi.interactionpi;

import pt.ulisboa.tecnico.rmi.StatusMessages;

import java.io.Serializable;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class InteractionResponse implements Serializable {

    private final String interactionName;
    private final StatusMessages messageStatus;
    private final String gameUID;
    private final String solution;

    public InteractionResponse(String interactionName, StatusMessages status, String gameID, String solution) {
        this.interactionName = interactionName;
        this.messageStatus = status;
        this.gameUID = gameID;
        this.solution = solution;
    }

    public String getInteractionName() {
        return interactionName;
    }

    public StatusMessages getMessageStatus() {
        return messageStatus;
    }

    public String getGameUID() {
        return gameUID;
    }

    public String getSolution() {
        return solution;
    }
}
