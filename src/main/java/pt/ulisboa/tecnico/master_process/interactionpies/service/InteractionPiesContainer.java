package pt.ulisboa.tecnico.master_process.interactionpies.service;

import pt.ulisboa.tecnico.master_process.api.MasterPiAPI;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;
import pt.ulisboa.tecnico.rmi.interactionpi.Interactions;
import pt.ulisboa.tecnico.rmi.masterpi.MasterPiProxy;

import java.io.IOException;
import java.net.*;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class InteractionPiesContainer {

    private static final Logger LOGGER = Logger.getLogger(InteractionPiesContainer.class.getName());
    private static final short PORT = 8087;
    private static final ConcurrentHashMap<String, InteractionPi> interactionPies = new ConcurrentHashMap<>();
    private static InteractionPiesContainer interactionPiContainer;

    private InteractionPiesContainer() throws RemoteException, IllegalStateException {
        if (interactionPiContainer != null) {
            throw new IllegalStateException("There is already an Instance of InteractionPiesContainer");
        }

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            System.setProperty("java.rmi.server.hostname", socket.getLocalAddress().getHostAddress());
            MasterPiProxy server = new MasterPiAPI();
            Registry registry = LocateRegistry.createRegistry(PORT);
            registry.rebind("MasterPiAPI", server);
        } catch(SocketException | UnknownHostException exception) {
            exception.printStackTrace();
        }
    }

    public static InteractionPiesContainer getInstance() throws IllegalStateException, RemoteException {
        if(interactionPiContainer == null){
            interactionPiContainer = new InteractionPiesContainer();
        }
        return interactionPiContainer;
    }

    /**
     * Adds an Interaction Pi to the system and established a connection to it
     *
     * @param ipAddress The IPV4 address of the Interaction Pi
     * @param port The port where the Interaction Pi process is running
     * @return Message acknowledging the success of the operation
     * @throws IOException When it is not possible to connect to the Interaction Pi
     * @throws NotBoundException When there is no Interaction Pi process running at the given IP or Port
     */
    public final String addInteractionPi(String ipAddress, int port) throws IOException, NotBoundException {
        try {
            Inet4Address IP = (Inet4Address) Inet4Address.getByName(ipAddress);
            interactionPies.put(IP.getHostAddress(), new InteractionPi(IP, port));

            LOGGER.info("New Interaction Pi @" + IP.getHostAddress() + " successfully added.");

            return "New Interaction Pi added Successfully!";
        } catch (UnknownHostException exception) {
            throw new UnknownHostException("IP Address badly formatted");
        } catch (ConnectException connectException) {
            throw new ConnectException("Connection Refused to Host: " + ipAddress + "; Wrong IP Address or Host is Down.");
        }
    }

    /**
     * Gets an Interaction Pi
     *
     * @param ipAddress The address of the Interaction Pi to get
     * @return The Interaction Pi
     * @throws IllegalArgumentException If an Interaction Pi with the given address does not exist or the IP address is badly formatted
     */
    public final InteractionPi getInteractionPi(String ipAddress) throws IllegalArgumentException {
        try {
            Inet4Address IP = (Inet4Address) Inet4Address.getByName(ipAddress);
            if (!interactionPies.containsKey(IP.getHostAddress())) {
                throw new IllegalArgumentException("The Interaction Pi with address " + IP.getHostAddress() + " does not exist.");
            } else {
                return interactionPies.get(IP.getHostAddress());
            }
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException("IP Address badly formatted");
        }
    }

    /**
     * Removes an Interaction Pi from the system
     *
     * @param ipAddress The address of the Interaction Pi to remove
     * @return Message acknowledging the success of the operation
     */
    public final String removeInteractionPi(String ipAddress) {
        try {
            Inet4Address IP = (Inet4Address) Inet4Address.getByName(ipAddress);
            if (!interactionPies.containsKey(IP.getHostAddress())) {
                throw new IllegalArgumentException("The Interaction Pi with address " + IP.getHostAddress() + " does not exist.");
            } else {
                interactionPies.remove(IP.getHostAddress()).closeConnection();
                return "Interaction Pi@" + IP.getHostAddress() + " removed successfully";
            }
        } catch (UnknownHostException exception) {
            throw new IllegalArgumentException("IP Address badly formatted");
        }
    }

    /**
     * Gets all the interactions from all the Interaction Pies connected to the Master Pi
     *
     * @return An ArrayList containing all of the interactions of the system
     * @throws RemoteException If an error occurs in the retrieval of the Interactions in the Interaction Pies
     */
    public final ArrayList<Interactions> getAllInteractions() throws RemoteException {
        ArrayList<Interactions> interactions = new ArrayList<>();
        for (InteractionPi pi : interactionPies.values()) {
            interactions.add(pi.getAllInteractions());
        }
        return interactions;
    }

    /**
     * Gets all of the interactions from all of the Interaction Pies that are available to participate in a game, removing
     * the interactions that have already been selected by the current game to be used.
     *
     * @param gameID The ID of the game where these interaction will be used
     * @return An ArrayList containing all of the available interactions
     * @throws RemoteException In case an error occurs in one of the Interaction Pies
     */
    public final ArrayList<Interactions> getAllAvailableInteractions(String gameID) throws RemoteException {
        HashMap<String, ArrayList<String>> selectedInteractions = EscapeTheRoomContainer.getInstance().getEscapeTheRoom(gameID).getSelectedInteractions();
        ArrayList<Interactions> interactions = new ArrayList<>();
        for (InteractionPi pi : interactionPies.values()) {
            interactions.add(pi.getAllAvailableInteractions());
        }
        ArrayList<Interactions> processedInteractions = new ArrayList<>();
        for (Interactions i : interactions) {
            if (selectedInteractions.containsKey(i.getAddress().getHostAddress())) {
                ArrayList<String> interactionNames = i.getInteractionNames();
                interactionNames.removeAll(selectedInteractions.get(i.getAddress().getHostAddress()));
                i.setInteractionNames(interactionNames);
            }
            processedInteractions.add(i);
        }
        return processedInteractions;
    }

    //############## Auxiliary Methods Below ###############################

    public synchronized static ArrayList<InteractionPi> getInteractionPies() {
        return new ArrayList<>(interactionPies.values());
    }

    public synchronized static ConcurrentHashMap<String, InteractionPi> getInteractionPiesHashMap() {
        return interactionPies;
    }

}