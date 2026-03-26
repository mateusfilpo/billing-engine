package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Payment;
import java.util.UUID;

public interface ProcessPaymentUseCase {
    Payment process(UUID invoiceId);
}