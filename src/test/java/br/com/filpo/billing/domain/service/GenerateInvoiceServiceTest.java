package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
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
class GenerateInvoiceServiceTest {

    @Mock
    private FindSubscriptionPort findSubscriptionPort;

    @Mock
    private FindPlanPort findPlanPort;

    @Mock
    private SaveInvoicePort saveInvoicePort;

    @InjectMocks
    private GenerateInvoiceService generateInvoiceService;

    @Test
    @DisplayName("Deve gerar uma fatura (DRAFT) com 1 item baseado no plano da assinatura")
    void shouldGenerateInvoiceSuccessfully() {
        // Arrange
        UUID subscriptionId = UUID.randomUUID();
        UUID customerId = UUID.randomUUID();
        UUID planId = UUID.randomUUID();

        Subscription subscription = Subscription.builder()
                .id(subscriptionId)
                .customerId(customerId)
                .planId(planId)
                .status(SubscriptionStatus.ACTIVE)
                .currentPeriodEnd(java.time.LocalDate.now().plusMonths(1))
                .build();

        Plan plan = Plan.createNew("Plano Teste", "Desc", new BigDecimal("150.00"), BillingCycle.MONTHLY);

        when(findSubscriptionPort.findById(subscriptionId)).thenReturn(Optional.of(subscription));
        when(findPlanPort.findById(planId)).thenReturn(Optional.of(plan));
        when(saveInvoicePort.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        Invoice result = generateInvoiceService.generate(subscriptionId);

        // Assert
        assertNotNull(result);
        assertEquals(InvoiceStatus.DRAFT, result.getStatus());
        assertEquals(customerId, result.getCustomerId());
        assertEquals(subscriptionId, result.getSubscriptionId());
        assertEquals(1, result.getItems().size());
        assertEquals(new BigDecimal("150.00"), result.getTotalAmount());
        assertEquals("Cobrança referente ao plano: Plano Teste", result.getItems().get(0).getDescription());

        verify(saveInvoicePort, times(1)).save(any(Invoice.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar gerar fatura para assinatura inativa")
    void shouldThrowExceptionWhenSubscriptionIsInactive() {
        // Arrange
        UUID subscriptionId = UUID.randomUUID();
        Subscription subscription = Subscription.createNew(UUID.randomUUID(), UUID.randomUUID(), BillingCycle.MONTHLY);
        subscription.suspend();

        when(findSubscriptionPort.findById(subscriptionId)).thenReturn(Optional.of(subscription));

        // Act & Assert
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> generateInvoiceService.generate(subscriptionId));

        assertEquals("Não é possível gerar faturas para assinaturas inativas", exception.getMessage());
        verify(findPlanPort, never()).findById(any());
        verify(saveInvoicePort, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção se a assinatura não for encontrada")
    void shouldThrowExceptionWhenSubscriptionNotFound() {
        UUID subscriptionId = UUID.randomUUID();
        when(findSubscriptionPort.findById(subscriptionId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> generateInvoiceService.generate(subscriptionId));
    }
}