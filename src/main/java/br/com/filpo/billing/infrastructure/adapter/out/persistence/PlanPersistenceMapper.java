package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.BillingCycle;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.model.PlanStatus;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.PlanJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PlanPersistenceMapper {

    public PlanJpaEntity toEntity(Plan plan) {
        return PlanJpaEntity.builder()
                .id(plan.getId())
                .name(plan.getName())
                .description(plan.getDescription())
                .price(plan.getPrice())
                .billingCycle(plan.getBillingCycle().name())
                .status(plan.getStatus().name())
                .createdAt(plan.getCreatedAt())
                .build();
    }

    public Plan toDomain(PlanJpaEntity entity) {
        return Plan.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .price(entity.getPrice())
                .billingCycle(BillingCycle.valueOf(entity.getBillingCycle()))
                .status(PlanStatus.valueOf(entity.getStatus()))
                .createdAt(entity.getCreatedAt())
                .build();
    }
}