package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.BillingCycle;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.port.in.UpdatePlanUseCase;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import br.com.filpo.billing.domain.port.out.SavePlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UpdatePlanService implements UpdatePlanUseCase {

    private final FindPlanPort findPlanPort;
    private final SavePlanPort savePlanPort;

    @Override
    @Transactional
    public Plan update(UUID id, String name, String description, BigDecimal price, BillingCycle billingCycle) {
        Plan plan = findPlanPort.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com o ID: " + id));

        plan.updateDetails(name, description, price, billingCycle);

        return savePlanPort.save(plan);
    }
}