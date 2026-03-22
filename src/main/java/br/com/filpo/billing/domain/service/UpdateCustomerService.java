package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.in.UpdateCustomerUseCase;
import br.com.filpo.billing.domain.port.out.FindCustomerPort;
import br.com.filpo.billing.domain.port.out.SaveCustomerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdateCustomerService implements UpdateCustomerUseCase {

    private final FindCustomerPort findCustomerPort;
    private final SaveCustomerPort saveCustomerPort;

    @Override
    @Transactional
    public Customer update(UUID id, String name, String email) {
        Customer customer = findCustomerPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o ID: " + id));

        customer.updateDetails(name, email);

        return saveCustomerPort.save(customer);
    }
}