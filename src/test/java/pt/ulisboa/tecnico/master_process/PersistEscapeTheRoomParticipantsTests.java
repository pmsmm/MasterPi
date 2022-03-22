package pt.ulisboa.tecnico.master_process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom.ParticipantEntity;
import pt.ulisboa.tecnico.master_process.databases.entities.interfaces.EscapeTheRoomParticipantRepository;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoom;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class PersistEscapeTheRoomParticipantsTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EscapeTheRoomParticipantRepository escapeTheRoomParticipantRepository;

    private EscapeTheRoom instance;

    @BeforeEach
    public void setEscapeTheRoomInstance() {
        this.instance = EscapeTheRoomContainer.getInstance().createNewEscapeTheRoom();
        pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant participant1 = new pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant("Pedro", "Maria", "pedro8745@gmail.com", 23, "MALE");
        pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant participant2 = new pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant("Joao", "Maria", "teste@gmail.com", 16, "MALE");
        this.instance.addParticipant(participant1);
        this.instance.addParticipant(participant2);
    }

    @Test
    public void commitParticipantsToDatabase() {
        ArrayList<ParticipantEntity> arrayList = new ArrayList<>();
        for (pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant participant : instance.getAllParticipants()) {
            arrayList.add(new ParticipantEntity(participant.getFirstName(), participant.getLastName(), participant.getEmailAddress(),
                    participant.getGender(), participant.getAge()));
        }

        for (ParticipantEntity participant : arrayList) {
            entityManager.persist(participant);
        }

        ArrayList<ParticipantEntity> databaseResult = new ArrayList<>(escapeTheRoomParticipantRepository.findAll());

        assertEquals(2, databaseResult.size());
        assertEquals("Pedro", databaseResult.get(0).getFirstName());
        assertEquals("Maria", databaseResult.get(0).getLastName());
        assertEquals(23, databaseResult.get(0).getAge());
        assertEquals("pedro8745@gmail.com", databaseResult.get(0).getEmail());

    }

}
