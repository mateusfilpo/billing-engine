package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.model.InvoiceItem;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.InvoiceItemResponse;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.InvoiceResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InvoiceWebMapper {

    public InvoiceResponse toResponse(Invoice invoice) {
        List<InvoiceItemResponse> itemResponses = invoice.getItems().stream()
                .map(this::toItemResponse)
                .toList();

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getCustomerId(),
                invoice.getSubscriptionId(),
                invoice.getStatus().name(),
                invoice.getCurrency(),
                invoice.getTotalAmount(),
                invoice.getDueDate() != null ? invoice.getDueDate().toString() : null,
                invoice.getPaidAt() != null ? invoice.getPaidAt().toString() : null,
                itemResponses,
                invoice.getCreatedAt() != null ? invoice.getCreatedAt().toString() : null);
    }

    public List<InvoiceResponse> toResponseList(List<Invoice> invoices) {
        return invoices.stream()
                .map(this::toResponse)
                .toList();
    }

    private InvoiceItemResponse toItemResponse(InvoiceItem item) {
        return new InvoiceItemResponse(
                item.getId(),
                item.getDescription(),
                item.getAmount(),
                item.getQuantity());
    }
}