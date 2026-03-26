package br.com.filpo.billing.infrastructure.adapter.out.gateway;

import br.com.filpo.billing.domain.model.PaymentGatewayResponse;
import br.com.filpo.billing.domain.port.out.PaymentGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component
@Profile("default")
public class FakePaymentGateway implements PaymentGateway {

    private final Random random = new Random();

    @Override
    public PaymentGatewayResponse charge(BigDecimal amount, String idempotencyKey) {
        log.info("[FAKE GATEWAY] Cobrando R$ {} (Chave Idempotência: {})...", amount, idempotencyKey);

        // Simula 80% de chance de sucesso
        boolean success = random.nextInt(100) < 80;

        if (success) {
            String fakeGatewayId = "FAKE-" + UUID.randomUUID();
            log.info("[FAKE GATEWAY] Sucesso! ID Gerado: {}", fakeGatewayId);
            return new PaymentGatewayResponse(true, fakeGatewayId, null);
        } else {
            log.warn("[FAKE GATEWAY] Falha simulada no processamento.");
            return new PaymentGatewayResponse(false, null, "Cartão recusado (simulação)");
        }
    }
}