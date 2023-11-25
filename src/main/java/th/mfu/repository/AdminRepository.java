package th.mfu.repository;

import th.mfu.model.*;
import org.springframework.data.repository.CrudRepository;

public interface AdminRepository extends CrudRepository<Admin, Long> {
    public Admin findByID(Long iD);
}