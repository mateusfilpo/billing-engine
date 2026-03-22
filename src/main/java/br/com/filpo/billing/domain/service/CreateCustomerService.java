package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.in.CreateCustomerUseCase;
import br.com.filpo.billing.domain.port.out.SaveCustomerPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateCustomerService implements CreateCustomerUseCase {

    private final SaveCustomerPort saveCustomerPort;

    @Override
    @Transactional 
    public Customer create(Customer customer) {
        log.info("Iniciando a criação do cliente: {}", customer.getEmail());

        Customer savedCustomer = saveCustomerPort.save(customer);

        log.info("Cliente criado com sucesso. ID: {}", savedCustomer.getId()); 
        return savedCustomer;
    }
}