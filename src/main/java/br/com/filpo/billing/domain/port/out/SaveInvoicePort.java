package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Invoice;

public interface SaveInvoicePort {
    Invoice save(Invoice invoice);
}