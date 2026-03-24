package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.SubscriptionResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SubscriptionWebMapper {

    public SubscriptionResponse toResponse(Subscription subscription) {
        return new SubscriptionResponse(
                subscription.getId(),
                subscription.getCustomerId(),
                subscription.getPlanId(),
                subscription.getStatus().name(),
                subscription.getCurrentPeriodStart() != null ? subscription.getCurrentPeriodStart().toString() : null,
                subscription.getCurrentPeriodEnd() != null ? subscription.getCurrentPeriodEnd().toString() : null,
                subscription.isCancelAtPeriodEnd(),
                subscription.getCancelledAt() != null ? subscription.getCancelledAt().toString() : null,
                subscription.getCreatedAt() != null ? subscription.getCreatedAt().toString() : null);
    }

    public List<SubscriptionResponse> toResponseList(List<Subscription> subscriptions) {
        return subscriptions.stream()
                .map(this::toResponse)
                .toList();
    }
}