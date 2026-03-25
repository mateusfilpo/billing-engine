package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record GenerateInvoiceRequest(
        @NotNull(message = "O ID da assinatura é obrigatório") UUID subscriptionId) {
}