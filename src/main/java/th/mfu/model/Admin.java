package th.mfu.model;

import th.mfu.model.interfaces.*;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Admin implements User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;
    private String Password;
    private String Name;
    private String Role = "ADMIN";

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
}
