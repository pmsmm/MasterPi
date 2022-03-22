package pt.ulisboa.tecnico.master_process.api;

import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;
import pt.ulisboa.tecnico.rmi.games.Games;
import pt.ulisboa.tecnico.rmi.masterpi.MasterPiProxy;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.logging.Logger;

/**
 * Implementation of the API methods of the Master Pi.
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class MasterPiAPI extends UnicastRemoteObject implements MasterPiProxy {

    private final static Logger LOGGER = Logger.getLogger(MasterPiAPI.class.getName());

    public MasterPiAPI() throws RemoteException {
    }

    /**
     * Used for guaranteeing that the connection was established between the Interaction Pi and the Master Pi
     *
     * @return String symbolizing the successful connection
     * @throws RemoteException
     * @deprecated This method is deprecated and should be deleted or replaced by another.
     */
    @Override
    public String test() throws RemoteException {
        return "Communication with Master Pi Established Successfully";
    }

    /**
     * Credits the points to the respective interaction when that interaction has been solved.
     *
     * @param game The name of the game where this interaction is being used
     * @param gameUID The ID of the Game where this interaction is being used
     * @param interactionFriendlyID The user friendly name of the interaction
     * @param points The amount of points to be credited for the resolution of this interaction
     * @throws IllegalArgumentException When there is no Interaction with interactionFriendlyID in the system
     */
    @Override
    public void setAcquiredPoints(Games game, String gameUID, String interactionFriendlyID, int points) throws IllegalArgumentException {
        switch (game) {
            case ESCAPE_THE_ROOM:
                EscapeTheRoomContainer.getRunningInstances().get(gameUID).setAcquiredPoints(interactionFriendlyID, points);
                break;
        }
    }

    /**
     * Reports failures that may occur to an interaction to the game that is using that interaction so that it can deal
     * with the error appropriately.
     *
     * @param gameName The name of the game
     * @param gameUID The unique identifier of the game instance
     * @param interactionIdentifier The interaction identifier
     */
    @Override
    public void reportDeviceFailure(Games gameName, String gameUID, String interactionIdentifier) {
        switch (gameName) {
            case ESCAPE_THE_ROOM:
                EscapeTheRoomContainer.getRunningInstances().get(gameUID).failureHandler(interactionIdentifier);
                break;
        }
    }

    @Override
    public void reportDeviceFailure(String deviceIdentifier) {

    }
}
