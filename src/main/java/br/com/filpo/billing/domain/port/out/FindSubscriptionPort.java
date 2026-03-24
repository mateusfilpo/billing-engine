package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Subscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindSubscriptionPort {
    Optional<Subscription> findById(UUID id);

    List<Subscription> findByCustomerId(UUID customerId);
}