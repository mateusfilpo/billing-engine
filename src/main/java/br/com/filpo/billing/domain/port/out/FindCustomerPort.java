package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Customer;
import java.util.UUID;
import java.util.Optional;

public interface FindCustomerPort {
    Optional<Customer> findById(UUID id);
}