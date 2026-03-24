package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.domain.model.SubscriptionStatus;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.SubscriptionJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class SubscriptionPersistenceMapper {

    public SubscriptionJpaEntity toEntity(Subscription subscription) {
        return SubscriptionJpaEntity.builder()
                .id(subscription.getId())
                .customerId(subscription.getCustomerId())
                .planId(subscription.getPlanId())
                .status(subscription.getStatus().name())
                .currentPeriodStart(subscription.getCurrentPeriodStart())
                .currentPeriodEnd(subscription.getCurrentPeriodEnd())
                .cancelAtPeriodEnd(subscription.isCancelAtPeriodEnd())
                .cancelledAt(subscription.getCancelledAt())
                .createdAt(subscription.getCreatedAt())
                .updatedAt(subscription.getUpdatedAt())
                .build();
    }

    public Subscription toDomain(SubscriptionJpaEntity entity) {
        return Subscription.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .planId(entity.getPlanId())
                .status(SubscriptionStatus.valueOf(entity.getStatus()))
                .currentPeriodStart(entity.getCurrentPeriodStart())
                .currentPeriodEnd(entity.getCurrentPeriodEnd())
                .cancelAtPeriodEnd(entity.isCancelAtPeriodEnd())
                .cancelledAt(entity.getCancelledAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}