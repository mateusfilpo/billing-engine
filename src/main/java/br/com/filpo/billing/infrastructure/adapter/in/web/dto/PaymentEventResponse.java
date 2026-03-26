package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import java.util.UUID;

public record PaymentEventResponse(
        UUID id,
        String type,
        String description,
        String metadata,
        String createdAt) {
}