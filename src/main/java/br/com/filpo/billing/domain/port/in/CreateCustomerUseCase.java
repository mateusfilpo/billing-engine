package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Customer;

public interface CreateCustomerUseCase {
    Customer create(Customer customer);
}
