package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.TestcontainersConfiguration;
import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataCustomerRepository;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataPlanRepository;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataSubscriptionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SubscriptionPersistenceAdapterIT {

    @Autowired
    private SubscriptionPersistenceAdapter subscriptionAdapter;

    @Autowired
    private CustomerPersistenceAdapter customerAdapter;

    @Autowired
    private PlanPersistenceAdapter planAdapter;

    @Autowired
    private SpringDataSubscriptionRepository subscriptionRepository;
    @Autowired
    private SpringDataCustomerRepository customerRepository;
    @Autowired
    private SpringDataPlanRepository planRepository;

    @BeforeEach
    void setUp() {
        // A ordem de deleção importa por causa das FKs (filhos antes dos pais)
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve persistir uma assinatura conectando Customer e Plan no banco real")
    void shouldSaveAndFindSubscriptionInDatabase() {
        // Arrange - 1. Criar e salvar Cliente
        Customer customer = Customer.createNew("Assinante Teste", "assinante@teste.com", "88888888");
        Customer savedCustomer = customerAdapter.save(customer);

        // Arrange - 2. Criar e salvar Plano
        Plan plan = Plan.createNew("Plano Anual", "12 meses", new BigDecimal("1200.00"), BillingCycle.ANNUAL);
        Plan savedPlan = planAdapter.save(plan);

        // Arrange - 3. Criar Assinatura
        Subscription subscription = Subscription.createNew(savedCustomer.getId(), savedPlan.getId(),
                savedPlan.getBillingCycle());

        // Act - Salvar Assinatura
        Subscription savedSubscription = subscriptionAdapter.save(subscription);

        // Assert - Verifica se foi salva e se o ID foi gerado
        assertThat(savedSubscription.getId()).isNotNull();

        // Act - Buscar por ID
        Optional<Subscription> foundById = subscriptionAdapter.findById(savedSubscription.getId());

        // Assert
        assertThat(foundById).isPresent();
        assertThat(foundById.get().getCustomerId()).isEqualTo(savedCustomer.getId());
        assertThat(foundById.get().getPlanId()).isEqualTo(savedPlan.getId());
        assertThat(foundById.get().getStatus()).isEqualTo(SubscriptionStatus.ACTIVE);

        // Act - Buscar por Customer ID (Lista)
        List<Subscription> foundByCustomer = subscriptionAdapter.findByCustomerId(savedCustomer.getId());

        // Assert
        assertThat(foundByCustomer).hasSize(1);
        assertThat(foundByCustomer.get(0).getId()).isEqualTo(savedSubscription.getId());
    }
}