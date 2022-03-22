package pt.ulisboa.tecnico.master_process.interactionpies.service;

import pt.ulisboa.tecnico.rmi.interactionpi.InteractionPiProxy;

import java.util.concurrent.Callable;

/**
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class PingCallable implements Callable<String> {

    private static InteractionPiProxy interactionPiProxy;

    public PingCallable(InteractionPiProxy proxy) {
        interactionPiProxy = proxy;
    }

    @Override
    public String call() throws Exception {
        return interactionPiProxy.ping();
    }
}
