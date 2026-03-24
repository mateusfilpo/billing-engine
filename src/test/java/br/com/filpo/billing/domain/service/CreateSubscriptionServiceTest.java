package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.out.FindCustomerPort;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateSubscriptionServiceTest {

    @Mock
    private FindCustomerPort findCustomerPort;

    @Mock
    private FindPlanPort findPlanPort;

    @Mock
    private SaveSubscriptionPort saveSubscriptionPort;

    @InjectMocks
    private CreateSubscriptionService createSubscriptionService;

    @Test
    @DisplayName("Deve criar uma assinatura com sucesso quando Customer e Plan forem válidos")
    void shouldCreateSubscriptionSuccessfully() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        Customer customer = Customer.createNew("Filpo", "filpo@test.com", "123456");
        Plan plan = Plan.createNew("Premium", "Desc", new BigDecimal("99.90"), BillingCycle.MONTHLY);

        when(findCustomerPort.findById(customerId)).thenReturn(Optional.of(customer));
        when(findPlanPort.findById(planId)).thenReturn(Optional.of(plan));
        when(saveSubscriptionPort.save(any(Subscription.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Subscription result = createSubscriptionService.create(customerId, planId);

        // Assert
        assertNotNull(result);
        assertEquals(SubscriptionStatus.ACTIVE, result.getStatus());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(planId, result.getPlanId());
        assertFalse(result.isCancelAtPeriodEnd());

        verify(saveSubscriptionPort, times(1)).save(any(Subscription.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando o plano estiver inativo")
    void shouldThrowExceptionWhenPlanIsInactive() {
        // Arrange
        UUID customerId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        Customer customer = Customer.createNew("Filpo", "filpo@test.com", "123456");
        Plan plan = Plan.createNew("Premium", "Desc", new BigDecimal("99.90"), BillingCycle.MONTHLY);
        plan.deactivate();

        when(findCustomerPort.findById(customerId)).thenReturn(Optional.of(customer));
        when(findPlanPort.findById(planId)).thenReturn(Optional.of(plan));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> createSubscriptionService.create(customerId, planId));

        assertEquals("Não é possível criar uma assinatura para um plano inativo.", exception.getMessage());
        verify(saveSubscriptionPort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o cliente não for encontrado")
    void shouldThrowExceptionWhenCustomerNotFound() {
        UUID customerId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        when(findCustomerPort.findById(customerId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> createSubscriptionService.create(customerId, planId));

        verify(findPlanPort, never()).findById(any());
    }
}