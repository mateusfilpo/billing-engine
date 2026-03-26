package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Payment;

public interface SavePaymentPort {
    Payment save(Payment payment);
}