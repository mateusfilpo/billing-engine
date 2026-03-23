package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Plan;

public interface CreatePlanUseCase {
    Plan create(Plan plan);
}