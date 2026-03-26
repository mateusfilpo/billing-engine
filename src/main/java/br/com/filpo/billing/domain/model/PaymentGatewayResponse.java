package br.com.filpo.billing.domain.model;

public record PaymentGatewayResponse(
        boolean success,
        String gatewayPaymentId,
        String errorMessage) {
}