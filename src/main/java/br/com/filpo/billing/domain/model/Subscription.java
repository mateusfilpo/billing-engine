package br.com.filpo.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Subscription {

    private final UUID id;
    private final UUID customerId;
    private final UUID planId;
    private SubscriptionStatus status;
    private LocalDate currentPeriodStart;
    private LocalDate currentPeriodEnd;
    private boolean cancelAtPeriodEnd;
    private LocalDateTime cancelledAt;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Subscription createNew(UUID customerId, UUID planId, BillingCycle billingCycle) {
        if (customerId == null || planId == null || billingCycle == null) {
            throw new IllegalArgumentException("Customer, Plan e BillingCycle são obrigatórios");
        }

        LocalDate startDate = LocalDate.now();
        LocalDate endDate = calculatePeriodEnd(startDate, billingCycle);

        return Subscription.builder()
                .customerId(customerId)
                .planId(planId)
                .status(SubscriptionStatus.ACTIVE)
                .currentPeriodStart(startDate)
                .currentPeriodEnd(endDate)
                .cancelAtPeriodEnd(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public void cancel() {
        this.cancelAtPeriodEnd = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        if (this.status != SubscriptionStatus.ACTIVE) {
            throw new IllegalStateException("Apenas assinaturas ativas podem ser suspensas");
        }
        this.status = SubscriptionStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void reactivate() {
        if (this.status != SubscriptionStatus.SUSPENDED) {
            throw new IllegalStateException("Apenas assinaturas suspensas podem ser reativadas");
        }
        this.status = SubscriptionStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void renewPeriod(BillingCycle billingCycle) {
        this.currentPeriodStart = this.currentPeriodEnd;
        this.currentPeriodEnd = calculatePeriodEnd(this.currentPeriodStart, billingCycle);
        this.updatedAt = LocalDateTime.now();
    }

    public void handleEndOfPeriod() {
        if (this.cancelAtPeriodEnd) {
            this.status = SubscriptionStatus.CANCELLED;
            this.cancelledAt = LocalDateTime.now();
            this.updatedAt = LocalDateTime.now();
        }
    }

    private static LocalDate calculatePeriodEnd(LocalDate startDate, BillingCycle billingCycle) {
        return switch (billingCycle) {
            case MONTHLY -> startDate.plusMonths(1);
            case QUARTERLY -> startDate.plusMonths(3);
            case ANNUAL -> startDate.plusYears(1);
        };
    }
}