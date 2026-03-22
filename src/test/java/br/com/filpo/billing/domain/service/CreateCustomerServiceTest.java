package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.port.out.SaveCustomerPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateCustomerServiceTest {

    @Mock
    private SaveCustomerPort saveCustomerPort;

    @InjectMocks
    private CreateCustomerService createCustomerService;

    @Test
    @DisplayName("Deve salvar um cliente com sucesso e retornar o objeto salvo")
    void shouldCreateCustomerSuccessfully() {
        // Arrange
        Customer customerToCreate = Customer.createNew("Mateus", "mateus@filpo.com.br", "12345678900");

        when(saveCustomerPort.save(any(Customer.class))).thenReturn(customerToCreate);

        // Act
        Customer result = createCustomerService.create(customerToCreate);

        // Assert
        assertNotNull(result);
        assertEquals("mateus@filpo.com.br", result.getEmail());
        assertEquals("ACTIVE", result.getStatus());

        verify(saveCustomerPort, times(1)).save(customerToCreate);
    }
}