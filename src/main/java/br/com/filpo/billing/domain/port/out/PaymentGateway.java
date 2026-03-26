package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.PaymentGatewayResponse;
import java.math.BigDecimal;

public interface PaymentGateway {
    PaymentGatewayResponse charge(BigDecimal amount, String idempotencyKey);
}