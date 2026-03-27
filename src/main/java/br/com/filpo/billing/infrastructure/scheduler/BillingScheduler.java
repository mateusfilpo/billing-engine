package br.com.filpo.billing.infrastructure.scheduler;

import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.in.GenerateInvoiceUseCase;
import br.com.filpo.billing.domain.port.in.ProcessPaymentUseCase;
import br.com.filpo.billing.domain.port.in.UpdateInvoiceStatusUseCase;
import br.com.filpo.billing.domain.port.out.FindPaymentPort;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class BillingScheduler {

    private final FindSubscriptionPort findSubscriptionPort;
    private final SaveSubscriptionPort saveSubscriptionPort;
    private final FindPlanPort findPlanPort;
    private final GenerateInvoiceUseCase generateInvoiceUseCase;
    private final UpdateInvoiceStatusUseCase updateInvoiceStatusUseCase;
    private final ProcessPaymentUseCase processPaymentUseCase;
    private final FindPaymentPort findPaymentPort;

    /**
     * Roda todo dia à meia-noite (00:00:00).
     * Busca assinaturas que vencem hoje e orquestra a cobrança.
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void processDailyBilling() {
        LocalDate today = LocalDate.now();
        log.info("[SCHEDULER] Iniciando faturamento diário para a data: {}", today);

        List<Subscription> expiringSubscriptions = findSubscriptionPort.findActiveByPeriodEnd(today);
        log.info("[SCHEDULER] Encontradas {} assinaturas para processar hoje.", expiringSubscriptions.size());

        for (Subscription subscription : expiringSubscriptions) {
            try {
                subscription.handleEndOfPeriod();
                if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
                    saveSubscriptionPort.save(subscription);
                    log.info("Assinatura {} cancelada conforme programado (cancelAtPeriodEnd).", subscription.getId());
                    continue;
                }

                Invoice invoice = generateInvoiceUseCase.generate(subscription.getId());
                updateInvoiceStatusUseCase.open(invoice.getId());
                Payment payment = processPaymentUseCase.process(invoice.getId());

                if (payment.getStatus() == PaymentStatus.PAID) {
                    Plan plan = findPlanPort.findById(subscription.getPlanId())
                            .orElseThrow(() -> new IllegalStateException("Plano não encontrado para renovação"));

                    subscription.renewPeriod(plan.getBillingCycle());
                    saveSubscriptionPort.save(subscription);
                    log.info("Assinatura {} renovada com sucesso. Novo vencimento: {}", subscription.getId(),
                            subscription.getCurrentPeriodEnd());
                }

            } catch (Exception e) {
                log.error("[SCHEDULER] Erro ao processar faturamento da assinatura {}: {}", subscription.getId(),
                        e.getMessage(), e);
            }
        }
        log.info("[SCHEDULER] Faturamento diário concluído.");
    }

    /**
     * Roda todo dia às 06:00 da manhã.
     * Busca pagamentos falhos que atingiram o tempo de retry.
     */
    @Scheduled(cron = "0 0 6 * * *")
    public void retryFailedPayments() {
        LocalDateTime now = LocalDateTime.now();
        log.info("[SCHEDULER] Iniciando retry de pagamentos falhos em: {}", now);

        List<Payment> failedPayments = findPaymentPort.findFailedPaymentsDueForRetry(now);
        log.info("[SCHEDULER] Encontrados {} pagamentos para retry.", failedPayments.size());

        for (Payment payment : failedPayments) {
            try {
                log.info("Tentando reprocessar pagamento da fatura {}", payment.getInvoiceId());
                processPaymentUseCase.process(payment.getInvoiceId());
            } catch (Exception e) {
                log.error("[SCHEDULER] Erro no retry do pagamento {}: {}", payment.getId(), e.getMessage(), e);
            }
        }
        log.info("[SCHEDULER] Retry de pagamentos concluído.");
    }
}