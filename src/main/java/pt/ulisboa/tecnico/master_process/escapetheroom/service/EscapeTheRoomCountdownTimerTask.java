package pt.ulisboa.tecnico.master_process.escapetheroom.service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.TimerTask;
import java.util.logging.Logger;

/**
 * TimerTask that counts the elapsed time since the beginning of the game and terminates the game when the time reaches
 * its limit.
 *
 * @author Pedro Maria
 * @version 1.0
 * @since 1.0
 */

public class EscapeTheRoomCountdownTimerTask extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(EscapeTheRoomCountdownTimerTask.class.getName());
    private final EscapeTheRoom instance;
    private int durationInMinutes;

    public EscapeTheRoomCountdownTimerTask(EscapeTheRoom instance, int durationInMinutes) {
        this.instance = instance;
        this.durationInMinutes = durationInMinutes;
    }

    @Override
    public void run() {
        if (instance.getGameState().equals(GameState.RUNNING)) {
            LOGGER.info("Remaining Time: " + LocalTime.MIN.plusSeconds(
                    (instance.getEscapeTheRoomDurationInSeconds() - instance.getELAPSED_TIME())
            ).format(DateTimeFormatter.ofPattern("H:mm:ss")));
            instance.setELAPSED_TIME(instance.getELAPSED_TIME() + 1);
            if (--durationInMinutes <= 0) {
                LOGGER.info("Time Limit Reached. Terminating Escape The Room");
                instance.stopGame(GameState.NOT_FINISHED);
            }
        }
    }
}
