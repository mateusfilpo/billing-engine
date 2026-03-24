package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.model.PlanStatus;
import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.domain.port.in.CreateSubscriptionUseCase;
import br.com.filpo.billing.domain.port.out.FindCustomerPort;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateSubscriptionService implements CreateSubscriptionUseCase {

    private final FindCustomerPort findCustomerPort;
    private final FindPlanPort findPlanPort;
    private final SaveSubscriptionPort saveSubscriptionPort;

    @Override
    @Transactional
    public Subscription create(UUID customerId, UUID planId) {
        log.info("Iniciando a criação de assinatura. Customer: {}, Plan: {}", customerId, planId);

        findCustomerPort.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado com o ID: " + customerId));

        Plan plan = findPlanPort.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com o ID: " + planId));

        if (plan.getStatus() != PlanStatus.ACTIVE) {
            throw new IllegalStateException("Não é possível criar uma assinatura para um plano inativo.");
        }

        Subscription subscription = Subscription.createNew(customerId, planId, plan.getBillingCycle());

        Subscription savedSubscription = saveSubscriptionPort.save(subscription);

        log.info("Assinatura criada com sucesso. ID: {}", savedSubscription.getId());
        return savedSubscription;
    }
}