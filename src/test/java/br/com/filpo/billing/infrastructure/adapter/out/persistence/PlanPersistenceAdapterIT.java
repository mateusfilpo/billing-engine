package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.TestcontainersConfiguration;
import br.com.filpo.billing.domain.model.BillingCycle;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.model.PlanStatus;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataPlanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class PlanPersistenceAdapterIT {

    @Autowired
    private PlanPersistenceAdapter adapter;

    @Autowired
    private SpringDataPlanRepository repository;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    @DisplayName("Deve persistir um plano no banco de dados real e recuperá-lo garantindo conversão de Enums")
    void shouldSaveAndFindPlanInDatabase() {
        // Arrange
        Plan plan = Plan.createNew(
                "Plano Anual Enterprise",
                "Plano para grandes empresas",
                new BigDecimal("1200.00"),
                BillingCycle.ANNUAL);

        // Act - Salvar
        Plan savedPlan = adapter.save(plan);

        // Assert - Verifica se foi salvo com ID gerado
        assertThat(savedPlan.getId()).isNotNull();
        assertThat(savedPlan.getName()).isEqualTo("Plano Anual Enterprise");

        // Act - Buscar pelo ID
        Optional<Plan> foundPlan = adapter.findById(savedPlan.getId());

        // Assert - Verifica se os dados (incluindo os Enums) foram persistidos e lidos
        // corretamente
        assertThat(foundPlan).isPresent();
        assertThat(foundPlan.get().getPrice()).isEqualByComparingTo(new BigDecimal("1200.00"));
        assertThat(foundPlan.get().getBillingCycle()).isEqualTo(BillingCycle.ANNUAL);
        assertThat(foundPlan.get().getStatus()).isEqualTo(PlanStatus.ACTIVE);
    }
}