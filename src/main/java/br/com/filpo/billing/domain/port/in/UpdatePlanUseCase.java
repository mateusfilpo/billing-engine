package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.BillingCycle;
import br.com.filpo.billing.domain.model.Plan;
import java.math.BigDecimal;
import java.util.UUID;

public interface UpdatePlanUseCase {
    Plan update(UUID id, String name, String description, BigDecimal price, BillingCycle billingCycle);
}