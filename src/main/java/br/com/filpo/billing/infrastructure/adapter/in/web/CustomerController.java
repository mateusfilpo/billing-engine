package br.com.filpo.billing.infrastructure.adapter.in.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.in.CreateCustomerUseCase;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;

    @PostMapping
    public ResponseEntity<Customer> create(@RequestBody CreateCustomerRequest request) {
        Customer customerToCreate = Customer.createNew(
                request.name(),
                request.email(),
                request.document()
        );

        Customer savedCustomer = createCustomerUseCase.create(customerToCreate);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedCustomer);
    }
    
    record CreateCustomerRequest(String name, String email, String document) {}
}
