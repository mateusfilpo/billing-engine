package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Plan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindPlanUseCase {
    Optional<Plan> findById(UUID id);

    List<Plan> findAll();
}