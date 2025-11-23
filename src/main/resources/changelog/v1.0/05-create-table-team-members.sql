-- liquibase formatted sql

-- changeset liquibase:5
CREATE TABLE team_members (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    image VARCHAR(500),
    expertise TEXT NOT NULL,
    email VARCHAR(255),
    linkedin VARCHAR(255),
    github VARCHAR(255),
    scholar VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- rollback DROP TABLE team_members;

