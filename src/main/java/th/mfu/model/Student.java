package th.mfu.model;

import th.mfu.model.interfaces.*;

import java.util.HashMap;

import javax.persistence.*;

@Entity
@Table(name = "Student")
public class Student implements User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Password;
    private String Name;
    private String Role;
    private String Program;
    private String School;
    private String Department;

    // store as json (for flexible data length)
    private String jAdvisors;
    private String jSchedules;

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
        return Role;
    }
    public void setRole(String role) { // interfaces.User
        this.Role = role;
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

    // public String getjAdvisors() {
    //     return jAdvisors;
    // }
    // public void setjAdvisors(String jAdvisors) {
    //     this.jAdvisors = jAdvisors;
    // }
    // public String getjSchedules() {
    //     return jSchedules;
    // }
    // public void setjSchedules(String jSchedules) {
    //     this.jSchedules = jSchedules;
    // }    
}
