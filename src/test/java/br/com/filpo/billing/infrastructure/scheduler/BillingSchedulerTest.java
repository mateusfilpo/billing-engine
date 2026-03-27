package br.com.filpo.billing.infrastructure.scheduler;

import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.in.GenerateInvoiceUseCase;
import br.com.filpo.billing.domain.port.in.ProcessPaymentUseCase;
import br.com.filpo.billing.domain.port.in.UpdateInvoiceStatusUseCase;
import br.com.filpo.billing.domain.port.out.FindPaymentPort;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BillingSchedulerTest {

        @Mock
        private FindSubscriptionPort findSubscriptionPort;
        @Mock
        private SaveSubscriptionPort saveSubscriptionPort;
        @Mock
        private FindPlanPort findPlanPort;
        @Mock
        private GenerateInvoiceUseCase generateInvoiceUseCase;
        @Mock
        private UpdateInvoiceStatusUseCase updateInvoiceStatusUseCase;
        @Mock
        private ProcessPaymentUseCase processPaymentUseCase;
        @Mock
        private FindPaymentPort findPaymentPort;

        @InjectMocks
        private BillingScheduler billingScheduler;

        @Test
        @DisplayName("Deve gerar fatura, pagar e renovar período de assinatura ativa")
        void shouldProcessDailyBillingAndRenewSuccessfully() {
                UUID subId = UUID.randomUUID();
                UUID planId = UUID.randomUUID();
                UUID invoiceId = UUID.randomUUID();

                Subscription sub = Subscription.builder()
                                .id(subId).planId(planId).status(SubscriptionStatus.ACTIVE)
                                .currentPeriodEnd(LocalDate.now()).cancelAtPeriodEnd(false).build();

                Plan plan = Plan.createNew("Plano", "Desc", BigDecimal.TEN, BillingCycle.MONTHLY);
                Invoice invoice = Invoice.builder().id(invoiceId).build();
                Payment payment = Payment.builder().status(PaymentStatus.PAID).build();

                when(findSubscriptionPort.findActiveByPeriodEnd(any(LocalDate.class))).thenReturn(List.of(sub));
                when(generateInvoiceUseCase.generate(subId)).thenReturn(invoice);
                when(processPaymentUseCase.process(invoiceId)).thenReturn(payment);
                when(findPlanPort.findById(planId)).thenReturn(Optional.of(plan));

                billingScheduler.processDailyBilling();

                verify(updateInvoiceStatusUseCase).open(invoiceId);
                verify(saveSubscriptionPort).save(sub);
                assertEquals(LocalDate.now().plusMonths(1), sub.getCurrentPeriodEnd());
        }

        @Test
        @DisplayName("Deve cancelar a assinatura em vez de cobrar se cancelAtPeriodEnd for true")
        void shouldCancelSubscriptionIfCancelAtPeriodEndIsTrue() {
                Subscription sub = Subscription.builder()
                                .id(UUID.randomUUID()).status(SubscriptionStatus.ACTIVE)
                                .currentPeriodEnd(LocalDate.now()).cancelAtPeriodEnd(true).build();

                when(findSubscriptionPort.findActiveByPeriodEnd(any(LocalDate.class))).thenReturn(List.of(sub));

                billingScheduler.processDailyBilling();

                assertEquals(SubscriptionStatus.CANCELLED, sub.getStatus());
                verify(saveSubscriptionPort).save(sub);
                verifyNoInteractions(generateInvoiceUseCase, updateInvoiceStatusUseCase, processPaymentUseCase);
        }

        @Test
        @DisplayName("Uma exceção em uma assinatura não deve interromper o processamento das outras")
        void exceptionInOneSubscriptionShouldNotStopOthers() {
                UUID subId1 = UUID.randomUUID();
                UUID subId2 = UUID.randomUUID();
                UUID invoiceId2 = UUID.randomUUID();
                UUID planId2 = UUID.randomUUID();

                Subscription sub1 = Subscription.builder().id(subId1).cancelAtPeriodEnd(false).build();
                Subscription sub2 = Subscription.builder().id(subId2).planId(planId2)
                                .currentPeriodEnd(LocalDate.now()).cancelAtPeriodEnd(false).build();

                Plan plan = Plan.createNew("Plano", "Desc", BigDecimal.TEN, BillingCycle.MONTHLY);
                Invoice invoice2 = Invoice.builder().id(invoiceId2).build();
                Payment payment2 = Payment.builder().status(PaymentStatus.PAID).build();

                when(findSubscriptionPort.findActiveByPeriodEnd(any())).thenReturn(List.of(sub1, sub2));

                when(generateInvoiceUseCase.generate(subId1))
                                .thenThrow(new RuntimeException("Simulando falha de banco de dados"));

                when(generateInvoiceUseCase.generate(subId2)).thenReturn(invoice2);
                when(processPaymentUseCase.process(invoiceId2)).thenReturn(payment2);
                when(findPlanPort.findById(planId2)).thenReturn(Optional.of(plan));

                billingScheduler.processDailyBilling();

                verify(generateInvoiceUseCase).generate(subId1);
                verify(generateInvoiceUseCase).generate(subId2);
                verify(saveSubscriptionPort).save(sub2);
        }

        @Test
        @DisplayName("Deve tentar reprocessar pagamentos falhos na rotina de retry")
        void shouldRetryFailedPayments() {
                UUID invoiceId = UUID.randomUUID();
                Payment failedPayment = Payment.builder().id(UUID.randomUUID()).invoiceId(invoiceId)
                                .status(PaymentStatus.FAILED).build();

                when(findPaymentPort.findFailedPaymentsDueForRetry(any(LocalDateTime.class)))
                                .thenReturn(List.of(failedPayment));

                billingScheduler.retryFailedPayments();

                verify(processPaymentUseCase).process(invoiceId);
        }
}