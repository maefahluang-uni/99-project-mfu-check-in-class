package th.mfu.model;

import java.util.*;
import javax.persistence.*;

@Entity
@Table(name = "Semester")
public class Semester {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    
    @Column(name = "ACADEMIC_YEAR")
    private Long Year;
    @Column(name = "TERM")
    private Long Term;
    @Column(name = "START_DATE")
    private String DateStart;
    @Column(name = "FINISH_DATE")
    private String DateFinish;

    public Long getID() {
        return ID;
    }
    public void setID(Long id) {
        this.ID = id;
    }
    public Long getYear() {
        return Year;
    }
    public void setYear(Long year) {
        Year = year;
    }
    public Long getTerm() {
        return Term;
    }
    public void setTerm(Long term) {
        Term = term;
    }
    public String getDateStart() {
        return DateStart;
    }
    public void setDateStart(String dateStart) {
        DateStart = dateStart;
    }
    public String getDateFinish() {
        return DateFinish;
    }
    public void setDateFinish(String dateFinish) {
        DateFinish = dateFinish;
    }

}
