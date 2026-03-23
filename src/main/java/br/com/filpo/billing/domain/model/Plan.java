package br.com.filpo.billing.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class Plan {

    private final UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private BillingCycle billingCycle;
    private PlanStatus status;
    private final LocalDateTime createdAt;

    public static Plan createNew(String name, String description, BigDecimal price, BillingCycle billingCycle) {
        validateInputs(name, price, billingCycle);

        return Plan.builder()
                .name(name)
                .description(description)
                .price(price)
                .billingCycle(billingCycle)
                .status(PlanStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();
    }

    public void updateDetails(String newName, String newDescription, BigDecimal newPrice,
            BillingCycle newBillingCycle) {
        validateInputs(newName, newPrice, newBillingCycle);

        this.name = newName;
        this.description = newDescription;
        this.price = newPrice;
        this.billingCycle = newBillingCycle;
    }

    public void deactivate() {
        this.status = PlanStatus.INACTIVE;
    }

    private static void validateInputs(String name, BigDecimal price, BillingCycle billingCycle) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O nome do plano não pode ser vazio");
        }
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero");
        }
        if (billingCycle == null) {
            throw new IllegalArgumentException("O ciclo de cobrança é obrigatório");
        }
    }
}