package br.com.filpo.billing.infrastructure.adapter.out.persistence.repository;

import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.SubscriptionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface SpringDataSubscriptionRepository extends JpaRepository<SubscriptionJpaEntity, UUID> {
    List<SubscriptionJpaEntity> findByCustomerId(UUID customerId);

    List<SubscriptionJpaEntity> findByStatusAndCurrentPeriodEnd(String status, LocalDate date);
}