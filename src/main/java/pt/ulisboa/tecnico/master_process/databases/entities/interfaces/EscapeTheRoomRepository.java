package pt.ulisboa.tecnico.master_process.databases.entities.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pt.ulisboa.tecnico.master_process.databases.entities.escapetheroom.EscapeTheRoomEntity;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface EscapeTheRoomRepository extends JpaRepository<EscapeTheRoomEntity, Integer> {
}
