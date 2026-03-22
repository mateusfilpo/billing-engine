package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.TestcontainersConfiguration;
import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class CustomerPersistenceAdapterIT {

    @Autowired
    private CustomerPersistenceAdapter adapter;

    @Autowired
    private SpringDataCustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve persistir um cliente no banco de dados real (Docker) e recuperá-lo")
    void shouldSaveAndFindCustomerInDatabase() {
        // Arrange
        Customer customer = Customer.createNew("Filpo Tech", "tech@filpo.com.br", "99.999.999/0001-99");

        // Act - Salvar via Adapter
        Customer savedCustomer = adapter.save(customer);

        // Assert - Verificar se retornou o objeto salvo
        assertThat(savedCustomer.getId()).isNotNull();
        assertThat(savedCustomer.getName()).isEqualTo("Filpo Tech");

        // Act - Buscar via Adapter (testando a outra Port que implementamos)
        Optional<Customer> foundCustomer = adapter.findById(savedCustomer.getId());

        // Assert - Verificar se o dado no banco é idêntico
        assertThat(foundCustomer).isPresent();
        assertThat(foundCustomer.get().getEmail()).isEqualTo("tech@filpo.com.br");
        assertThat(foundCustomer.get().getDocument()).isEqualTo("99.999.999/0001-99");
    }
}