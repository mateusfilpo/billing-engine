package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Subscription;

public interface SaveSubscriptionPort {
    Subscription save(Subscription subscription);
}