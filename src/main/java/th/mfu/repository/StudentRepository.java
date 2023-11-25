package th.mfu.repository;

import th.mfu.model.*;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    public Student findByID(Long id);
    List<Student> findByLecturer_ID(Long lecturerId);
}
