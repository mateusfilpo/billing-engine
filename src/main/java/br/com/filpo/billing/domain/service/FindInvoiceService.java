package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.port.in.FindInvoiceUseCase;
import br.com.filpo.billing.domain.port.out.FindInvoicePort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindInvoiceService implements FindInvoiceUseCase {

    private final FindInvoicePort findInvoicePort;

    @Override
    public Optional<Invoice> findById(UUID id) {
        return findInvoicePort.findById(id);
    }

    @Override
    public List<Invoice> findByCustomerId(UUID customerId) {
        return findInvoicePort.findByCustomerId(customerId);
    }
}