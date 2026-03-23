package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Plan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface FindPlanPort {
    Optional<Plan> findById(UUID id);

    List<Plan> findAll();
}