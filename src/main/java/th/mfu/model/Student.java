package th.mfu.model;

import th.mfu.model.interfaces.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Student")
public class Student implements User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Password;
    private String Name;
    private String Program;
    private String School;
    private String Department;

    @ManyToOne
    @JoinColumn(name = "LECTURER_ID")
    private Lecturer lecturer;
    
    public Long getID() { // interfaces.User
        return ID;
    }
    public void setID(Long iD) { // interfaces.User
        ID = iD;
    }
    public String getPassword() { // interfaces.User
        return Password;
    }
    public void setPassword(String password) { // interfaces.User
        Password = password;
    }
    public String getName() { // interfaces.User
        return Name;
    }
    public void setName(String name) { // interfaces.User
        Name = name;
    }
    public String getRole() { // interfaces.User
        return "STUDENT";
    }
    public String getProgram() {
        return Program;
    }
    public void setProgram(String program) {
        Program = program;
    }
    public String getSchool() {
        return School;
    }
    public void setSchool(String school) {
        School = school;
    }
    public String getDepartment() {
        return Department;
    }
    public void setDepartment(String deparment) {
        Department = deparment;
    }

    
    public Lecturer getLecturer() {
        return lecturer;
    }
    public void setLecturer(Lecturer lecturer) {
        this.lecturer = lecturer;
    }
}
