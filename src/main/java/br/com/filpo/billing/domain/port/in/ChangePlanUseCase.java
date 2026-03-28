package br.com.filpo.billing.domain.port.in;

import br.com.filpo.billing.domain.model.Invoice;
import java.util.UUID;

public interface ChangePlanUseCase {
    Invoice changePlan(UUID subscriptionId, UUID newPlanId);
}