package br.com.filpo.billing.domain.model;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PaymentEvent {
    private final UUID id;
    private final UUID paymentId;
    private final PaymentEventType type;
    private final String description;
    private final String metadata;
    private final LocalDateTime createdAt;

    public static PaymentEvent create(UUID paymentId, PaymentEventType type, String description, String metadata) {
        return PaymentEvent.builder()
                .paymentId(paymentId)
                .type(type)
                .description(description)
                .metadata(metadata)
                .createdAt(LocalDateTime.now())
                .build();
    }
}