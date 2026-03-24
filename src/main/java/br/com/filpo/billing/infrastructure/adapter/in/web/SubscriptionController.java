package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.domain.port.in.CancelSubscriptionUseCase;
import br.com.filpo.billing.domain.port.in.CreateSubscriptionUseCase;
import br.com.filpo.billing.domain.port.in.FindSubscriptionUseCase;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.CreateSubscriptionRequest;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.SubscriptionResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final CreateSubscriptionUseCase createSubscriptionUseCase;
    private final FindSubscriptionUseCase findSubscriptionUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
    private final SubscriptionWebMapper mapper;

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(@Valid @RequestBody CreateSubscriptionRequest request) {
        Subscription savedSubscription = createSubscriptionUseCase.create(request.customerId(), request.planId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(savedSubscription));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubscriptionResponse> findById(@PathVariable UUID id) {
        Subscription subscription = findSubscriptionUseCase.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura não encontrada com o ID: " + id));
        return ResponseEntity.ok(mapper.toResponse(subscription));
    }

    @GetMapping
    public ResponseEntity<List<SubscriptionResponse>> findByCustomerId(@RequestParam UUID customerId) {
        List<Subscription> subscriptions = findSubscriptionUseCase.findByCustomerId(customerId);
        return ResponseEntity.ok(mapper.toResponseList(subscriptions));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(@PathVariable UUID id) {
        Subscription cancelledSubscription = cancelSubscriptionUseCase.cancel(id);
        return ResponseEntity.ok(mapper.toResponse(cancelledSubscription));
    }
}