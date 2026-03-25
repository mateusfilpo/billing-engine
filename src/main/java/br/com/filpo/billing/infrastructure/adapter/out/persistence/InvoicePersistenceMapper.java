package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.model.InvoiceItem;
import br.com.filpo.billing.domain.model.InvoiceStatus;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.InvoiceItemJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.InvoiceJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoicePersistenceMapper {

    public InvoiceJpaEntity toEntity(Invoice invoice) {
        List<InvoiceItemJpaEntity> itemEntities = invoice.getItems().stream()
                .map(this::toItemEntity)
                .toList();

        return InvoiceJpaEntity.builder()
                .id(invoice.getId())
                .customerId(invoice.getCustomerId())
                .subscriptionId(invoice.getSubscriptionId())
                .status(invoice.getStatus().name())
                .currency(invoice.getCurrency())
                .totalAmount(invoice.getTotalAmount())
                .dueDate(invoice.getDueDate())
                .paidAt(invoice.getPaidAt())
                .items(new java.util.ArrayList<>(itemEntities)) // JPA prefere coleções mutáveis
                .createdAt(invoice.getCreatedAt())
                .updatedAt(invoice.getUpdatedAt())
                .build();
    }

    public Invoice toDomain(InvoiceJpaEntity entity) {
        List<InvoiceItem> items = entity.getItems().stream()
                .map(this::toItemDomain)
                .toList();

        return Invoice.builder()
                .id(entity.getId())
                .customerId(entity.getCustomerId())
                .subscriptionId(entity.getSubscriptionId())
                .status(InvoiceStatus.valueOf(entity.getStatus()))
                .currency(entity.getCurrency())
                .totalAmount(entity.getTotalAmount())
                .dueDate(entity.getDueDate())
                .paidAt(entity.getPaidAt())
                .items(new java.util.ArrayList<>(items))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private InvoiceItemJpaEntity toItemEntity(InvoiceItem item) {
        return InvoiceItemJpaEntity.builder()
                .id(item.getId())
                .description(item.getDescription())
                .amount(item.getAmount())
                .quantity(item.getQuantity())
                .createdAt(item.getCreatedAt())
                .build();
    }

    private InvoiceItem toItemDomain(InvoiceItemJpaEntity entity) {
        return InvoiceItem.builder()
                .id(entity.getId())
                .description(entity.getDescription())
                .amount(entity.getAmount())
                .quantity(entity.getQuantity())
                .createdAt(entity.getCreatedAt())
                .build();
    }
}