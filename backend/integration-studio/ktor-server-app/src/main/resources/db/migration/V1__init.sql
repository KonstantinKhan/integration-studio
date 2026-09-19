-- Creates the initial schema. Fresh databases run this migration; databases
-- that already had the tables (created by the former SchemaUtils DDL) are
-- baselined at version 1 and skip it.

CREATE TABLE IF NOT EXISTS migration_runs (
    id                  UUID PRIMARY KEY,
    started_at          TIMESTAMPTZ NOT NULL,
    status              VARCHAR(20) NOT NULL,
    total_count         INTEGER,
    created_at          TIMESTAMPTZ NOT NULL,
    type                VARCHAR(10),
    sending_started_at  TIMESTAMPTZ,
    from_date           TIMESTAMPTZ,
    to_date             TIMESTAMPTZ,
    error_type          VARCHAR(60),
    started_by          VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS migration_events (
    id                UUID PRIMARY KEY,
    run_id            UUID NOT NULL REFERENCES migration_runs(id),
    name              TEXT NOT NULL,
    type_id           INTEGER NOT NULL,
    object_id         INTEGER NOT NULL,
    payload           JSONB NOT NULL,
    status            VARCHAR(20) NOT NULL,
    status_updated_at TIMESTAMPTZ NOT NULL,
    created_at        TIMESTAMPTZ NOT NULL
);
