package br.com.filpo.billing.infrastructure.adapter.out.persistence.repository;

import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.PaymentJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SpringDataPaymentRepository extends JpaRepository<PaymentJpaEntity, UUID> {
    List<PaymentJpaEntity> findByInvoiceId(UUID invoiceId);

    List<PaymentJpaEntity> findByStatusAndNextRetryAtLessThanEqual(String status, LocalDateTime now);
}