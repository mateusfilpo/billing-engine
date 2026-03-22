package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.out.FindCustomerPort;
import br.com.filpo.billing.domain.port.out.SaveCustomerPort;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.CustomerJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements SaveCustomerPort, FindCustomerPort {

    private final SpringDataCustomerRepository repository;
    private final CustomerPersistenceMapper mapper;

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = mapper.toEntity(customer);
        CustomerJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return repository.findById(id)
                .map(mapper::toDomain);
    }
}