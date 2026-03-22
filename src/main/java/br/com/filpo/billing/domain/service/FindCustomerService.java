package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.in.FindCustomerUseCase;
import br.com.filpo.billing.domain.port.out.FindCustomerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindCustomerService implements FindCustomerUseCase {

    private final FindCustomerPort findCustomerPort;

    @Override
    public Optional<Customer> findById(UUID id) {
        return findCustomerPort.findById(id);
    }
}