package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Invoice;
import java.util.UUID;

public interface GenerateInvoiceUseCase {
    Invoice generate(UUID subscriptionId);
}