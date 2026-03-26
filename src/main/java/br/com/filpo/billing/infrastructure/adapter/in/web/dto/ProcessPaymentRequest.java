package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record ProcessPaymentRequest(
        @NotNull(message = "O ID da fatura é obrigatório") UUID invoiceId) {
}