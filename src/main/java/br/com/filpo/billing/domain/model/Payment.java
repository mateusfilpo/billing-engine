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
public class Payment {

    private final UUID id;
    private final UUID invoiceId;
    private final String idempotencyKey;
    private PaymentStatus status;
    private final BigDecimal amount;
    private final String gateway;
    private String gatewayPaymentId;
    private int attemptNumber;
    private LocalDateTime nextRetryAt;

    @Builder.Default
    private final List<PaymentEvent> events = new ArrayList<>();

    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Payment createNew(UUID invoiceId, BigDecimal amount, String gateway) {
        String idempotencyKey = "PAY-" + invoiceId + "-1";

        Payment payment = Payment.builder()
                .invoiceId(invoiceId)
                .idempotencyKey(idempotencyKey)
                .status(PaymentStatus.PENDING)
                .amount(amount)
                .gateway(gateway)
                .attemptNumber(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        payment.addEvent(PaymentEventType.INITIATED, "Pagamento iniciado no sistema", null);
        return payment;
    }

    public void markAsProcessing() {
        if (this.status != PaymentStatus.PENDING && this.status != PaymentStatus.FAILED) {
            throw new IllegalStateException("Apenas pagamentos pendentes ou falhos podem ser processados");
        }
        this.status = PaymentStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
        addEvent(PaymentEventType.PROCESSING, "Enviando requisição para o gateway " + gateway, null);
    }

    public void markAsPaid(String gatewayPaymentId) {
        if (this.status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Pagamento precisa estar em processamento para ser marcado como pago");
        }
        this.status = PaymentStatus.PAID;
        this.gatewayPaymentId = gatewayPaymentId;
        this.updatedAt = LocalDateTime.now();
        addEvent(PaymentEventType.PAID, "Pagamento confirmado pelo gateway",
                "{\"gateway_id\": \"" + gatewayPaymentId + "\"}");
    }

    public void markAsFailed(String reason) {
        if (this.status != PaymentStatus.PROCESSING) {
            throw new IllegalStateException("Pagamento precisa estar em processamento para falhar");
        }
        this.status = PaymentStatus.FAILED;
        this.attemptNumber += 1;
        this.nextRetryAt = LocalDateTime.now().plusHours(24);
        this.updatedAt = LocalDateTime.now();

        addEvent(PaymentEventType.FAILED, "Falha no gateway: " + reason, null);
        addEvent(PaymentEventType.RETRY_SCHEDULED, "Nova tentativa agendada",
                "{\"next_retry_at\": \"" + this.nextRetryAt + "\"}");
    }

    private void addEvent(PaymentEventType type, String description, String metadata) {
        this.events.add(PaymentEvent.create(this.id, type, description, metadata));
    }
}