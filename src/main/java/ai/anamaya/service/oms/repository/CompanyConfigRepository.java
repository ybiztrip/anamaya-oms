package ai.anamaya.service.oms.repository;

import ai.anamaya.service.oms.entity.CompanyConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface CompanyConfigRepository extends JpaRepository<CompanyConfig, Long> {
    Optional<CompanyConfig> findByCompanyIdAndCode(Long companyId, String code);
    List<CompanyConfig> findAllByCompanyId(Long companyId);
}
