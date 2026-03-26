package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        UUID invoiceId,
        String idempotencyKey,
        String status,
        BigDecimal amount,
        String gateway,
        String gatewayPaymentId,
        int attemptNumber,
        String nextRetryAt,
        List<PaymentEventResponse> events,
        String createdAt) {
}