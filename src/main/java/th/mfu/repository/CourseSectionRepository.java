package th.mfu.repository;

import java.util.*;
import th.mfu.model.*;

import org.springframework.data.repository.CrudRepository;

public interface CourseSectionRepository extends CrudRepository<CourseSection, Long> {
    public CourseSection findByID(Long id);
    public List<CourseSection> findByCourseID(Long COURSE_ID); // view all <Section> in this course.
    public List<CourseSection> findByStudentID(Long STUDENT_ID); // view all <Section> by <Student> study schedule.
    public List<CourseSection> findByLecturerID(Long LECTURER_ID); // view all <Section> by <Lecturer> teach schedule.

    public List<CourseSection> findByStudentIDAndSemesterID(Long STUDENT_ID, Long SEMESTER_ID); // view all <Section> by <Student> study schedule with specific <Semester>.
    public List<CourseSection> findByLecturerIDAndSemesterID(Long LECTURER_ID, Long SEMESTER_ID); // view all <Section> by <Student> study schedule with specific <Semester>.
}
