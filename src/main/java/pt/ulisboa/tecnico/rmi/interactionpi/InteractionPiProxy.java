package pt.ulisboa.tecnico.rmi.interactionpi;

import pt.ulisboa.tecnico.rmi.games.GameCommands;
import pt.ulisboa.tecnico.rmi.games.Games;

import java.net.Inet4Address;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public interface InteractionPiProxy extends Remote {
    String deleteGame(String gameID) throws RemoteException;
    String ping() throws RemoteException;
    InteractionPiResponse gameRequestsHandler(Games gamesToStart, String ETR_ID, GameCommands command, ArrayList<String> selectedInteractions) throws Exception;
    String setupBidirectionalLink(Inet4Address IpAddress) throws RemoteException;
    Interactions getAllInteractions() throws RemoteException;
    Interactions getAllAvailableInteractions() throws RemoteException;
    String setInteractionPiLocation(String location) throws RemoteException;
    String getInteractionPiLocation() throws RemoteException;
    String setInteractionSolved(Games game, String gameID, String interactionName) throws RemoteException;
    String sendMessageToInteraction(Games game, String gameID, String interactionName, String message) throws RemoteException;
    String startAllSerialInteractions() throws RemoteException;
    String startAllSerialSensors() throws RemoteException;
    String startAllDevices() throws RemoteException;
}