package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.in.GenerateInvoiceUseCase;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenerateInvoiceService implements GenerateInvoiceUseCase {

    private final FindSubscriptionPort findSubscriptionPort;
    private final FindPlanPort findPlanPort;
    private final SaveInvoicePort saveInvoicePort;

    @Override
    @Transactional
    public Invoice generate(UUID subscriptionId) {
        log.info("Gerando fatura para a assinatura: {}", subscriptionId);

        Subscription subscription = findSubscriptionPort.findById(subscriptionId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Assinatura não encontrada com o ID: " + subscriptionId));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Não é possível gerar faturas para assinaturas inativas");
        }

        Plan plan = findPlanPort.findById(subscription.getPlanId())
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado para a assinatura"));

        InvoiceItem item = InvoiceItem.createNew(
                "Cobrança referente ao plano: " + plan.getName(),
                plan.getPrice(),
                1);

        LocalDateTime dueDate = subscription.getCurrentPeriodEnd().atStartOfDay();

        Invoice invoice = Invoice.createFromSubscription(
                subscription.getCustomerId(),
                subscription.getId(),
                dueDate,
                List.of(item));

        Invoice savedInvoice = saveInvoicePort.save(invoice);
        log.info("Fatura gerada com sucesso. ID: {}", savedInvoice.getId());

        return savedInvoice;
    }
}