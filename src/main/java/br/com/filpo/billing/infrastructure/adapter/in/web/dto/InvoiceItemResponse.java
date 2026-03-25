package br.com.filpo.billing.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record InvoiceItemResponse(
        UUID id,
        String description,
        BigDecimal amount,
        int quantity) {
}