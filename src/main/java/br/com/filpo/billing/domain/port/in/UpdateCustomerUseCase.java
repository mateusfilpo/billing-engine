package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Customer;
import java.util.UUID;

public interface UpdateCustomerUseCase {
    Customer update(UUID id, String name, String email);
}