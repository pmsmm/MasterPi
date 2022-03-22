package pt.ulisboa.tecnico.master_process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoom;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant;
import pt.ulisboa.tecnico.master_process.interactionpies.service.InteractionPiesContainer;
import pt.ulisboa.tecnico.master_process.interactions.MorseCodeInteraction;

import java.util.Locale;

public class StartEscapeTheRoomTests {

    private EscapeTheRoom instance;

    @BeforeEach
    public void setEscapeTheRoomInstance() throws Exception {
        this.instance = EscapeTheRoomContainer.getInstance().createNewEscapeTheRoom();
        this.instance.addParticipant(new Participant("Pedro", "Maria", "teste", 23, "MALE"));
        this.instance.addParticipant(new Participant("Pedro", "Miguel", "teste", 24, "MALE"));
        this.instance.setGroupName("Test Group");
        InteractionPiesContainer.getInstance().addInteractionPi("127.0.0.1", 8086);
    }

    @Test
    public void startEscapeTheRoom() {
        System.out.println(instance.startGame());
    }

}
