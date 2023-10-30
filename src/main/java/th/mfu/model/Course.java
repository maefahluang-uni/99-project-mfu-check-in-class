package th.mfu.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Name;
    private Section[] Sections; // Entity Section.java
    
    public Course(String courseName, Section[] courseSections) {
        this.Name = courseName;
        this.Sections = courseSections;
    }

    public Long getID() {
        return ID;
    }
    public void setID(Long iD) {
        ID = iD;
    };
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public Section[] getSections() {
        return Sections;
    }
    public void setSections(Section[] sections) {
        Sections = sections;
    }
}
