-- liquibase formatted sql

-- changeset liquibase:7
-- Add indexes for performance optimization

-- Index on project slug for faster lookups (already has UNIQUE constraint which creates index)
-- Index on project status for filtering
CREATE INDEX idx_projects_status ON projects(status);

-- Index on project dates for filtering
CREATE INDEX idx_projects_dates ON projects(start_date, end_date);

-- Index on team member name for searching
CREATE INDEX idx_team_members_name ON team_members(name);

-- Composite index on translations for faster queries (already exists)
-- CREATE INDEX idx_translations_entity ON translations(entity_type, entity_id);

-- rollback DROP INDEX IF EXISTS idx_projects_status;
-- rollback DROP INDEX IF EXISTS idx_projects_dates;
-- rollback DROP INDEX IF EXISTS idx_team_members_name;

