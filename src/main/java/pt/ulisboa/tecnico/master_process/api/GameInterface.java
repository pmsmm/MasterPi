package pt.ulisboa.tecnico.master_process.api;

import pt.ulisboa.tecnico.master_process.escapetheroom.service.GameState;

/**
 * The interface that all the games that will be developed to run on this infrastructure will implement. Contains the
 * most basic methods that any game must implement like the start, stop and pause game methods.
 */
public interface GameInterface {
    String startGame() throws IllegalStateException;
    String stopGame(GameState state) throws IllegalStateException;
    String pauseGame() throws IllegalStateException;
}
