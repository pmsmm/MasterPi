package pt.ulisboa.tecnico.master_process.interactionpies.service;

import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoom;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;
import pt.ulisboa.tecnico.rmi.interactionpi.InteractionPiProxy;

import java.net.Inet4Address;
import java.rmi.RemoteException;
import java.util.concurrent.*;
import java.util.logging.Logger;

/**
 * Responsible for continuously pinging the several Interaction Pies connected to the Master Pi
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class PingThread implements Runnable{

    private final static Logger LOGGER = Logger.getLogger(PingThread.class.getName());

    private final InteractionPiProxy proxy;
    private final Inet4Address ip;
    private final ExecutorService threadPool = Executors.newFixedThreadPool(1);

    public PingThread(InteractionPiProxy interactionPiProxy, Inet4Address ipAddress) {
        this.proxy = interactionPiProxy;
        this.ip = ipAddress;
    }

    @Override
    public void run() {
        int numberOfFailedPings = 0;
        while (numberOfFailedPings < 3) {
            Future<String> pingResult = threadPool.submit(new PingCallable(proxy));
            try {
                pingResult.get(10, TimeUnit.SECONDS);
                numberOfFailedPings = 0;
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                LOGGER.warning("Ping Not Acknowledged. Failed Attempts: " + ++numberOfFailedPings);
            } finally {
                try {
                    if (numberOfFailedPings < 3) {
                        Thread.sleep(5000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            LOGGER.warning("Lost connection to Interaction Pi@" + ip.getHostAddress());
            InteractionPi pi = InteractionPiesContainer.getInstance().getInteractionPi(this.ip.getHostAddress());
            for (EscapeTheRoom escapeTheRoom : EscapeTheRoomContainer.getRunningInstances().values()) {
                for (Interaction interaction : escapeTheRoom.getAllInteractions()) {
                    if (pi.equals(interaction.getInteractionPi())) {
                        escapeTheRoom.failureHandler(interaction.getInteractionName());
                    }
                }
            }
        } catch (IllegalStateException | RemoteException | IllegalArgumentException exception) {
            LOGGER.severe(exception.getMessage());
        } finally {
            try {
                LOGGER.info(InteractionPiesContainer.getInstance().removeInteractionPi(this.ip.getHostAddress()));
            } catch (RemoteException remoteException) {
                LOGGER.severe(remoteException.getMessage());
            }
        }
    }
}
