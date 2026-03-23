package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.port.in.FindPlanUseCase;
import br.com.filpo.billing.domain.port.out.FindPlanPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FindPlanService implements FindPlanUseCase {

    private final FindPlanPort findPlanPort;

    @Override
    public Optional<Plan> findById(UUID id) {
        return findPlanPort.findById(id);
    }

    @Override
    public List<Plan> findAll() {
        return findPlanPort.findAll();
    }
}