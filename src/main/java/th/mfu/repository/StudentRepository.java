package th.mfu.repository;

import th.mfu.model.*;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface StudentRepository extends CrudRepository<Student, Long> {
    public Student findByID(Long id);
    List<Student> findByLecturer_ID(Long lecturerId);
}
