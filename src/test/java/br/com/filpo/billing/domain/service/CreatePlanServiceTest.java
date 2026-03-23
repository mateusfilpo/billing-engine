package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.BillingCycle;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.model.PlanStatus;
import br.com.filpo.billing.domain.port.out.SavePlanPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePlanServiceTest {

    @Mock
    private SavePlanPort savePlanPort;

    @InjectMocks
    private CreatePlanService createPlanService;

    @Test
    @DisplayName("Deve criar um plano com sucesso e retornar o objeto salvo")
    void shouldCreatePlanSuccessfully() {
        // Arrange
        Plan planToCreate = Plan.createNew(
                "Plano Premium",
                "Acesso completo",
                new BigDecimal("99.90"),
                BillingCycle.MONTHLY);

        when(savePlanPort.save(any(Plan.class))).thenReturn(planToCreate);

        // Act
        Plan result = createPlanService.create(planToCreate);

        // Assert
        assertNotNull(result);
        assertEquals("Plano Premium", result.getName());
        assertEquals(new BigDecimal("99.90"), result.getPrice());
        assertEquals(BillingCycle.MONTHLY, result.getBillingCycle());
        assertEquals(PlanStatus.ACTIVE, result.getStatus());

        verify(savePlanPort, times(1)).save(planToCreate);
    }
}