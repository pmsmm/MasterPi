package pt.ulisboa.tecnico.master_process.api;

import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPiesContainer;
import pt.ulisboa.tecnico.rmi.StatusMessages;
import pt.ulisboa.tecnico.rmi.games.GameCommands;
import pt.ulisboa.tecnico.rmi.games.Games;
import pt.ulisboa.tecnico.rmi.interactionpi.InteractionPiResponse;
import pt.ulisboa.tecnico.rmi.interactionpi.InteractionResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Responsible for handling all the operations regarding the requests sent to the interaction pies and validating the
 * responses sent back from the latter.
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class GameRequestHandler {

    private static final Logger LOGGER = Logger.getLogger(GameRequestHandler.class.getName());
    private static final int MAX_ATTEMPTS = 3;

    private final ArrayList<InteractionPi> interactionPiArrayList;
    private final ExecutorService cachedThreadPool;
    private final String creationDate;
    private final Games games;
    private HashMap<InteractionPi, InteractionPiResponse> startResponses;

    /**
     * Constructor that instantiates this class with the date of the creation of the game to which requests it will be
     * responsible for and the game itself.
     *
     * @param gameCreationDate the date of creation of the game
     * @param game the name of the game itself
     */
    public GameRequestHandler(String gameCreationDate, Games game) {
        interactionPiArrayList = InteractionPiesContainer.getInteractionPies();
        cachedThreadPool = Executors.newCachedThreadPool();
        this.creationDate = gameCreationDate;
        this.games = game;
    }

    /**
     * Responsible for sending to every participating Interaction Pi the request ordering them to prepared themselves for
     * the iteration of a game.
     *
     * @param selectedInteractions The interactions selected by the user to be used in the game
     * @return Hashmap containing the interaction pies and their respective responses
     * @throws IllegalStateException When there are no available interactions or one of the Interaction Pies fails to answer
     */
    public HashMap<InteractionPi, InteractionPiResponse> readyGame(HashMap<String, ArrayList<String>> selectedInteractions) throws IllegalStateException {
        HashMap<InteractionPi, InteractionPiResponse> responses;

        if (selectedInteractions.size() == 0) {
            responses = this.sendRequest(this.games, GameCommands.READY, null);

            int numberOfInteractions = 0;
            for (InteractionPi pi : responses.keySet()) {
                numberOfInteractions += responses.get(pi).getInteractions().size();
            }
            if (numberOfInteractions == 0) {
                throw new IllegalStateException("There are no available Interactions");
            }

        } else {
            responses = validateInteractionPiesResponses(this.sendRequest(this.games, GameCommands.READY, selectedInteractions));
        }

        return responses;
    }

    /**
     * Responsible for sending the request to the interaction pies that will be participating in the game iteration to start
     */
    public void startGame() {
        this.startResponses = validateInteractionPiesResponses(this.sendRequest(this.games, GameCommands.START, null));
    }

    /**
     * Responsible for sending the request to the interaction pies that are participating in the game iteration to pause
     *
     * @return Hashmap containing the interaction pies and their respective responses
     */
    public HashMap<InteractionPi, InteractionPiResponse> pauseGame() {
        return validateInteractionPiesResponses(this.sendRequest(this.games, GameCommands.PAUSE, null));
    }

    /**
     * Responsible for sending the request to the interaction pies that are participating in the game iteration to stop
     *
     * @return Hashmap containing the interaction pies and their respective responses
     */
    public HashMap<InteractionPi, InteractionPiResponse> stopGame() {
        return validateInteractionPiesResponses(this.sendRequest(this.games, GameCommands.STOP, null));
    }

    /**
     * Responsible for sending the requests to the several interaction pies and process their answers in order to see if
     * any errors have occurred.
     *
     * @param games The game to which this request corresponds to
     * @param command The command regarding the game
     * @param selectedInteractions The selected interactions to which this request will have action on
     * @return Hashmap containing the interaction pies and their respective responses
     */
    private HashMap<InteractionPi, InteractionPiResponse> sendRequest(Games games, GameCommands command, HashMap<String, ArrayList<String>> selectedInteractions) {
        HashMap<InteractionPi, Future<InteractionPiResponse>> futureHashMap = new HashMap<>();

        if (selectedInteractions != null) {
            for (String interactionPiAddress : selectedInteractions.keySet()) {
                for (InteractionPi pi : interactionPiArrayList) {
                    if (pi.getIP().getHostAddress().equals(interactionPiAddress)) {
                        futureHashMap.put(pi, cachedThreadPool.submit(new GameRequestCallable(pi.getProxy(), this.creationDate, games, command, selectedInteractions.get(interactionPiAddress))));
                    }
                }
            }
        } else {
            for (InteractionPi pi : interactionPiArrayList) {
                futureHashMap.put(pi, cachedThreadPool.submit(new GameRequestCallable(pi.getProxy(), this.creationDate, games, command, null)));
            }
        }

        HashMap<InteractionPi, InteractionPiResponse> responses = new HashMap<>();

        for (InteractionPi pi : futureHashMap.keySet()) {
            try {
                responses.put(pi, (futureHashMap.get(pi)).get(60, TimeUnit.SECONDS));
            } catch (InterruptedException | ExecutionException | TimeoutException | NullPointerException e) {
                LOGGER.severe(e.getMessage());
                try {
                    responses.put(pi, retryRequest(pi, games, command, selectedInteractions));
                } catch (IllegalStateException exception) {
                    LOGGER.severe(exception.getMessage());
                }
            }
        }

        return responses;
    }

    /**
     * Retries the request to an Interaction Pi
     *
     * @param pi The Interaction Pi to which the request will be sent to
     * @param games The game to which this request is intended for
     * @param command The command regarding the game
     * @param selectedInteractions The selected interactions to which this request will have action on
     * @return The response returned by the Interaction Pi
     * @throws IllegalStateException When it is not possible to retrieve an answer from the Interaction Pi
     */
    private InteractionPiResponse retryRequest(InteractionPi pi, Games games, GameCommands command, HashMap<String, ArrayList<String>> selectedInteractions) throws IllegalStateException {
        int attempts = 0;
        InteractionPiResponse answer = null;

        while (answer == null && attempts < MAX_ATTEMPTS) {
            try {
                if (selectedInteractions != null) {
                    answer = (cachedThreadPool.submit(new GameRequestCallable(pi.getProxy(), this.creationDate, games, command, selectedInteractions.get(pi.getIP().getHostAddress())))).get(5, TimeUnit.SECONDS);
                } else {
                    answer = (cachedThreadPool.submit(new GameRequestCallable(pi.getProxy(), this.creationDate, games, command, null))).get(5, TimeUnit.SECONDS);
                }
            } catch (InterruptedException | ExecutionException | TimeoutException exception) {
                LOGGER.warning("Failed to Retrieve Answer From Interaction Pi@" + pi.getIP().getHostAddress() + " Number of Attempts: " + ++attempts);
            }
        }

        if (answer == null) {
            LOGGER.severe("Failed to Retrieve an Answer from Interaction Pi@" + pi.getIP().getHostAddress() + " after several attempts");
            throw new IllegalStateException("Failed to receive an answer from all Interaction Pi's");
        }

        return answer;
    }

    /**
     * Checks to see if all the responses returned from the Interaction Pies are valid of if there are any errors
     *
     * @param responses The responses returned from the Interaction Pies
     * @return The responses returned from the Interaction Pies
     * @throws IllegalStateException If there are errors in the answers returned by the Interaction Pies
     */
    private HashMap<InteractionPi, InteractionPiResponse> validateInteractionPiesResponses(HashMap<InteractionPi, InteractionPiResponse> responses) throws IllegalStateException {
        ArrayList<String> errorMessages = new ArrayList<>();
        for (InteractionPi pi : responses.keySet()) {
            for (InteractionResponse interactionResponse : responses.get(pi).getInteractions()) {
                if (interactionResponse.getMessageStatus() == StatusMessages.ERROR) {
                    errorMessages.add(interactionResponse.getSolution());
                }
            }
        }

        if (errorMessages.size() > 0) {
            for (String error : errorMessages) {
                LOGGER.severe(error);
            }
            throw new IllegalStateException("Error(s) detected in Interaction Response(s)");
        }

        return responses;
    }

    /**
     * @return The responses returned from the Interaction Pies to the start request.
     */
    public HashMap<InteractionPi, InteractionPiResponse> getStartResponses() {
        return this.startResponses;
    }

    /**
     * @return The arraylist of the Interaction Pies that are participating in the game iteration
     */
    public ArrayList<InteractionPi> getInteractionPiArrayList() {
        return interactionPiArrayList;
    }

}
