package br.com.filpo.billing.infrastructure.adapter.out.persistence;

import br.com.filpo.billing.TestcontainersConfiguration;
import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.infrastructure.adapter.out.persistence.repository.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class PaymentPersistenceAdapterIT {

    @Autowired
    private PaymentPersistenceAdapter paymentAdapter;
    @Autowired
    private InvoicePersistenceAdapter invoiceAdapter;
    @Autowired
    private SubscriptionPersistenceAdapter subscriptionAdapter;
    @Autowired
    private PlanPersistenceAdapter planAdapter;
    @Autowired
    private CustomerPersistenceAdapter customerAdapter;

    @Autowired
    private SpringDataPaymentRepository paymentRepository;
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
        paymentRepository.deleteAll();
        invoiceRepository.deleteAll();
        subscriptionRepository.deleteAll();
        planRepository.deleteAll();
        customerRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve persistir um Pagamento e seus Eventos no banco de dados")
    void shouldSaveAndFindPaymentWithEvents() {
        // Setup da cadeia de FKs
        Customer customer = customerAdapter.save(Customer.createNew("Teste", "teste@test.com", "123"));
        Plan plan = planAdapter.save(Plan.createNew("Plano", "Desc", new BigDecimal("100.00"), BillingCycle.MONTHLY));
        Subscription sub = subscriptionAdapter
                .save(Subscription.createNew(customer.getId(), plan.getId(), BillingCycle.MONTHLY));
        Invoice invoice = invoiceAdapter
                .save(Invoice.createFromSubscription(customer.getId(), sub.getId(), LocalDateTime.now(), List.of()));

        // Act
        Payment payment = Payment.createNew(invoice.getId(), new BigDecimal("100.00"), "FAKE");
        payment.markAsProcessing();
        Payment savedPayment = paymentAdapter.save(payment);

        // Assert
        assertThat(savedPayment.getId()).isNotNull();
        assertThat(savedPayment.getEvents()).hasSize(2); // INITIATED e PROCESSING

        Optional<Payment> foundPayment = paymentAdapter.findById(savedPayment.getId());
        assertThat(foundPayment).isPresent();
        assertThat(foundPayment.get().getIdempotencyKey()).isEqualTo("PAY-" + invoice.getId() + "-1");
        assertThat(foundPayment.get().getEvents().get(0).getType()).isEqualTo(PaymentEventType.INITIATED);
    }
}