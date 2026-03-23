package br.com.filpo.billing.domain.port.out;

import br.com.filpo.billing.domain.model.Plan;

public interface SavePlanPort {
    Plan save(Plan plan);
}