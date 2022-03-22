package pt.ulisboa.tecnico.master_process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoom;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EscapeTheRoomParticipantsTests {

    private EscapeTheRoom instance;

    @BeforeEach
    public void setEscapeTheRoomInstance() throws Exception {
        this.instance = EscapeTheRoomContainer.getInstance().createNewEscapeTheRoom();
        Participant participant1 = new Participant("Pedro", "Maria", "pedro8745@gmail.com", 23, "MALE");
        Participant participant2 = new Participant("Joao", "Maria", "teste@gmail.com", 16, "MALE");
        this.instance.addParticipant(participant1);
        this.instance.addParticipant(participant2);
    }

    @Test
    public void assertNumberOfParticipantsEqualsTwo() {
        assertEquals(2, instance.getAllParticipants().size());
    }

}
