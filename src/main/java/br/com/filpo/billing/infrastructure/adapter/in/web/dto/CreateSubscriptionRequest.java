package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateSubscriptionRequest(
        @NotNull(message = "O ID do cliente é obrigatório") UUID customerId,

        @NotNull(message = "O ID do plano é obrigatório") UUID planId) {
}