package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Invoice;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindInvoiceUseCase {
    Optional<Invoice> findById(UUID id);

    List<Invoice> findByCustomerId(UUID customerId);
}