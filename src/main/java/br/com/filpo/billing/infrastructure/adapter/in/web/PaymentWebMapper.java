package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.model.Payment;
import br.com.filpo.billing.domain.model.PaymentEvent;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.PaymentEventResponse;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.PaymentResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PaymentWebMapper {

    public PaymentResponse toResponse(Payment payment) {
        List<PaymentEventResponse> eventResponses = payment.getEvents().stream()
                .map(this::toEventResponse)
                .toList();

        return new PaymentResponse(
                payment.getId(),
                payment.getInvoiceId(),
                payment.getIdempotencyKey(),
                payment.getStatus().name(),
                payment.getAmount(),
                payment.getGateway(),
                payment.getGatewayPaymentId(),
                payment.getAttemptNumber(),
                payment.getNextRetryAt() != null ? payment.getNextRetryAt().toString() : null,
                eventResponses,
                payment.getCreatedAt() != null ? payment.getCreatedAt().toString() : null);
    }

    public List<PaymentResponse> toResponseList(List<Payment> payments) {
        return payments.stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentEventResponse toEventResponse(PaymentEvent event) {
        return new PaymentEventResponse(
                event.getId(),
                event.getType().name(),
                event.getDescription(),
                event.getMetadata(),
                event.getCreatedAt() != null ? event.getCreatedAt().toString() : null);
    }
}