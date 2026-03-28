package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ChangePlanRequest(
        @NotNull(message = "O ID do novo plano é obrigatório") UUID newPlanId) {
}