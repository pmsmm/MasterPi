package pt.ulisboa.tecnico.rmi.masterpi;

import pt.ulisboa.tecnico.rmi.games.Games;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public interface MasterPiProxy extends Remote {
    String test() throws RemoteException;
    void setAcquiredPoints(Games game, String gameUID, String interactionFriendlyID, int points) throws RemoteException;
    void reportDeviceFailure(Games gameName, String gameUID, String interactionIdentifier) throws RemoteException;
    void reportDeviceFailure(String deviceIdentifier) throws RemoteException;
}