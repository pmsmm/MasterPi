package pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom;

import pt.ulisboa.tecnico.master_process.databases.entities.Gender;
import pt.ulisboa.tecnico.master_process.databases.entities.Participant;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;

@Entity
public class ParticipantEntity extends Participant {

    @ManyToMany(mappedBy = "participants")
    protected Set<GroupEntity> belongingGroups = new HashSet<>();

    public ParticipantEntity(String fName, String lName, String emailAddress, Gender gender, int age) {
        super(fName, lName, emailAddress, gender, age);
    }

    protected ParticipantEntity() {}

    public Set<GroupEntity> getBelongingGroups() {
        return belongingGroups;
    }

    public void setBelongingGroups(Set<GroupEntity> belongingGroups) {
        this.belongingGroups = belongingGroups;
    }
}
