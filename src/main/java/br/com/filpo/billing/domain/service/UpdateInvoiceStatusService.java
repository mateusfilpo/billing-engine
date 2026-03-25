package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.port.in.UpdateInvoiceStatusUseCase;
import br.com.filpo.billing.domain.port.out.FindInvoicePort;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateInvoiceStatusService implements UpdateInvoiceStatusUseCase {

    private final FindInvoicePort findInvoicePort;
    private final SaveInvoicePort saveInvoicePort;

    @Override
    @Transactional
    public Invoice open(UUID invoiceId) {
        Invoice invoice = getInvoice(invoiceId);
        invoice.open();
        return saveInvoicePort.save(invoice);
    }

    @Override
    @Transactional
    public Invoice markAsPaid(UUID invoiceId) {
        Invoice invoice = getInvoice(invoiceId);
        invoice.markAsPaid();
        return saveInvoicePort.save(invoice);
    }

    @Override
    @Transactional
    public Invoice voidInvoice(UUID invoiceId) {
        Invoice invoice = getInvoice(invoiceId);
        invoice.voidInvoice();
        return saveInvoicePort.save(invoice);
    }

    private Invoice getInvoice(UUID invoiceId) {
        return findInvoicePort.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Fatura não encontrada com o ID: " + invoiceId));
    }
}