CREATE TABLE payments (
    id                  UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
    invoice_id          UUID NOT NULL REFERENCES invoices(id),
    idempotency_key     VARCHAR(100) NOT NULL UNIQUE,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING' 
                        CHECK (status IN ('PENDING','PROCESSING','PAID','FAILED','REFUNDED')),
    amount              NUMERIC(10,2) NOT NULL CHECK (amount > 0),
    gateway             VARCHAR(50) NOT NULL,
    gateway_payment_id  VARCHAR(255),
    attempt_number      INTEGER NOT NULL DEFAULT 1 CHECK (attempt_number > 0),
    next_retry_at       TIMESTAMP,
    created_at          TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE TABLE payment_events (
    id          UUID PRIMARY KEY DEFAULT uuid_generate_v7(),
    payment_id  UUID NOT NULL REFERENCES payments(id),
    type        VARCHAR(50) NOT NULL 
                CHECK (type IN ('INITIATED','PROCESSING','PAID','FAILED','RETRY_SCHEDULED','REFUNDED')),
    description TEXT,
    metadata    TEXT, 
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);

-- Índices
CREATE INDEX idx_payments_invoice ON payments(invoice_id);
CREATE INDEX idx_payments_next_retry ON payments(next_retry_at) 
    WHERE status = 'FAILED' AND next_retry_at IS NOT NULL;
CREATE INDEX idx_payment_events_payment ON payment_events(payment_id);