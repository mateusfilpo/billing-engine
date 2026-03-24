package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.domain.port.in.FindSubscriptionUseCase;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindSubscriptionService implements FindSubscriptionUseCase {

    private final FindSubscriptionPort findSubscriptionPort;

    @Override
    public Optional<Subscription> findById(UUID id) {
        return findSubscriptionPort.findById(id);
    }

    @Override
    public List<Subscription> findByCustomerId(UUID customerId) {
        return findSubscriptionPort.findByCustomerId(customerId);
    }
}