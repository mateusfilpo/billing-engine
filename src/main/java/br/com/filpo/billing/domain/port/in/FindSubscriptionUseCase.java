package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Subscription;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindSubscriptionUseCase {
    Optional<Subscription> findById(UUID id);

    List<Subscription> findByCustomerId(UUID customerId);
}