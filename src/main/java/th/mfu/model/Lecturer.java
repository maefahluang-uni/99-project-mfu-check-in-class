package th.mfu.model;

import th.mfu.model.interfaces.*;
import javax.persistence.*;
import java.util.*;

@Entity
@Table(name = "Lecturer")
public class Lecturer implements User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Password;
    private String Name;
    private String Department;
    private String School;
    
    // @OneToMany(mappedBy = "lecturer")
    // private List<Student> students = new ArrayList<>();
    // public List<Student> getStudents() {
    //     return students;
    // }
    // public void setStudents(List<Student> students) {
    //     this.students = students;
    // }

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
        return "LECTURER";
    }
    public String getDepartment() {
        return Department;
    }
    public void setDepartment(String department) {
        Department = department;
    }
    public String getSchool() {
        return School;
    }
    public void setSchool(String school) {
        School = school;
    }
}
