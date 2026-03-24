package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.domain.port.in.CancelSubscriptionUseCase;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class CancelSubscriptionService implements CancelSubscriptionUseCase {

    private final FindSubscriptionPort findSubscriptionPort;
    private final SaveSubscriptionPort saveSubscriptionPort;

    @Override
    @Transactional
    public Subscription cancel(UUID subscriptionId) {
        log.info("Solicitando cancelamento da assinatura: {}", subscriptionId);

        Subscription subscription = findSubscriptionPort.findById(subscriptionId)
                .orElseThrow(
                        () -> new EntityNotFoundException("Assinatura não encontrada com o ID: " + subscriptionId));

        subscription.cancel();

        return saveSubscriptionPort.save(subscription);
    }
}