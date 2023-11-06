package th.mfu.repository;

import java.util.List;
import th.mfu.model.*;

import org.springframework.data.repository.CrudRepository;

public interface LecturerRepository extends CrudRepository<Lecturer, Long> {
    public List<Lecturer> findAll();
    public Lecturer findByID(Long iD);
}