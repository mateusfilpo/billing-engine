package br.com.filpo.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Invoice {

    private final UUID id;
    private final UUID customerId;
    private final UUID subscriptionId;
    private InvoiceStatus status;
    private final String currency;
    private BigDecimal totalAmount;
    private final LocalDateTime dueDate;
    private LocalDateTime paidAt;

    @Builder.Default
    private final List<InvoiceItem> items = new ArrayList<>();

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Invoice createFromSubscription(UUID customerId, UUID subscriptionId, LocalDateTime dueDate,
            List<InvoiceItem> initialItems) {
        Invoice invoice = Invoice.builder()
                .customerId(customerId)
                .subscriptionId(subscriptionId)
                .status(InvoiceStatus.DRAFT)
                .currency("BRL")
                .dueDate(dueDate)
                .items(initialItems != null ? new ArrayList<>(initialItems) : new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        invoice.recalculateTotal();
        return invoice;
    }

    public void open() {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Apenas faturas em rascunho (DRAFT) podem ser abertas");
        }
        this.status = InvoiceStatus.OPEN;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsPaid() {
        if (this.status != InvoiceStatus.OPEN) {
            throw new IllegalStateException("Apenas faturas abertas (OPEN) podem ser marcadas como pagas");
        }
        this.status = InvoiceStatus.PAID;
        this.paidAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void voidInvoice() {
        if (this.status == InvoiceStatus.PAID) {
            throw new IllegalStateException("Faturas pagas não podem ser canceladas/void");
        }
        this.status = InvoiceStatus.VOID;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsUncollectible() {
        if (this.status != InvoiceStatus.OPEN) {
            throw new IllegalStateException("Apenas faturas abertas podem ser marcadas como incobráveis");
        }
        this.status = InvoiceStatus.UNCOLLECTIBLE;
        this.updatedAt = LocalDateTime.now();
    }

    public void addItem(InvoiceItem item) {
        if (this.status != InvoiceStatus.DRAFT) {
            throw new IllegalStateException("Itens só podem ser adicionados a faturas em rascunho (DRAFT)");
        }
        this.items.add(item);
        this.recalculateTotal();
        this.updatedAt = LocalDateTime.now();
    }

    public void recalculateTotal() {
        this.totalAmount = this.items.stream()
                .map(InvoiceItem::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}