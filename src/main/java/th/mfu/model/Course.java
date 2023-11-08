package th.mfu.model;

import javax.persistence.*;

@Entity
@Table(name = "Course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Name;

    public Long getID() {
        return ID;
    }
    public void setID(Long id) {
        this.ID = id;
    };
    public String getName() {
        return Name;
    }
    public void setName(String name) {
        Name = name;
    }
}
