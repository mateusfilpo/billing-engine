package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.port.in.CreatePlanUseCase;
import br.com.filpo.billing.domain.port.in.FindPlanUseCase;
import br.com.filpo.billing.domain.port.in.UpdatePlanUseCase;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.CreatePlanRequest;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.PlanResponse;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.UpdatePlanRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
public class PlanController {

    private final CreatePlanUseCase createPlanUseCase;
    private final FindPlanUseCase findPlanUseCase;
    private final UpdatePlanUseCase updatePlanUseCase;
    private final PlanWebMapper mapper;

    @PostMapping
    public ResponseEntity<PlanResponse> create(@Valid @RequestBody CreatePlanRequest request) {
        Plan planToCreate = Plan.createNew(
                request.name(),
                request.description(),
                request.price(),
                request.billingCycle());

        Plan savedPlan = createPlanUseCase.create(planToCreate);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(savedPlan));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PlanResponse> findById(@PathVariable UUID id) {
        Plan plan = findPlanUseCase.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado com o ID: " + id));
        return ResponseEntity.ok(mapper.toResponse(plan));
    }

    @GetMapping
    public ResponseEntity<List<PlanResponse>> findAll() {
        List<Plan> plans = findPlanUseCase.findAll();
        return ResponseEntity.ok(mapper.toResponseList(plans));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PlanResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePlanRequest request) {

        Plan updatedPlan = updatePlanUseCase.update(
                id,
                request.name(),
                request.description(),
                request.price(),
                request.billingCycle());

        return ResponseEntity.ok(mapper.toResponse(updatedPlan));
    }
}