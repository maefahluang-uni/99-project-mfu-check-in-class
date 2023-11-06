package th.mfu.repository;

import th.mfu.model.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    public Student findByID(Long iD);
}
