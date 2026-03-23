package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.PlanResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PlanWebMapper {

    public PlanResponse toResponse(Plan plan) {
        return new PlanResponse(
                plan.getId(),
                plan.getName(),
                plan.getDescription(),
                plan.getPrice(),
                plan.getBillingCycle().name(),
                plan.getStatus().name(),
                plan.getCreatedAt() != null ? plan.getCreatedAt().toString() : null);
    }

    public List<PlanResponse> toResponseList(List<Plan> plans) {
        return plans.stream()
                .map(this::toResponse)
                .toList();
    }
}