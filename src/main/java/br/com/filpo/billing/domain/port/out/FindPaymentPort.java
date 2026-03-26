package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Payment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindPaymentPort {
    Optional<Payment> findById(UUID id);

    List<Payment> findByInvoiceId(UUID invoiceId);
}