package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import java.util.UUID;

public record SubscriptionResponse(
        UUID id,
        UUID customerId,
        UUID planId,
        String status,
        String currentPeriodStart,
        String currentPeriodEnd,
        boolean cancelAtPeriodEnd,
        String cancelledAt,
        String createdAt) {
}