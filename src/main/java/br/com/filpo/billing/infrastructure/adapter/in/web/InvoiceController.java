package br.com.filpo.billing.infrastructure.adapter.in.web;

import br.com.filpo.billing.domain.exception.EntityNotFoundException;
import br.com.filpo.billing.domain.model.Invoice;
import br.com.filpo.billing.domain.port.in.FindInvoiceUseCase;
import br.com.filpo.billing.domain.port.in.GenerateInvoiceUseCase;
import br.com.filpo.billing.domain.port.in.UpdateInvoiceStatusUseCase;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.GenerateInvoiceRequest;
import br.com.filpo.billing.infrastructure.adapter.in.web.dto.InvoiceResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final GenerateInvoiceUseCase generateInvoiceUseCase;
    private final FindInvoiceUseCase findInvoiceUseCase;
    private final UpdateInvoiceStatusUseCase updateInvoiceStatusUseCase;
    private final InvoiceWebMapper mapper;

    @PostMapping
    public ResponseEntity<InvoiceResponse> generate(@Valid @RequestBody GenerateInvoiceRequest request) {
        Invoice generatedInvoice = generateInvoiceUseCase.generate(request.subscriptionId());
        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponse(generatedInvoice));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InvoiceResponse> findById(@PathVariable UUID id) {
        Invoice invoice = findInvoiceUseCase.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Fatura não encontrada com o ID: " + id));
        return ResponseEntity.ok(mapper.toResponse(invoice));
    }

    @GetMapping
    public ResponseEntity<List<InvoiceResponse>> findByCustomerId(@RequestParam UUID customerId) {
        List<Invoice> invoices = findInvoiceUseCase.findByCustomerId(customerId);
        return ResponseEntity.ok(mapper.toResponseList(invoices));
    }

    @PostMapping("/{id}/open")
    public ResponseEntity<InvoiceResponse> openInvoice(@PathVariable UUID id) {
        Invoice openedInvoice = updateInvoiceStatusUseCase.open(id);
        return ResponseEntity.ok(mapper.toResponse(openedInvoice));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<InvoiceResponse> payInvoice(@PathVariable UUID id) {
        Invoice paidInvoice = updateInvoiceStatusUseCase.markAsPaid(id);
        return ResponseEntity.ok(mapper.toResponse(paidInvoice));
    }
}