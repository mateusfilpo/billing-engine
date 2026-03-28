package br.com.filpo.billing.infrastructure.seed;

import br.com.filpo.billing.domain.model.BillingCycle;
import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.domain.port.out.SaveCustomerPort;
import br.com.filpo.billing.domain.port.out.SavePlanPort;
import br.com.filpo.billing.domain.port.out.SaveSubscriptionPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class DataSeeder implements ApplicationRunner {

    private final SaveCustomerPort saveCustomerPort;
    private final SavePlanPort savePlanPort;
    private final SaveSubscriptionPort saveSubscriptionPort;

    @Override
    public void run(ApplicationArguments args) {
        log.info("[SEED] Iniciando a criação de dados de demonstração (Profile: dev)...");
        Faker faker = new Faker(Locale.of("pt", "BR"));

        // 1. Criar Planos
        Plan basic = savePlanPort
                .save(Plan.createNew("Básico", "Plano de entrada", new BigDecimal("29.90"), BillingCycle.MONTHLY));
        Plan pro = savePlanPort
                .save(Plan.createNew("Pro", "Plano para profissionais", new BigDecimal("99.90"), BillingCycle.MONTHLY));
        Plan enterprise = savePlanPort.save(
                Plan.createNew("Enterprise", "Plano anual corporativo", new BigDecimal("999.00"), BillingCycle.ANNUAL));

        // 2. Criar Clientes e Assinaturas
        List<Plan> plans = List.of(basic, pro, enterprise);

        for (int i = 0; i < 5; i++) {
            Customer customer = saveCustomerPort.save(Customer.createNew(
                    faker.name().fullName(),
                    faker.internet().emailAddress(),
                    faker.cpf().valid()));

            Plan randomPlan = plans.get(faker.number().numberBetween(0, plans.size()));

            saveSubscriptionPort.save(Subscription.createNew(
                    customer.getId(),
                    randomPlan.getId(),
                    randomPlan.getBillingCycle()));
        }

        log.info("[SEED] Dados de demonstração criados com sucesso! 3 Planos, 5 Clientes e 5 Assinaturas ativas.");
    }
}