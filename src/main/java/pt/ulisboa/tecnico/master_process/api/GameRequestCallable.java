package pt.ulisboa.tecnico.master_process.api;

import pt.ulisboa.tecnico.rmi.games.GameCommands;
import pt.ulisboa.tecnico.rmi.games.Games;
import pt.ulisboa.tecnico.rmi.interactionpi.InteractionPiProxy;
import pt.ulisboa.tecnico.rmi.interactionpi.InteractionPiResponse;

import java.util.ArrayList;
import java.util.concurrent.Callable;

/**
 * Implements the Callable interface and returns an object of the type InteractionPiResponse. This class was implemented
 * with the purpose of parallelizing the sending of requests to the Interaction Pi processes regarding games.
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class GameRequestCallable implements Callable<InteractionPiResponse> {

    private final InteractionPiProxy interactionPi;
    private final String gameIdentifier;
    private final Games games;
    private final GameCommands command;
    private final ArrayList<String> selectedInteraction;

    public GameRequestCallable(InteractionPiProxy pi, String gameID, Games games, GameCommands command, ArrayList<String> selectedInteractions) {
        this.interactionPi = pi;
        this.gameIdentifier = gameID;
        this.games = games;
        this.command = command;
        this.selectedInteraction = selectedInteractions;
    }

    @Override
    public InteractionPiResponse call() throws Exception {
        return interactionPi.gameRequestsHandler(games, gameIdentifier, command, selectedInteraction);
    }
}