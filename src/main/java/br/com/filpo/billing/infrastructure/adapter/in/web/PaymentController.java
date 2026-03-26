package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Payment;
import br.com.filpo.billing.domain.port.in.FindPaymentUseCase;
import br.com.filpo.billing.domain.port.in.ProcessPaymentUseCase;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.PaymentResponse;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.ProcessPaymentRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final ProcessPaymentUseCase processPaymentUseCase;
    private final FindPaymentUseCase findPaymentUseCase;
    private final PaymentWebMapper mapper;

    @PostMapping
    public ResponseEntity<PaymentResponse> process(@Valid @RequestBody ProcessPaymentRequest request) {
        Payment payment = processPaymentUseCase.process(request.invoiceId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(payment));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PaymentResponse> findById(@PathVariable UUID id) {
        Payment payment = findPaymentUseCase.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Pagamento não encontrado com o ID: " + id));
        return ResponseEntity.ok(mapper.toResponse(payment));
    }

    @GetMapping
    public ResponseEntity<List<PaymentResponse>> findByInvoiceId(@RequestParam UUID invoiceId) {
        List<Payment> payments = findPaymentUseCase.findByInvoiceId(invoiceId);
        return ResponseEntity.ok(mapper.toResponseList(payments));
    }
}