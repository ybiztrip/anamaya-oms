package ai.anamaya.service.oms.core.repository;

import ai.anamaya.service.oms.core.entity.CompanyCreditInvoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyCreditInvoiceRepository extends JpaRepository<CompanyCreditInvoice, Long>, JpaSpecificationExecutor<CompanyCreditInvoice> {
    Boolean existsByCompanyIdAndDocNo(Long companyId, String docNo);
}
