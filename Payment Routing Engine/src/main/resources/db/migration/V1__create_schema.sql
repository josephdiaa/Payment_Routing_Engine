CREATE TABLE users
(
    id            UUID         NOT NULL DEFAULT gen_random_uuid() primary key,
    created_at    timestamp(6) NOT NULL,
    updated_at    timestamp(6) NOT NULL,
    is_deleted    Boolean      NOT NULL default false,
    username      VARCHAR      NOT NULL UNIQUE,
    password_hash VARCHAR      NOT NULL,
    role          VARCHAR      NOT NULL,
    active        Boolean      NOT NULL
);

CREATE TABLE gateways
(
    id                     UUID           NOT NULL DEFAULT gen_random_uuid() primary key,
    created_at             timestamp(6)   NOT NULL,
    updated_at             timestamp(6)   NOT NULL,
    is_deleted             Boolean        NOT NULL default false,
    name                   VARCHAR        NOT NULL UNIQUE,
    fixed_commission       NUMERIC(19, 4) NOT NULL,
    percentage_commission  NUMERIC(19, 4) NOT NULL,
    daily_limit            NUMERIC(19, 4) NOT NULL,
    processing_time        VARCHAR        NOT NULL,
    availability_start     TIME,
    availability_end       TIME,
    available_days         VARCHAR        NOT NULL,
    min_transaction_amount NUMERIC(19, 4) NOT NULL,
    max_transaction_amount NUMERIC(19, 4),
    active                 BOOLEAN        NOT NULL
);

CREATE TABLE billers
(
    id         UUID         NOT NULL DEFAULT gen_random_uuid() primary key,
    created_at timestamp(6) NOT NULL,
    updated_at timestamp(6) NOT NULL,
    is_deleted Boolean      NOT NULL default false,
    name       VARCHAR      NOT NULL UNIQUE,
    code       VARCHAR      NOT NULL UNIQUE,
    active     BOOLEAN      NOT NULL
);

CREATE TABLE gateway_daily_usage
(
    id                UUID           NOT NULL DEFAULT gen_random_uuid() primary key,
    created_at        timestamp(6)   NOT NULL,
    updated_at        timestamp(6)   NOT NULL,
    is_deleted        Boolean        NOT NULL default false,
    usage_date        DATE           NOT NULL,
    total_amount      NUMERIC(19, 4) NOT NULL,
    transaction_count INTEGER        NOT NULL,
    gateway_id        UUID           NOT NULL,
    CONSTRAINT fk_gateway_daily_usage_gateway
        FOREIGN KEY (gateway_id)
            REFERENCES gateways (id) ON DELETE CASCADE,
    CONSTRAINT uq_gateway_usage_date UNIQUE (gateway_id, usage_date)
);

CREATE TABLE payment_transactions
(
    id                UUID           NOT NULL DEFAULT gen_random_uuid() primary key,
    created_at        timestamp(6)   NOT NULL,
    updated_at        timestamp(6)   NOT NULL,
    is_deleted        Boolean        NOT NULL default false,
    reference_number  VARCHAR        NOT NULL UNIQUE,
    amount            NUMERIC(19, 4) NOT NULL,
    commission_amount NUMERIC(19, 4) NOT NULL,
    status            VARCHAR        NOT NULL,
    processed_at      timestamp(6),
    biller_id         UUID           NOT NULL,
    CONSTRAINT fk_payment_transactions_biller
        FOREIGN KEY (biller_id)
            REFERENCES billers (id) ON DELETE CASCADE,
    gateway_id        UUID           NOT NULL,
    CONSTRAINT fk_payment_transactions_gateway
        FOREIGN KEY (gateway_id)
            REFERENCES gateways (id) ON DELETE CASCADE

);