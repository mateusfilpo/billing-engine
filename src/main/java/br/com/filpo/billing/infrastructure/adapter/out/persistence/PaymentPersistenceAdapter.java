package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Payment;
import br.com.filpo.billing.domain.port.out.FindPaymentPort;
import br.com.filpo.billing.domain.port.out.SavePaymentPort;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.PaymentJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentPersistenceAdapter implements SavePaymentPort, FindPaymentPort {

    private final SpringDataPaymentRepository repository;
    private final PaymentPersistenceMapper mapper;

    @Override
    public Payment save(Payment payment) {
        PaymentJpaEntity entity = mapper.toEntity(payment);
        PaymentJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Payment> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Payment> findByInvoiceId(UUID invoiceId) {
        return repository.findByInvoiceId(invoiceId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}