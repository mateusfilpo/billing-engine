package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.CustomerResponse;
import org.springframework.stereotype.Component;

@Component
public class CustomerWebMapper {

    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getName(),
                customer.getEmail(),
                customer.getStatus(),
                customer.getCreatedAt() != null ? customer.getCreatedAt().toString() : null);
    }
}