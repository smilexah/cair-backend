-- liquibase formatted sql

-- changeset liquibase:4
CREATE TABLE translations (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT NOT NULL,
    field_name VARCHAR(50) NOT NULL,
    language_code VARCHAR(10) NOT NULL,
    value TEXT NOT NULL,
    CONSTRAINT uq_translation UNIQUE (entity_type, entity_id, field_name, language_code)
);

CREATE INDEX idx_translations_entity ON translations(entity_type, entity_id);
CREATE INDEX idx_translations_language ON translations(language_code);

-- rollback DROP TABLE translations;

