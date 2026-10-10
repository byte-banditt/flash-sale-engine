-- Reviewable PostgreSQL schema delta. Hibernate ddl-auto=update applies the entity
-- mappings in this demo; use versioned migrations before deploying a real sale.
-- Existing products require an audited sale-start backfill, not a guessed timestamp.
ALTER TABLE products ADD COLUMN IF NOT EXISTS sale_started_at timestamp;

CREATE TABLE IF NOT EXISTS outbox_events (
    event_id uuid PRIMARY KEY,
    order_id varchar(255) NOT NULL UNIQUE REFERENCES orders(order_id),
    event_type varchar(255) NOT NULL,
    payload jsonb NOT NULL,
    status varchar(255) NOT NULL CHECK (status IN ('PENDING','PUBLISHED','FAILED')),
    created_at timestamp NOT NULL,
    published_at timestamp
);
CREATE INDEX IF NOT EXISTS outbox_pending_created ON outbox_events(status, created_at);
-- No automatic conversion of legacy Redis-only reservations is safe. Drain the
-- old generation first; initialize the new sale explicitly in PostgreSQL.
