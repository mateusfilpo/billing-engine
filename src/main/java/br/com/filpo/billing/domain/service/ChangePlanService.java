package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.in.ChangePlanUseCase;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChangePlanService implements ChangePlanUseCase {

    private final FindSubscriptionPort findSubscriptionPort;
    private final FindPlanPort findPlanPort;
    private final SaveSubscriptionPort saveSubscriptionPort;
    private final SaveInvoicePort saveInvoicePort;

    @Override
    @Transactional
    public Invoice changePlan(UUID subscriptionId, UUID newPlanId) {
        log.info("Iniciando troca de plano para a assinatura: {} -> Novo Plano: {}", subscriptionId, newPlanId);

        Subscription subscription = findSubscriptionPort.findById(subscriptionId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Assinatura não encontrada com o ID: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Apenas assinaturas ativas podem trocar de plano");
        }

        Plan oldPlan = findPlanPort.findById(subscription.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plano antigo não encontrado"));

        Plan newPlan = findPlanPort.findById(newPlanId)
                .orElseThrow(() -> new EntityNotFoundException("Novo plano não encontrado com o ID: " + newPlanId));

        if (newPlan.getStatus() != PlanStatus.ACTIVE) {
            throw new IllegalStateException("Não é possível trocar para um plano inativo");
        }

        LocalDate today = LocalDate.now();

        // 1. Calcula o Crédito (o que ele já pagou e não vai usar)
        BigDecimal credit = ProrateCalculator.calculateCredit(
                oldPlan.getPrice(), today, subscription.getCurrentPeriodStart(), subscription.getCurrentPeriodEnd());

        // 2. Calcula o Débito (o que ele vai usar do plano novo até o fim do ciclo
        // atual)
        BigDecimal debit = ProrateCalculator.calculateDebit(
                newPlan.getPrice(), today, subscription.getCurrentPeriodStart(), subscription.getCurrentPeriodEnd());

        // 3. Cria os Itens da Fatura (Crédito entra negativo)
        InvoiceItem creditItem = InvoiceItem.createNew("Crédito proporcional — " + oldPlan.getName(), credit.negate(),
                1);
        InvoiceItem debitItem = InvoiceItem.createNew("Débito proporcional — " + newPlan.getName(), debit, 1);

        // 4. Cria a Fatura de Ajuste vencendo hoje mesmo
        Invoice adjustmentInvoice = Invoice.createFromSubscription(
                subscription.getCustomerId(),
                subscription.getId(),
                LocalDateTime.now(),
                List.of(creditItem, debitItem));

        // 5. Atualiza a Assinatura e Salva tudo
        subscription.changePlan(newPlan.getId());
        saveSubscriptionPort.save(subscription);

        Invoice savedInvoice = saveInvoicePort.save(adjustmentInvoice);
        log.info("Troca de plano concluída. Fatura de ajuste gerada: {}", savedInvoice.getId());

        return savedInvoice;
    }
}