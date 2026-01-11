package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @EntityGraph(attributePaths = {"userRoles", "userRoles.role"})

    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNo(String phoneNo);

    boolean existsByEmail(String email);

    @Query("""
        SELECT DISTINCT u
        FROM User u
        JOIN u.userRoles ur
        JOIN ur.role r
        WHERE u.companyId = :companyId
         AND r.code = "APPROVER"
    """)
    List<User> findUserApprover(@Param("companyId") Long companyId);
}
