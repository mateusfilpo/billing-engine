package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.in.CreateCustomerUseCase;
import br.com.filpo.billing.domain.port.in.FindCustomerUseCase;
import br.com.filpo.billing.domain.port.in.UpdateCustomerUseCase;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.CreateCustomerRequest;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.CustomerResponse;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.UpdateCustomerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CreateCustomerUseCase createCustomerUseCase;
    private final FindCustomerUseCase findCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final CustomerWebMapper mapper;

    @PostMapping
    public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CreateCustomerRequest request) {
        Customer customerToCreate = Customer.createNew(
                request.name(),
                request.email(),
                request.document());

        Customer savedCustomer = createCustomerUseCase.create(customerToCreate);

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(savedCustomer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> findById(@PathVariable UUID id) {
        Customer customer = findCustomerUseCase.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o ID: " + id));

        return ResponseEntity.ok(mapper.toResponse(customer));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCustomerRequest request) {

        Customer updatedCustomer = updateCustomerUseCase.update(
                id,
                request.name(),
                request.email());

        return ResponseEntity.ok(mapper.toResponse(updatedCustomer));
    }
}