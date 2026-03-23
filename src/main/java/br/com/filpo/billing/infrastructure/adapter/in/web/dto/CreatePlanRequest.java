package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import br.com.filpo.billing.domain.model.BillingCycle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CreatePlanRequest(
        @NotBlank(message = "O nome é obrigatório") String name,

        String description,

        @NotNull(message = "O preço é obrigatório") @Positive(message = "O preço deve ser maior que zero") BigDecimal price,

        @NotNull(message = "O ciclo de cobrança é obrigatório") BillingCycle billingCycle) {
}