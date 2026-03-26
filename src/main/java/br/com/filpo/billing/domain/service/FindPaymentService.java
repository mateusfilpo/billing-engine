package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Payment;
import br.com.filpo.billing.domain.port.in.FindPaymentUseCase;
import br.com.filpo.billing.domain.port.out.FindPaymentPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindPaymentService implements FindPaymentUseCase {

    private final FindPaymentPort findPaymentPort;

    @Override
    public Optional<Payment> findById(UUID id) {
        return findPaymentPort.findById(id);
    }

    @Override
    public List<Payment> findByInvoiceId(UUID invoiceId) {
        return findPaymentPort.findByInvoiceId(invoiceId);
    }
}