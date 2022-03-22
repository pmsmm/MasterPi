package pt.ulisboa.tecnico.master_process.interactionpies.service;

import pt.ulisboa.tecnico.rmi.interactionpi.InteractionPiProxy;
import pt.ulisboa.tecnico.rmi.interactionpi.Interactions;

import java.io.IOException;
import java.net.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Representation of an Interaction Pi
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class InteractionPi {

    private static final Logger LOGGER = Logger.getLogger(InteractionPi.class.getName());
    private static final ExecutorService threadPool = Executors.newFixedThreadPool(2);

    private final Inet4Address IP;
    private final int PORT;

    private InteractionPiProxy serverProxy;

    public InteractionPi(Inet4Address ipAddress, int port) throws IOException, NotBoundException {
        serverProxy = (InteractionPiProxy) LocateRegistry.getRegistry(ipAddress.getHostAddress(), 8086).lookup("InteractionPiAPI");
        this.IP = ipAddress;
        this.PORT = port;

        try(final DatagramSocket socket = new DatagramSocket()){
            socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
            LOGGER.info(serverProxy.setupBidirectionalLink((Inet4Address) socket.getLocalAddress()));
        } catch(SocketException | UnknownHostException exception) {
            exception.printStackTrace();
        }

        threadPool.submit(new PingThread(serverProxy, ipAddress));
    }

    /**
     * Retrieves all of the interactions that are connected to this Interaction Pi
     *
     * @return Interactions object containing all of the interactions connected to this Interaction Pi
     * @throws RemoteException In case an error occurs in the Interaction Pi side
     */
    public Interactions getAllInteractions() throws RemoteException{
        Interactions interactions = serverProxy.getAllInteractions();
        interactions.setAddress(this.IP);
        interactions.setPort(this.PORT);
        return interactions;
    }

    /**
     * Retrieves all of the interactions that are available to be used in a game that are connected to this Interaction Pi
     *
     * @return Interactions object containing all of the interactions connected to this Interaction Pi that are available
     * @throws RemoteException In case an error occurs in the Interaction Pi side
     */
    public Interactions getAllAvailableInteractions() throws RemoteException {
        Interactions interactions = serverProxy.getAllAvailableInteractions();
        interactions.setAddress(this.IP);
        interactions.setPort(this.PORT);
        return interactions;
    }

    public void closeConnection() {
        serverProxy = null;
    }

    //################################### Auxiliary Methods ########################################

    public Inet4Address getIP() {
        return IP;
    }

    public int getPORT() {
        return PORT;
    }

    public InteractionPiProxy getProxy() {
        return serverProxy;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InteractionPi pi = (InteractionPi) o;
        return getPORT() == pi.getPORT() && getIP().equals(pi.getIP());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIP());
    }
}
