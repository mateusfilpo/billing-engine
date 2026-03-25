package br.com.filpo.billing.infrastructure.adapter.out.persistence.repository;

import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.InvoiceJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SpringDataInvoiceRepository extends JpaRepository<InvoiceJpaEntity, UUID> {
    List<InvoiceJpaEntity> findByCustomerId(UUID customerId);
}