package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.SavePlanPort;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.entity.PlanJpaEntity;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataPlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PlanPersistenceAdapter implements SavePlanPort, FindPlanPort {

    private final SpringDataPlanRepository repository;
    private final PlanPersistenceMapper mapper;

    @Override
    public Plan save(Plan plan) {
        PlanJpaEntity entity = mapper.toEntity(plan);
        PlanJpaEntity savedEntity = repository.save(entity);
        return mapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Plan> findById(UUID id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Plan> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}