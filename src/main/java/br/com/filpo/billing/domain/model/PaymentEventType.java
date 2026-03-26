package br.com.filpo.billing.domain.model;

public enum PaymentEventType {
    INITIATED, PROCESSING, PAID, FAILED, RETRY_SCHEDULED, REFUNDED
}