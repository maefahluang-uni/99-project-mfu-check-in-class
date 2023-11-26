package th.mfu.repository;

import th.mfu.model.*;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface StudentRepository extends CrudRepository<Student, Long> {
    public Student findByID(Long id);
    List<Student> findByLecturer_ID(Long lecturerId);
    @Query("SELECT COUNT(l) > 0 FROM Student l WHERE LOWER(l.Name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
}
