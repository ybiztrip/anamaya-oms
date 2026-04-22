package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    List<Role> findAllByIsSuperAdmin(String isSuperAdmin);
}
