package pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class EscapeTheRoomEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    protected Integer id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "group_id", nullable = false)
    protected GroupEntity group;

    @Column(name = "start_time", nullable = false)
    protected String startTime;

    @Column(name = "end_time", nullable = false)
    protected String endTime;

    @Column(name = "day_of_run", nullable = false)
    protected String dayOfRun;

    @Column(name = "score", nullable = false)
    protected Double score;

    public EscapeTheRoomEntity(GroupEntity group, String startTime, String endTime, String dayOfRun, Double score) {
        this.group = group;
        this.startTime = startTime;
        this.endTime = endTime;
        this.dayOfRun = dayOfRun;
        this.score = score;
    }

    protected EscapeTheRoomEntity() {}

    public Integer getId() {
        return id;
    }

    public GroupEntity getGroup() {
        return group;
    }

    public void setGroup(GroupEntity group) {
        this.group = group;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDayOfRun() {
        return dayOfRun;
    }

    public void setDayOfRun(String dayOfRun) {
        this.dayOfRun = dayOfRun;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }
}
