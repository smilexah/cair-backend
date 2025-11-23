-- Create roles table
CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

-- Insert default roles
INSERT INTO roles (name) VALUES ('ADMIN'), ('USER');
