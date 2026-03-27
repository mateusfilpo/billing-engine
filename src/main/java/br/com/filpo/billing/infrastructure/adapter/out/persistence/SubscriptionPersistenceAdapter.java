package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.domain.model.SubscriptionStatus;
import br.com.filpo.billing.domain.port.out.FindSubscriptionPort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.SubscriptionJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataSubscriptionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SubscriptionPersistenceAdapter implements SaveSubscriptionPort, FindSubscriptionPort {

    private final SpringDataSubscriptionRepository repository;
    private final SubscriptionPersistenceMapper mapper;

    @Override
    public Subscription save(Subscription subscription) {
        SubscriptionJpaEntity entity = mapper.toEntity(subscription);
        SubscriptionJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Subscription> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Subscription> findByCustomerId(UUID customerId) {
        return repository.findByCustomerId(customerId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Subscription> findActiveByPeriodEnd(LocalDate date) {
        return repository.findByStatusAndCurrentPeriodEnd(SubscriptionStatus.ACTIVE.name(), date).stream()
                .map(mapper::toDomain)
                .toList();
    }
}