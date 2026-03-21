package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.out.SaveCustomerPort;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.CustomerJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataCustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomerPersistenceAdapter implements SaveCustomerPort {

    private final SpringDataCustomerRepository repository;

    @Override
    public Customer save(Customer customer) {
        CustomerJpaEntity entity = CustomerJpaEntity.builder()
                .id(customer.getId())
                .name(customer.getName())
                .email(customer.getEmail())
                .document(customer.getDocument())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();

       
        CustomerJpaEntity savedEntity = repository.save(entity);

        
        return Customer.builder()
                .id(savedEntity.getId())
                .name(savedEntity.getName())
                .email(savedEntity.getEmail())
                .document(savedEntity.getDocument())
                .status(savedEntity.getStatus())
                .createdAt(savedEntity.getCreatedAt())
                .updatedAt(savedEntity.getUpdatedAt())
                .build();
    }
}
