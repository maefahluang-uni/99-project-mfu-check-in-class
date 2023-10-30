package th.mfu.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Password;
    private String Name;
    private String Program;
    private String School;
    private String Deparment;
    private Long AdvisorID; // Lecturer.ID

    public String getProgram() {
        return Program;
    }
    public void setProgram(String program) {
        Program = program;
    }
    public Long getAdvisorID() {
        return AdvisorID;
    }
    public void setAdvisorID(Long advisorID) {
        AdvisorID = advisorID;
    }
    public Long getID() {
        return ID;
    }
    public void setID(Long iD) {
        ID = iD;
    }
    public String getPassword() {
        return Password;
    }
    public void setPassword(String password) {
        Password = password;
    }
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
    public String getDeparment() {
        return Deparment;
    }
    public void setDeparment(String deparment) {
        Deparment = deparment;
    }
    public String getSchool() {
        return School;
    }
    public void setSchool(String school) {
        School = school;
    }
}
