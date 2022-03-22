package pt.ulisboa.tecnico.master_process.databases.entities.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom.ParticipantEntity;

import javax.transaction.Transactional;
import java.util.List;

@Repository
@Transactional
public interface EscapeTheRoomParticipantRepository extends JpaRepository<ParticipantEntity, Integer> {
    @Query("SELECT part FROM ParticipantEntity part WHERE part.firstName = (:fName)")
    List<ParticipantEntity> findByFirstName(@Param("fName") String firstName);
    @Query("SELECT part FROM ParticipantEntity part WHERE part.firstName = (:fName) AND part.lastName = (:lName)")
    List<ParticipantEntity> findByFirstNameAndLastName(@Param("fName") String firstName, @Param("lName") String lastName);
    @Query("SELECT part FROM ParticipantEntity part WHERE part.email = (:email)")
    ParticipantEntity findByEmailAddress(@Param("email") String emailAddress);
    @Query("SELECT part FROM ParticipantEntity part WHERE part.age = (:age)")
    List<ParticipantEntity> findByAge(@Param("age") int age);
    @Query("SELECT part FROM ParticipantEntity part WHERE part.age BETWEEN (:lowerLimit) AND (:upperLimit)")
    List<ParticipantEntity> findByAgeInterval(@Param("lowerLimit") int lowerAgeLimit, @Param("upperLimit") int upperAgeLimit);
    @Query("SELECT part FROM ParticipantEntity part WHERE part.age > (:age)")
    List<ParticipantEntity> findParticipantsByAgeBiggerThan(@Param("age") int age);
    @Query("SELECT part FROM ParticipantEntity part WHERE part.age < (:age)")
    List<ParticipantEntity> findParticipantByAgeLowerThan(@Param("age") int age);
}
