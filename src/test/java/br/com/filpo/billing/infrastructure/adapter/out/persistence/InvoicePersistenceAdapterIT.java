package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import br.com.filpo.billing.TestcontainersConfiguration;
import br.com.filpo.billing.domain.model.BillingCycle;
import br.com.filpo.billing.domain.model.Customer;
import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.model.InvoiceItem;
import br.com.filpo.billing.domain.model.InvoiceStatus;
import br.com.filpo.billing.domain.model.Plan;
import br.com.filpo.billing.domain.model.Subscription;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataCustomerRepository;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataInvoiceRepository;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataPlanRepository;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.SpringDataSubscriptionRepository;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class InvoicePersistenceAdapterIT {

    @Autowired
    private InvoicePersistenceAdapter invoiceAdapter;
    @Autowired
    private CustomerPersistenceAdapter customerAdapter;
    @Autowired
    private PlanPersistenceAdapter planAdapter;
    @Autowired
    private SubscriptionPersistenceAdapter subscriptionAdapter;

    @Autowired
    private SpringDataInvoiceRepository invoiceRepository;
    @Autowired
    private SpringDataSubscriptionRepository subscriptionRepository;
    @Autowired
    private SpringDataPlanRepository planRepository;
    @Autowired
    private SpringDataCustomerRepository customerRepository;

    @AfterEach
    void tearDown() {
        invoiceRepository.deleteAll();
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve persistir uma Fatura (Aggregate Root) e seus Itens em cascata no banco de dados real")
    void shouldSaveAndFindInvoiceWithItemsInDatabase() {
        // 1. Setup completo de banco de dados
        Customer customer = customerAdapter.save(Customer.createNew("Empresa X", "x@empresa.com", "111222333"));
        Plan plan = planAdapter
                .save(Plan.createNew("Plano Pro", "Pro", new BigDecimal("500.00"), BillingCycle.MONTHLY));
        Subscription subscription = subscriptionAdapter
                .save(Subscription.createNew(customer.getId(), plan.getId(), plan.getBillingCycle()));

        // 2. Criação da Fatura e de Itens
        InvoiceItem item1 = InvoiceItem.createNew("Mensalidade Pro", new BigDecimal("500.00"), 1);
        InvoiceItem item2 = InvoiceItem.createNew("Taxa de Setup", new BigDecimal("150.00"), 1);

        Invoice invoiceToSave = Invoice.createFromSubscription(
                customer.getId(),
                subscription.getId(),
                LocalDateTime.now().plusDays(10),
                List.of(item1, item2));

        // Act - Salvar
        Invoice savedInvoice = invoiceAdapter.save(invoiceToSave);

        // Assert - Verifica se a Fatura e os Itens receberam IDs
        assertThat(savedInvoice.getId()).isNotNull();
        assertThat(savedInvoice.getItems()).hasSize(2);
        assertThat(savedInvoice.getItems().get(0).getId()).isNotNull();
        assertThat(savedInvoice.getTotalAmount()).isEqualByComparingTo(new BigDecimal("650.00"));

        // Act - Buscar por ID
        Optional<Invoice> foundInvoice = invoiceAdapter.findById(savedInvoice.getId());

        // Assert - Confirma recuperação limpa do banco
        assertThat(foundInvoice).isPresent();
        assertThat(foundInvoice.get().getStatus()).isEqualTo(InvoiceStatus.DRAFT);
        assertThat(foundInvoice.get().getItems()).hasSize(2);
        assertThat(foundInvoice.get().getItems().get(0).getAmount()).isEqualByComparingTo(new BigDecimal("500.00"));
    }
}