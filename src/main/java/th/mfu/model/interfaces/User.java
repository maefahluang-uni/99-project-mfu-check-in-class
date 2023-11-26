package th.mfu.model.interfaces;

public interface User {
    Long getID();
    void setID(Long __ID__);
    String getPassword();
    void setPassword(String __HASH_PASSWORD__);
    String getName();
    void setName(String __NAME__);
    String getRole();
}