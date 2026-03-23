package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.port.in.CreatePlanUseCase;
import br.com.filpo.billing.domain.port.out.SavePlanPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreatePlanService implements CreatePlanUseCase {

    private final SavePlanPort savePlanPort;

    @Override
    @Transactional
    public Plan create(Plan plan) {
        log.info("Iniciando a criação do plano: {}", plan.getName());
        Plan savedPlan = savePlanPort.save(plan);
        log.info("Plano criado com sucesso. ID: {}", savedPlan.getId());
        return savedPlan;
    }
}