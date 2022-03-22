package pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class GroupEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Integer id;

    @Column(name = "name", length = 64, unique = true, nullable = false)
    protected String groupName;

    @OneToMany(mappedBy = "group", cascade = CascadeType.ALL)
    protected List<EscapeTheRoomEntity> playedGames = new ArrayList<>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "group_participants", joinColumns = @JoinColumn(name = "group_id"), inverseJoinColumns = @JoinColumn(name = "participant_id"))
    protected Set<ParticipantEntity> participants = new HashSet<>();

    protected GroupEntity() {}

    public GroupEntity(String groupName) {
        this.groupName = groupName;
    }

    public Integer getId() {
        return id;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<EscapeTheRoomEntity> getPlayedGames() {
        return playedGames;
    }

    public void setPlayedGames(List<EscapeTheRoomEntity> playedGames) {
        this.playedGames = playedGames;
    }
}
