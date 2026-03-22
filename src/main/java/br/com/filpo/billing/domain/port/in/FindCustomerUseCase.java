package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Customer;
import java.util.UUID;
import java.util.Optional;

public interface FindCustomerUseCase {
    Optional<Customer> findById(UUID id);
}