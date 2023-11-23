package th.mfu.model;

import javax.persistence.*;

@Entity
@Table(name = "Course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "name")
    private String name;

    public Long getID() {
        return ID;
    }
    public void setID(Long id) {
        this.ID = id;
    }

    // Getter and setter for 'name'
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

