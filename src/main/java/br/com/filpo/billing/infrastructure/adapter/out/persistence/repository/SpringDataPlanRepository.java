package br.com.filpo.billing.infrastructure.adapter.out.persistence.repository;

import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.PlanJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpringDataPlanRepository extends JpaRepository<PlanJpaEntity, UUID> {
}