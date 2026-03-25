package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Invoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindInvoicePort {
    Optional<Invoice> findById(UUID id);

    List<Invoice> findByCustomerId(UUID customerId);
}