package br.com.filpo.billing.domain.service;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.model.InvoiceStatus;
import br.com.filpo.billing.domain.model.Payment;
import br.com.filpo.billing.domain.model.PaymentGatewayResponse;
import br.com.filpo.billing.domain.port.in.ProcessPaymentUseCase;
import br.com.filpo.billing.domain.port.out.FindInvoicePort;
import br.com.filpo.billing.domain.port.out.PaymentGateway;
import br.com.filpo.billing.domain.port.out.SaveInvoicePort;
import br.com.filpo.billing.domain.port.out.SavePaymentPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessPaymentService implements ProcessPaymentUseCase {

    private final FindInvoicePort findInvoicePort;
    private final SaveInvoicePort saveInvoicePort;
    private final SavePaymentPort savePaymentPort;
    private final PaymentGateway paymentGateway;

    @Override
    @Transactional
    public Payment process(UUID invoiceId) {
        log.info("Iniciando processamento de pagamento para a fatura: {}", invoiceId);

        // 1. Validar a fatura
        Invoice invoice = findInvoicePort.findById(invoiceId)
                .orElseThrow(() -> new EntityNotFoundException("Fatura não encontrada com o ID: " + invoiceId));

        if (invoice.getStatus() != InvoiceStatus.OPEN) {
            throw new IllegalStateException(
                    "A fatura não está aberta (OPEN) para pagamento. Status atual: " + invoice.getStatus());
        }

        // 2. Criar a intenção de pagamento no domínio
        Payment payment = Payment.createNew(invoice.getId(), invoice.getTotalAmount(), "FAKE_GATEWAY");
        payment.markAsProcessing();

        // 3. Chamar o Gateway Externo
        PaymentGatewayResponse gatewayResponse = paymentGateway.charge(payment.getAmount(),
                payment.getIdempotencyKey());

        // 4. Lidar com o resultado
        if (gatewayResponse.success()) {
            payment.markAsPaid(gatewayResponse.gatewayPaymentId());
            invoice.markAsPaid();
            saveInvoicePort.save(invoice); // Salva a fatura como paga
            log.info("Pagamento da fatura {} aprovado com sucesso!", invoiceId);
        } else {
            payment.markAsFailed(gatewayResponse.errorMessage());
            log.warn("Pagamento da fatura {} falhou: {}", invoiceId, gatewayResponse.errorMessage());
        }

        // 5. Salvar o histórico de pagamento e seus eventos gerados
        return savePaymentPort.save(payment);
    }
}