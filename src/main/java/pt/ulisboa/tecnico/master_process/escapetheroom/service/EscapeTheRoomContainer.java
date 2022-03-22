package pt.ulisboa.tecnico.master_process.escapetheroom.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Container responsible for the creation and storage of the several instances of Escape The Room games. Implements the
 * Singleton pattern
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class EscapeTheRoomContainer {

    private static HashMap<String, EscapeTheRoom> escapeTheRoomHashMap;
    private static HashMap<String, EscapeTheRoom> runningInstances;
    private static EscapeTheRoomContainer instance;

    private EscapeTheRoomContainer() {
        escapeTheRoomHashMap = new HashMap<>();
        runningInstances = new HashMap<>();
    }

    /**
     *
     * @return The unique instance in memory of this class
     */
    public static EscapeTheRoomContainer getInstance() {
        if (instance == null) {
            instance = new EscapeTheRoomContainer();
        }
        return instance;
    }

    /**
     *
     * @return Creates a new Escape The Room instance with the current date and time as identifiers
     */
    public synchronized final EscapeTheRoom createNewEscapeTheRoom() {
        SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy 'at' HH:mm:ss z");
        Date date = new Date(System.currentTimeMillis());
        EscapeTheRoom iteration = new EscapeTheRoom(formatter.format(date));
        escapeTheRoomHashMap.put(iteration.getCreationDate(), iteration);
        return iteration;
    }

    /**
     * Gets an Escape The Room instance
     *
     * @param creationDate The creation date of the instance that serves as its identifier
     * @return The Escape The Room instance
     */
    public final EscapeTheRoom getEscapeTheRoom(String creationDate) {
        EscapeTheRoom instance = escapeTheRoomHashMap.get(creationDate);
        if (instance == null) {
            throw new IllegalArgumentException("There is no Escape The Room with ID " + creationDate);
        }
        return instance;
    }

    /**
     * Deletes an Escape The Room instance
     *
     * @param creationDate The creation date of the instance that serves as its identifier
     * @return Message acknowledging the success of the operation
     * @throws IllegalArgumentException If there is no Escape The Room with such creation date
     * @throws IllegalStateException If the Escape The Room is in a state that does not allow it to be deleted
     */
    public final String deleteEscapeTheRoom(String creationDate) throws IllegalArgumentException, IllegalStateException {
        if (!escapeTheRoomHashMap.containsKey(creationDate)) {
            throw new IllegalArgumentException("There is no Escape The Room with ID " + creationDate);
        } else if (escapeTheRoomHashMap.get(creationDate).getGameState().equals(GameState.RUNNING) || escapeTheRoomHashMap.get(creationDate).getGameState().equals(GameState.PAUSED)) {
            throw new IllegalStateException("This Escape The Room instance is currently Running. Please Stop It first before attempting to remove it");
        } else {
            escapeTheRoomHashMap.remove(creationDate).delete();
            return "The Escape The Room with ID " + creationDate + " was deleted successfully";
        }
    }

    /**
     * Gets all of the instances of Escape The Room
     *
     * @return HashMap containing all of the instances of Escape The Room
     */
    public final HashMap<String, EscapeTheRoom> getAllEscapeTheRooms() {
        return escapeTheRoomHashMap;
    }

    /**
     * Gets all of the instances of Escape The Room that are running
     *
     * @return HashMap containing all of the instances of Escape The Room that are currently running
     */
    public static HashMap<String, EscapeTheRoom> getRunningInstances() {
        return runningInstances;
    }

    /**
     * Places an instance that has been started in the HashMap containing all of the running instances
     *
     * @param gameUID The UID of the game to be added
     * @param instance The Escape The Room instance
     */
    public static void addRunningInstance(String gameUID, EscapeTheRoom instance) {
        runningInstances.put(gameUID, instance);
        //TODO: Remove the Instance from the HashMap (UNCOMMENT BELLOW)
        //escapeTheRoomHashMap.remove(gameUID);
    }

    /**
     * Removes an instance from the currently running instances HashMap
     *
     * @param gameUID The UID of the instance to remove
     */
    public static void removeRunningInstance(String gameUID) {
        runningInstances.remove(gameUID);
    }

}
