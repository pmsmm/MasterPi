package pt.ulisboa.tecnico.master_process.escapetheroom.service;

import pt.ulisboa.tecnico.master_process.api.GameInterface;
import pt.ulisboa.tecnico.master_process.api.GameRequestHandler;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant;
import pt.ulisboa.tecnico.master_process.interactionpies.service.Interaction;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPi;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPiesContainer;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionStatus;
import pt.ulisboa.tecnico.master_process.interactions.InteractionFactory;
import pt.ulisboa.tecnico.master_process.interactions.InteractionType;
import pt.ulisboa.tecnico.rmi.games.Games;
import pt.ulisboa.tecnico.rmi.interactionpi.InteractionResponse;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Represents an iteration of the Escape The Room game and contains all the information regarding it.
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class EscapeTheRoom implements GameInterface {

    private static final Logger LOGGER = Logger.getLogger(EscapeTheRoom.class.getName());
    private static final Games GAMES = Games.ESCAPE_THE_ROOM;
    private static final int MAX_PARTICIPANTS = 4;
    private static final int MIN_PARTICIPANTS = 2;
    private static final int ESCAPE_THE_ROOM_DURATION_IN_SECONDS = 3600;

    private final ScheduledExecutorService executor;
    private final HashMap<Integer, Participant> participants;
    private final HashMap<String, ArrayList<String>> selectedInteractions;
    //TODO: This solution is TEMPORARY and should be improved
    private final HashMap<String, Integer> interactionsPhase;
    private ArrayList<Interaction> interactions;
    private final String creationDate;
    private final AtomicInteger uniqueID = new AtomicInteger(0);
    private GameRequestHandler handler;
    private double SCORE;
    private int ELAPSED_TIME;
    private String groupName;
    private String startDate;
    private String endDate;
    private GameState gameState;

    public EscapeTheRoom(String creationDate) {
        this.creationDate = creationDate;
        this.gameState = GameState.NOT_READY;
        this.participants = new HashMap<>();
        this.selectedInteractions = new HashMap<>();
        this.interactionsPhase = new HashMap<>();
        this.interactions = new ArrayList<>();
        this.executor = Executors.newSingleThreadScheduledExecutor();
        LOGGER.fine("Created new Escape The Room Game with ID " + this.getCreationDate());
    }

    /**
     * Adds a participant to this escape the room game
     *
     * @param participant The participant to be added
     * @return String symbolizing that the operation was performed successfully
     */
    public final String addParticipant(Participant participant) {
        if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
            try {
                if (participants.size() < MAX_PARTICIPANTS) {
                    participant.setID(uniqueID.getAndIncrement());
                    participants.put(participant.getID(), participant);
                    return "Participant " + participant.getFirstName() + " " + participant.getLastName() + " Added Successfully";
                } else {
                    throw new IllegalStateException("Unable to Add Participant. Maximum Number of Participants Reached");
                }
            } finally {
                checkState();
            }
        } else {
            throw new IllegalStateException("This Escape The Room is currently Running or Finished. Unable to Add Participant");
        }
    }

    /**
     * Get all the participants that are enlisted to participate in this iteration of the Escape The Room game
     *
     * @return Array containing the participants
     */
    public final ArrayList<Participant> getAllParticipants() {
        return new ArrayList<>(participants.values());
    }

    /**
     * Removes a participant from this Escape The Room instance
     *
     * @param participantID The ID of the participant that is to be removed
     * @return Message acknowledging a successful removal of the participant
     * @throws IllegalArgumentException When there is no participant with the given ID
     * @throws IllegalStateException When the Escape The Room is in a state that does not allow the removal of participants
     */
    public final String removeParticipant(int participantID) throws IllegalStateException, IllegalArgumentException {
        if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
            try {
                if (!participants.containsKey(participantID)) {
                    throw new IllegalArgumentException("There is no such participant with ID " + participantID + ".");
                } else {
                    Participant participantRemoved = participants.remove(participantID);
                    LOGGER.info("Removed Participant " + participantRemoved.getFirstName() + " "
                            + participantRemoved.getLastName() + " with ID " + participantRemoved.getID());
                    return "Participant Removed Successfully";
                }
            } finally {
                checkState();
            }
        } else {
            throw new IllegalStateException("This Escape The Room is currently Running or Finished. Unable to Remove Participant");
        }
    }

    /**
     * Removes all of the participants enlisted to participate in this iteration of Escape The Room
     *
     * @return Message acknowledging a successful removal of the participant
     * @throws IllegalStateException When the Escape The Room is in a state that does not allow the removal of participants
     */
    public final String removeAllParticipants() throws IllegalStateException{
        if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
            this.participants.clear();
            this.gameState = GameState.NOT_READY;
            return "All Participants Removed";
        } else {
            throw new IllegalStateException("This Escape The Room is currently Running or Finished. Unable to Remove Participants");
        }
    }

    /**
     * Adds an interaction to be used in this iteration of Escape The Room
     *
     * @param interactionPiIPAddress The address of the Interaction Pi that this interaction is connected to
     * @param interactionName The name of the interaction to be added
     * @param phase The phase that this interaction belongs to
     * @return Message acknowledging a successful addition of an interaction to this iteration
     * @throws IllegalStateException When the Escape The Room is in a state that does not allow the addition of interactions
     */
    public final String addInteraction(String interactionPiIPAddress, String interactionName, int phase) throws IllegalStateException{
        if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
            if (!selectedInteractions.containsKey(interactionPiIPAddress)) {
                ArrayList<String> interactionNames = new ArrayList<>();
                interactionNames.add(interactionName);
                selectedInteractions.put(interactionPiIPAddress, interactionNames);
            } else {
                ArrayList<String> interactionNames = selectedInteractions.get(interactionPiIPAddress);
                if (interactionNames.contains(interactionName)) {
                    return "Interaction Already Added";
                } else {
                    interactionNames.add(interactionName);
                    selectedInteractions.replace(interactionPiIPAddress, interactionNames);
                }
            }
            //TEMPORARY
            interactionsPhase.put(interactionName, phase);
            //END OF TEMPORARY
            return "Selected Interaction Added Successfully";
        } else {
            throw new IllegalStateException("This Escape The Room is currently Running or Finished. Unable to Add Interaction");
        }
    }

    /**
     * Removes an interaction from this iteration of Escape The Room
     *
     * @param interactionPiIPAddress The address of the Interaction where the interaction is connected to
     * @param interactionName The name of the interaction to be removed
     * @return Message acknowledging the success of the operation
     * @throws IllegalStateException When the Escape The Room is in a state that does not allow the removal of interactions
     */
    public final String removeInteraction(String interactionPiIPAddress, String interactionName) throws IllegalStateException{
        if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
            if (!selectedInteractions.containsKey(interactionPiIPAddress)) {
                return "The InteractionPi with IP " + interactionPiIPAddress + " does not exist";
            } else {
                ArrayList<String> interactionNames = selectedInteractions.get(interactionPiIPAddress);
                for (String name : interactionNames) {
                    if (name.equals(interactionName)) {
                        interactionNames.remove(interactionName);
                        selectedInteractions.replace(interactionPiIPAddress, interactionNames);
                        return "Interaction " + interactionName + " Was Removed Successfully";
                    }
                }
            }
            //TEMPORARY
            interactionsPhase.remove(interactionName);
            //END OF TEMPORARY
            return "The Interaction " + interactionName + " Does Not Exist";
        } else {
            throw new IllegalStateException("This Escape The Room is currently Running or Finished. Unable to Remove Interaction");
        }
    }

    /**
     * Removes all the interactions that were meant to be included in this iteration of Escape The Room
     *
     * @param interactionPiIPAddress Address of the Interaction Pi whose interactions shall all be removed. If this parameter
     *                               is null then all of the interactions from all the Interaction Pies shall be removed from
     *                               this iteration.
     * @return Message acknowledging the success of the operation
     * @throws IllegalStateException When the Escape The Room is in a state that does not allow the removal of interactions
     */
    public final String removeAllInteractions(String interactionPiIPAddress) throws IllegalStateException{
        if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
            if (interactionPiIPAddress == null) {
                selectedInteractions.clear();
                //TEMPORARY
                interactionsPhase.clear();
                //END OF TEMPORARY
                return "Removed All Selected Interactions";
            } else {
                if (!selectedInteractions.containsKey(interactionPiIPAddress)) {
                    return "The InteractionPi with IP " + interactionPiIPAddress + " does not exist.";
                } else {
                    selectedInteractions.remove(interactionPiIPAddress);
                    return "Removed All Selected Interactions from InteractionPi@" + interactionPiIPAddress;
                }
            }
        } else {
            throw new IllegalStateException("This Escape The Room is currently Running or Finished. Unable to Remove Interactions");
        }
    }

    /**
     * Starts this iteration of Escape The Room
     *
     * @return Message acknowledging the success of the operation
     * @throws IllegalStateException When the state of this iteration does not allow it to start. This could be due to the
     * absence of Interaction Pies, not enough participants, empty group name or invalid state that does not allow the game
     * to start.
     */
    @Override
    public final String startGame() throws IllegalStateException{
        if (InteractionPiesContainer.getInteractionPies().size() == 0) {
            throw new IllegalStateException("There are no Interaction Pies to start this Escape The Room");
        }

        if (participants.size() < MIN_PARTICIPANTS) {
            throw new IllegalStateException("There are not enough players to start this Escape The Room. At least two are required");
        }

        if (groupName == null || groupName.equals("")) {
            throw new IllegalStateException("The Group Name cannot be empty");
        }

        try {
            if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
                this.handler = new GameRequestHandler(this.creationDate, GAMES);

                this.handler.readyGame(selectedInteractions);

                this.handler.startGame();

                for (InteractionPi pi : handler.getStartResponses().keySet()) {
                    for (InteractionResponse response : handler.getStartResponses().get(pi).getInteractions()) {
                        interactions.add(InteractionFactory.getInteraction(pi, response.getInteractionName(), response.getSolution(), interactionsPhase.get(response.getInteractionName())));
                    }
                }

                this.generateAndDistributeClues();

                this.executor.scheduleAtFixedRate(new EscapeTheRoomCountdownTimerTask(this, ESCAPE_THE_ROOM_DURATION_IN_SECONDS), 0, 1, TimeUnit.SECONDS);
                this.gameState = GameState.RUNNING;
                EscapeTheRoomContainer.addRunningInstance(this.creationDate, this);
                this.startDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

                return "Escape The Room has now commenced.";
            } else {
                throw new IllegalStateException("This Escape The Room is currently Running or Finished. Unable to Restart");
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.severe("A problem occurred during start request. " + e.getMessage());
            handler.stopGame();
            throw e;
        }
    }

    /**
     * Generates the clues based on the solution generated by each interaction and then distributes the clues through the
     * several interactions
     */
    private void generateAndDistributeClues() {
        int initialPhase = 1, finalPhase = 1;
        HashMap<Integer, ArrayList<Interaction>> interactionsByPhase = new HashMap<>();

        LOGGER.info("Processing Solutions and Generating Clues...");
        for (Interaction interaction : interactions) {
            interaction.generateClues();
            if (interactionsByPhase.containsKey(interaction.getPhase())) {
                ArrayList<Interaction> arrayOfInteractions = interactionsByPhase.get(interaction.getPhase());
                arrayOfInteractions.add(interaction);
                interactionsByPhase.replace(interaction.getPhase(), arrayOfInteractions);
            } else {
                ArrayList<Interaction> interactionsArray = new ArrayList<>();
                interactionsArray.add(interaction);
                interactionsByPhase.put(interaction.getPhase(), interactionsArray);
            }

            if (interaction.getPhase() < initialPhase) {
                initialPhase = interaction.getPhase();
            }

            if (interaction.getPhase() > finalPhase) {
                finalPhase = interaction.getPhase();
            }

        }

        LOGGER.info("Finished Generating Clues and Organizing Interactions by Phase");

        LOGGER.info("Commencing Clue Distribution...");

        HashMap<String, Interaction> interactionsWithCluesAssigned = new HashMap<>();
        int currentPhase = initialPhase;
        while (currentPhase != (finalPhase + 1)) {
            ArrayList<String> clues = new ArrayList<>();
            ArrayList<Interaction> interactionsToDistributeCluesThrough = new ArrayList<>();

            //Get the generated clues from the secondary interactions of the current phase
            for (Interaction interaction : interactionsByPhase.get(currentPhase)) {
                if (interaction != null) {
                    if (interaction.getInteractionType() == InteractionType.SECONDARY) {
                        clues.addAll(interaction.getGeneratedClues());
                    } else {
                        interactionsToDistributeCluesThrough.add(interaction);
                    }
                } else {
                    LOGGER.warning("Phase Skip Detected. Phase " + currentPhase + " does not exist.");
                }
            }

            //Get all of the interactions from the previous phases and add them to the array of interactions to distribute clues through
            for (int i = currentPhase; i >= initialPhase; i--) {
                if (i == currentPhase) {
                    for (Interaction interaction : interactionsByPhase.get(i)) {
                        if (interaction.getInteractionType() == InteractionType.PRIMARY) {
                            interactionsToDistributeCluesThrough.add(interaction);
                        }
                    }
                } else {
                    interactionsToDistributeCluesThrough.addAll(interactionsByPhase.get(i));
                }
                if (i == finalPhase) {
                    for (Interaction interaction : interactionsByPhase.get(i)) {
                        if (interaction.getInteractionType() == InteractionType.SECONDARY) {
                            interactionsWithCluesAssigned.put(interaction.getInteractionName(), interaction);
                        }
                    }
                }
            }

            //Evenly Distribute Clues in the clues array throughout the interactions of the current and previous phases
            int numInteractions = interactionsToDistributeCluesThrough.size();
            int numItems = clues.size();

            int cluesPerInteraction = (numItems / numInteractions);
            int remainingClues = (numItems % numInteractions);

            for (int i = 1; i <= numInteractions; i++) {
                int extra = (i <= remainingClues) ? 1:0;
                Interaction interaction = interactionsToDistributeCluesThrough.get(i-1);
                for (int j = 0; j < (cluesPerInteraction + extra); j++) {
                    if (clues.size() == 0) {
                        break;
                    }
                    interaction.addCluesToReward(clues.remove(0));
                }
                interactionsWithCluesAssigned.put(interaction.getInteractionName(), interaction);
            }
            currentPhase++;
        }

        this.interactions = new ArrayList<>(interactionsWithCluesAssigned.values());

        LOGGER.info("Finished distributing clues through the several interactions");

    }

    /*TODO: Persist this Escape The Room iteration on the database even if it is not completed in order to use the fact
       that is completed or not for future data analysis */

    /**
     * Stops the Escape The Room game
     *
     * @param state The state that this iteration finishes on, which can either be FINISHED if the game was concluded by
     *              the players or NOT_FINISHED if the players failed to conclude the game or an error occurred during gameplay
     * @return Message acknowledging the success of the operation or null in case of an error
     * @throws IllegalStateException When the Escape The Room is in a state that does not allow a user to stop it
     */
    @Override
    public final String stopGame(GameState state) throws IllegalStateException{
        if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
            try {
                this.endDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));
                if (state != null) {
                    this.gameState = state;
                } else {
                    this.gameState = GameState.FINISHED;
                }
                this.handler.stopGame();
                this.executor.shutdown();
                EscapeTheRoomContainer.removeRunningInstance(this.creationDate);
                LOGGER.info("Escape The Room with ID " + creationDate + " terminated successfully");
                return "Escape The Room with ID " + creationDate + " terminated successfully";
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
                return null;
            }
        } else {
            throw new IllegalStateException("This Escape The Room is not currently Running. Unable to Stop it");
        }
    }

    /**
     * Pauses this Escape The Room game
     *
     * @return Message acknowledging the success or failure of the operation
     * @throws IllegalStateException When the Escape The Room is in a state that does not allow a user to pause it
     */
    @Override
    public final String pauseGame() throws IllegalStateException{
        if (gameState == GameState.RUNNING || gameState == GameState.PAUSED) {
            try {
                if (gameState == GameState.RUNNING) {
                    this.gameState = GameState.PAUSED;
                } else {
                    this.gameState = GameState.RUNNING;
                }
                this.handler.pauseGame();
                return "The Game is now " + this.gameState.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
        } else {
            throw new IllegalStateException("This Escape The Room is not currently Running. Unable to Pause it");
        }
    }

    /**
     * Credits points to an interaction that has been solved.
     *
     * @param interactionID The ID of the interaction to which the points should be credited to
     * @param acquiredPoints The amount of points to credit
     * @throws IllegalArgumentException If there is not an interaction with the given ID
     */
    public final void setAcquiredPoints(String interactionID, int acquiredPoints) throws IllegalArgumentException {
        for (Interaction interaction : interactions) {
            if (interaction.getInteractionName().equals(interactionID)) {
                LOGGER.info("Crediting " + acquiredPoints + " Points to " + interactionID);
                interaction.setAcquiredPoints(acquiredPoints);
                checkIfConcluded();
                return;
            }
        }
        throw new IllegalArgumentException("The Required Interaction with ID: " + interactionID + " was not found.");
    }

    /**
     * Checks all of the interactions that are being used in this iteration of the game and checks to see if all of them
     * have been solved in order to terminate the Escape The Room game.
     */
    private void checkIfConcluded() {
        for (Interaction interaction : interactions) {
            if (interaction.getInteractionStatus() == InteractionStatus.UNSOLVED) {
                return;
            }
        }
        this.calculateScore();
        this.stopGame(null);
    }

    /**
     * Calculates the score achieved by the participants of this iteration. This is achieved by checking the score attributed
     * by each interaction that has been correctly solved and summing them all together as well as a calculation regarding
     * the bonus achieved by completing the game in the least time possible.
     *
     * @return The calculated score
     */
    private double calculateScore() {
        int totalScore = 0;
        double timeBonus = (Math.abs((((double)getELAPSED_TIME()*100)/ESCAPE_THE_ROOM_DURATION_IN_SECONDS)-100)/100) + 1;
        for (Interaction interaction : interactions) {
            totalScore += interaction.getAcquiredPoints();
        }
        this.SCORE = totalScore * timeBonus;
        return this.SCORE;
    }

    //########################################### Auxiliary Methods ####################################################

    /**
     *
     * @return The creation date of this iteration
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     *
     * @return The start date of this iteration
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     *
     * @return The end date of this iteration
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     *
     * @return The score the participants achieved in this iteration
     */
    public double getScore() {
        return SCORE;
    }

    /*
    * TODO: REMOVE THESE SETTERS SINCE THEY ARE TEMPORARY
    * */

    public void setSCORE(double SCORE) {
        this.SCORE = SCORE;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    //###################################################

    /**
     *
     * @return All of the interactions that are part of this iteration
     */
    public final ArrayList<Interaction> getAllInteractions() {
        return interactions;
    }

    /**
     * Checks if a certain interaction is being used in this escape the room.
     *
     * @param interactionClassAsString The simple name of the interaction class.
     * @return True if this Escape The Room is using this interaction, false otherwise.
     */
    public final boolean containsInteraction(String interactionClassAsString) {
        for (Interaction interaction : interactions) {
            if (interaction.getClass().getSimpleName().equals(interactionClassAsString)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Gets an interaction by its class name.
     *
     * @param interactionClassAsString The simple name of the interaction class.
     * @return The interaction or null if this interaction is not being used in this Escape The Room.
     */
    public final Interaction getInteractionByClassName(String interactionClassAsString) {
        for (Interaction interaction : interactions) {
            if (interaction.getClass().getSimpleName().equals(interactionClassAsString)) {
                return interaction;
            }
        }
        return null;
    }

    /**
     *
     * @return The clues that the participants already obtained
     */
    public final ArrayList<String> getAcquiredClues() {
        ArrayList<String> acquiredClues = new ArrayList<>();
        for (Interaction interaction : interactions) {
            if (interaction.getInteractionStatus() == InteractionStatus.SOLVED) {
                acquiredClues.addAll(interaction.getCluesToReward());
            }
        }
        return acquiredClues;
    }

    /**
     * Checks to see if the state of this iteration can be changed
     */
    private void checkState() {
        if (participants.size() >= 2 && groupName != null) {
            this.gameState = GameState.READY;
        }
    }

    /**
     *
     * @return The current state of the game
     */
    public GameState getGameState() {
        return this.gameState;
    }

    /**
     * Sets the name of the group of players
     *
     * @param name The name of the group
     * @return Message acknowledging the success of the operation
     */
    public final String setGroupName(String name) {
        if (gameState == GameState.READY || gameState == GameState.NOT_READY) {
            try {
                if (name == null || name.trim().equals("")) {
                    throw new IllegalArgumentException("Name cannot be empty or null");
                }
                String trimmedName = name.trim();
                if (trimmedName.length() < 4) {
                    throw new IllegalArgumentException("Please introduce a name with 4 or more letters/digits");
                }
                this.groupName = trimmedName;
                LOGGER.fine("Set the Group name to " + trimmedName);
                return "Group Name was set to " + trimmedName;
            } finally {
                checkState();
            }
        } else {
            throw new IllegalStateException("This Escape The Room is not currently Running. Unable to change group name");
        }
    }

    /**
     *
     * @return The current name of the group
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     *
     * @return The duration in seconds of the Escape The Room
     */
    public int getEscapeTheRoomDurationInSeconds() {
        return ESCAPE_THE_ROOM_DURATION_IN_SECONDS;
    }

    /**
     *
     * @return The time that already passed since the start of the game
     */
    public int getELAPSED_TIME() {
        return ELAPSED_TIME;
    }

    /**
     *
     * @param elapsedTime The time already passed since the start of the game
     */
    public void setELAPSED_TIME(int elapsedTime) {
        this.ELAPSED_TIME = elapsedTime;
    }

    /**
     *
     * @return The current score that has been achieved by the participants
     */
    public double getCurrentScore() {
        return calculateScore();
    }

    /**
     *
     * @return The percentage of conclusion of the game
     */
    public int getPercentageOfConclusion() {
        int solvedInteractions = 0;
        for (Interaction interaction : interactions) {
            if (interaction.getAcquiredPoints() > 0)
                solvedInteractions++;
        }
        return (solvedInteractions / interactions.size()) * 100;
    }

    /**
     *
     * @return HashMap containing the interactions selected by the user to be used in this iteration of the game
     */
    public HashMap<String, ArrayList<String>> getSelectedInteractions() {
        return selectedInteractions;
    }

    /**
     * Deletes this game instance in all of the Interaction Pies that are being used to run it in the case that an error
     * occurs
     */
    public void delete() {
        if (this.handler != null && this.gameState == GameState.READY) {
            try {
                for (InteractionPi pi : this.handler.getInteractionPiArrayList()) {
                    LOGGER.info(pi.getProxy().deleteGame(this.creationDate));
                }
            } catch (Exception e) {
                LOGGER.severe(e.getMessage());
            }
        }
        this.executor.shutdown();
    }

    /**
     * Handles failures reported by the interaction pies involved in this iteration of the game and proceeds to either
     * terminate the game in case of a non recoverable failure or simply warns the user that something occurred and allows
     * the game to continue
     *
     * @param interactionIdentifier The identifier of the interaction that reported an error
     */
    public void failureHandler(String interactionIdentifier) {
        LOGGER.severe("Catastrophic Failure Detected. Interaction " + interactionIdentifier + " has failed.");
        for (Interaction interaction : interactions) {
            if (interaction.getInteractionName().equals(interactionIdentifier)) {
                if (interaction.getInteractionStatus() == InteractionStatus.SOLVED) {
                    LOGGER.warning("Interaction " + interactionIdentifier + " failure does not affect game execution. Clear to proceed");
                } else {
                    LOGGER.severe("Interaction " + interactionIdentifier + " has suffered a critical failure. Stopping Escape The Room...");
                    this.stopGame(GameState.NOT_FINISHED);
                    //TODO: I can return a boolean symbolizing that the Game is going to be stopped
                }
            }
        }
    }
}