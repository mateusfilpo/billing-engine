package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Subscription;
import java.util.UUID;

public interface CancelSubscriptionUseCase {
    Subscription cancel(UUID subscriptionId);
}