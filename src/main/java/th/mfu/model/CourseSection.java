package th.mfu.model;

import java.io.*;
import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "Course_Section")
public class CourseSection implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "COURSE_ID")
    private Course course;
    
    @ManyToOne
    @JoinTable(
        name = "COURSE_SECTION_ON_SEMESTER", joinColumns = @JoinColumn(name = "SECTION_ID"),
        inverseJoinColumns = @JoinColumn(name = "SEMESTER_ID"))
    public Semester semester;

    @ManyToMany
    @JoinTable(
        name = "STUDENT_SCHEDULE", joinColumns = @JoinColumn(name = "SECTION_ID"),
        inverseJoinColumns = @JoinColumn(name = "STUDENT_ID"))
    public List<Student> student;
    
    @ManyToMany
    @JoinTable(
        name = "LECTURER_SCHEDULE", joinColumns = @JoinColumn(name = "SECTION_ID"),
        inverseJoinColumns = @JoinColumn(name = "LECTURER_ID"))
    public List<Lecturer> lecturer;

    private String section;
    private String location;
    private String period;

    public Long getID() {
        return ID;
    }
    public void setID(Long id) {
        this.ID = id;
    }
    public Course getCourse() {
        return course;
    }
    public void setCourse(Course course) {
        this.course = course;
    }
    public String getSection() {
        return section;
    }
    public void setSection(String section) {
        this.section = section;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public String getPeriod() {
        return period;
    }
    public void setPeriod(String period) {
        this.period = period;
    }
}
