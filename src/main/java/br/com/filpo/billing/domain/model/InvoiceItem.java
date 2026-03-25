package br.com.filpo.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class InvoiceItem {

    private final UUID id;
    private final String description;
    private final BigDecimal amount;
    private final int quantity;
    private final LocalDateTime createdAt;

    public static InvoiceItem createNew(String description, BigDecimal amount, int quantity) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("A descrição do item é obrigatória");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("O valor não pode ser negativo");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero");
        }

        return InvoiceItem.builder()
                .description(description)
                .amount(amount)
                .quantity(quantity)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public BigDecimal getTotal() {
        return amount.multiply(BigDecimal.valueOf(quantity));
    }
}