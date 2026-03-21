package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Customer;

public interface SaveCustomerPort {
    Customer save(Customer customer);
}
