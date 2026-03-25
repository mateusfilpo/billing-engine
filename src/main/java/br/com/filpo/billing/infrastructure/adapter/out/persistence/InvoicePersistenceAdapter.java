package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.port.out.FindInvoicePort;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.InvoiceJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataInvoiceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class InvoicePersistenceAdapter implements SaveInvoicePort, FindInvoicePort {

    private final SpringDataInvoiceRepository repository;
    private final InvoicePersistenceMapper mapper;

    @Override
    public Invoice save(Invoice invoice) {
        InvoiceJpaEntity entity = mapper.toEntity(invoice);
        InvoiceJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Invoice> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Invoice> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}