package pt.ulisboa.tecnico.master_process;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom.EscapeTheRoomEntity;
import pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom.GroupEntity;
import pt.ulisboa.tecnico.master_process.databases.entities.interfaces.EscapeTheRoomRepository;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoomContainer;
import pt.ulisboa.tecnico.master_process.escapetheroom.service.participants.Participant;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
public class PersistEscapeTheRoomTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private EscapeTheRoomRepository escapeTheRoomRepository;

    private pt.ulisboa.tecnico.master_process.escapetheroom.service.EscapeTheRoom instance;
    private final String expectedStartAndEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis()));

    @BeforeEach
    public void setEscapeTheRoomInstance() {
        this.instance = EscapeTheRoomContainer.getInstance().createNewEscapeTheRoom();
        Participant participant1 = new Participant("Pedro", "Maria", "pedro8745@gmail.com", 23, "MALE");
        Participant participant2 = new Participant("Joao", "Maria", "teste@gmail.com", 16, "MALE");
        this.instance.addParticipant(participant1);
        this.instance.addParticipant(participant2);
        this.instance.setGroupName("The Scientists");
        this.instance.setSCORE(1000.0);
        this.instance.setStartDate(expectedStartAndEndDate);
        this.instance.setEndDate(expectedStartAndEndDate);
    }

    @Test
    public void persistEscapeTheRoomInstanceInDatabase() {
        EscapeTheRoomEntity entity = new EscapeTheRoomEntity(new GroupEntity(instance.getGroupName()), instance.getStartDate(),
                instance.getEndDate(), instance.getStartDate().substring(0, 10), instance.getScore());

        entityManager.persist(entity);

        EscapeTheRoomEntity fetchedFromDatabase = escapeTheRoomRepository.findAll().get(0);

        assertEquals(1000.0, fetchedFromDatabase.getScore());
        assertEquals(expectedStartAndEndDate, fetchedFromDatabase.getStartTime());
        assertEquals(expectedStartAndEndDate, fetchedFromDatabase.getEndTime());
        assertEquals(2, fetchedFromDatabase.getGroup().getId());
        assertEquals(expectedStartAndEndDate.substring(0, 10), fetchedFromDatabase.getDayOfRun());

    }

}
