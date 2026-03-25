package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Invoice;
import java.util.UUID;

public interface UpdateInvoiceStatusUseCase {
    Invoice open(UUID invoiceId);

    Invoice markAsPaid(UUID invoiceId);

    Invoice voidInvoice(UUID invoiceId);
}