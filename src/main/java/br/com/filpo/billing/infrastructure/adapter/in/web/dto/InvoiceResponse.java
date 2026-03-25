package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record InvoiceResponse(
        UUID id,
        UUID customerId,
        UUID subscriptionId,
        String status,
        String currency,
        BigDecimal totalAmount,
        String dueDate,
        String paidAt,
        List<InvoiceItemResponse> items,
        String createdAt) {
}