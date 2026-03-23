package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record PlanResponse(
        UUID id,
        String name,
        String description,
        BigDecimal price,
        String billingCycle,
        String status,
        String createdAt) {
}