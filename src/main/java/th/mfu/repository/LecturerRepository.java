package th.mfu.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import th.mfu.model.Course;
import th.mfu.model.CourseSection;
import th.mfu.model.Lecturer;
import th.mfu.repository.CourseRepository;

public interface LecturerRepository extends CrudRepository<Lecturer, Long> {
    
    public List<Lecturer> findAll();
    public Lecturer findByID(Long iD);
    
    @Query("SELECT COUNT(l) > 0 FROM Lecturer l WHERE LOWER(l.Name) = LOWER(:name)")
    boolean existsByNameIgnoreCase(@Param("name") String name);
}