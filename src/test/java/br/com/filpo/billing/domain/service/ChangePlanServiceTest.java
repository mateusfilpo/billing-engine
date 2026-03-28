package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangePlanServiceTest {

    @Mock
    private FindSubscriptionPort findSubscriptionPort;
    @Mock
    private FindPlanPort findPlanPort;
    @Mock
    private SaveSubscriptionPort saveSubscriptionPort;
    @Mock
    private SaveInvoicePort saveInvoicePort;

    @InjectMocks
    private ChangePlanService changePlanService;

    @Test
    @DisplayName("Deve trocar o plano, calcular prorate e gerar fatura de ajuste com sucesso")
    void shouldChangePlanAndGenerateAdjustmentInvoice() {
        UUID subId = UUID.randomUUID();
        UUID oldPlanId = UUID.randomUUID();
        UUID newPlanId = UUID.randomUUID();

        // Ciclo de 30 dias. Estamos trocando hoje (faltando tempo para acabar)
        LocalDate start = LocalDate.now().minusDays(15);
        LocalDate end = LocalDate.now().plusDays(15);

        Subscription sub = Subscription.builder()
                .id(subId).planId(oldPlanId).status(SubscriptionStatus.ACTIVE)
                .currentPeriodStart(start).currentPeriodEnd(end).build();

        Plan oldPlan = Plan.builder().id(oldPlanId).name("Old").price(new BigDecimal("100.00"))
                .status(PlanStatus.ACTIVE).build();
        Plan newPlan = Plan.builder().id(newPlanId).name("New").price(new BigDecimal("200.00"))
                .status(PlanStatus.ACTIVE).build();

        when(findSubscriptionPort.findById(subId)).thenReturn(Optional.of(sub));
        when(findPlanPort.findById(oldPlanId)).thenReturn(Optional.of(oldPlan));
        when(findPlanPort.findById(newPlanId)).thenReturn(Optional.of(newPlan));
        when(saveInvoicePort.save(any(Invoice.class))).thenAnswer(i -> i.getArgument(0));

        Invoice adjustmentInvoice = changePlanService.changePlan(subId, newPlanId);

        // Verifica os itens da fatura de ajuste
        assertNotNull(adjustmentInvoice);
        assertEquals(2, adjustmentInvoice.getItems().size()); // Débito e Crédito

        // Verifica se a subscription foi atualizada
        assertEquals(newPlanId, sub.getPlanId());
        verify(saveSubscriptionPort).save(sub);
        verify(saveInvoicePort).save(adjustmentInvoice);
    }

    @Test
    @DisplayName("Deve lançar exceção se a assinatura não estiver ativa")
    void shouldThrowIfSubscriptionIsNotActive() {
        Subscription sub = Subscription.builder().status(SubscriptionStatus.CANCELLED).build();
        when(findSubscriptionPort.findById(any())).thenReturn(Optional.of(sub));

        assertThrows(IllegalStateException.class,
                () -> changePlanService.changePlan(UUID.randomUUID(), UUID.randomUUID()));
        verifyNoInteractions(findPlanPort, saveSubscriptionPort, saveInvoicePort);
    }
}