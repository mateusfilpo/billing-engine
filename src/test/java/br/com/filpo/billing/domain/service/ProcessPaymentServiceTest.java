package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.model.*;
import br.com.filpo.billing.domain.port.out.FindInvoicePort;
import br.com.filpo.billing.domain.port.out.PaymentGateway;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
import br.com.filpo.billing.domain.port.out.SavePaymentPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentServiceTest {

    @Mock
    private FindInvoicePort findInvoicePort;
    @Mock
    private SaveInvoicePort saveInvoicePort;
    @Mock
    private SavePaymentPort savePaymentPort;
    @Mock
    private PaymentGateway paymentGateway;
    @InjectMocks
    private ProcessPaymentService processPaymentService;

    @Test
    @DisplayName("Deve processar pagamento com sucesso e mudar status da fatura para PAID")
    void shouldProcessPaymentSuccessfully() {
        UUID invoiceId = UUID.randomUUID();
        Invoice invoice = Invoice.builder().id(invoiceId).status(InvoiceStatus.OPEN)
                .totalAmount(new BigDecimal("100.00")).build();

        when(findInvoicePort.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentGateway.charge(any(), anyString()))
                .thenReturn(new PaymentGatewayResponse(true, "GATEWAY-123", null));
        when(savePaymentPort.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = processPaymentService.process(invoiceId);

        assertEquals(PaymentStatus.PAID, result.getStatus());
        assertEquals("GATEWAY-123", result.getGatewayPaymentId());
        assertEquals(InvoiceStatus.PAID, invoice.getStatus());

        verify(saveInvoicePort).save(invoice);
        verify(savePaymentPort).save(any(Payment.class));
    }

    @Test
    @DisplayName("Deve registrar falha quando o gateway recusar o pagamento")
    void shouldFailWhenGatewayRejects() {
        UUID invoiceId = UUID.randomUUID();
        Invoice invoice = Invoice.builder().id(invoiceId).status(InvoiceStatus.OPEN)
                .totalAmount(new BigDecimal("100.00")).build();

        when(findInvoicePort.findById(invoiceId)).thenReturn(Optional.of(invoice));
        when(paymentGateway.charge(any(), anyString())).thenReturn(new PaymentGatewayResponse(false, null, "Recusado"));
        when(savePaymentPort.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));

        Payment result = processPaymentService.process(invoiceId);

        assertEquals(PaymentStatus.FAILED, result.getStatus());
        assertNotNull(result.getNextRetryAt());
        assertEquals(InvoiceStatus.OPEN, invoice.getStatus());

        verify(saveInvoicePort, never()).save(invoice);
        verify(savePaymentPort).save(any(Payment.class));
    }
}