package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Payment;
import br.com.filpo.billing.domain.model.PaymentEvent;
import br.com.filpo.billing.domain.model.PaymentEventType;
import br.com.filpo.billing.domain.model.PaymentStatus;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.PaymentEventJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.PaymentJpaEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class PaymentPersistenceMapper {

        public PaymentJpaEntity toEntity(Payment payment) {
                List<PaymentEventJpaEntity> eventEntities = payment.getEvents().stream()
                                .map(this::toEventEntity)
                                .toList();

                return PaymentJpaEntity.builder()
                                .id(payment.getId())
                                .invoiceId(payment.getInvoiceId())
                                .idempotencyKey(payment.getIdempotencyKey())
                                .status(payment.getStatus().name())
                                .amount(payment.getAmount())
                                .gateway(payment.getGateway())
                                .gatewayPaymentId(payment.getGatewayPaymentId())
                                .attemptNumber(payment.getAttemptNumber())
                                .nextRetryAt(payment.getNextRetryAt())
                                .events(new java.util.ArrayList<>(eventEntities))
                                .createdAt(payment.getCreatedAt())
                                .updatedAt(payment.getUpdatedAt())
                                .build();
        }

        public Payment toDomain(PaymentJpaEntity entity) {
                List<PaymentEvent> events = entity.getEvents().stream()
                                .map(e -> toEventDomain(e, entity.getId()))
                                .toList();

                return Payment.builder()
                                .id(entity.getId())
                                .invoiceId(entity.getInvoiceId())
                                .idempotencyKey(entity.getIdempotencyKey())
                                .status(PaymentStatus.valueOf(entity.getStatus()))
                                .amount(entity.getAmount())
                                .gateway(entity.getGateway())
                                .gatewayPaymentId(entity.getGatewayPaymentId())
                                .attemptNumber(entity.getAttemptNumber())
                                .nextRetryAt(entity.getNextRetryAt())
                                .events(new java.util.ArrayList<>(events))
                                .createdAt(entity.getCreatedAt())
                                .updatedAt(entity.getUpdatedAt())
                                .build();
        }

        private PaymentEventJpaEntity toEventEntity(PaymentEvent event) {
                return PaymentEventJpaEntity.builder()
                                .id(event.getId())
                                .type(event.getType().name())
                                .description(event.getDescription())
                                .metadata(event.getMetadata())
                                .createdAt(event.getCreatedAt())
                                .build();
        }

        private PaymentEvent toEventDomain(PaymentEventJpaEntity entity, UUID paymentId) {
                return PaymentEvent.builder()
                                .id(entity.getId())
                                .paymentId(paymentId)
                                .type(PaymentEventType.valueOf(entity.getType()))
                                .description(entity.getDescription())
                                .metadata(entity.getMetadata())
                                .createdAt(entity.getCreatedAt())
                                .build();
        }
}