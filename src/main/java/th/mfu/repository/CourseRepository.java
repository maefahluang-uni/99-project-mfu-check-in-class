package th.mfu.repository;

import org.springframework.data.repository.CrudRepository;
import th.mfu.model.Course;
import th.mfu.model.CourseSection;
import th.mfu.repository.CourseRepository;

public interface CourseRepository extends CrudRepository<Course, Long> {
    boolean existsByNameIgnoreCase(String name);
    public Course findByID(Long id);
}
