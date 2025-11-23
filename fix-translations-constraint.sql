-- Manual fix for translations_field_name_check constraint
-- Run this SQL directly in your PostgreSQL database if the migration doesn't work

-- Check if constraint exists
SELECT conname, contype, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'translations'::regclass AND conname = 'translations_field_name_check';

-- Drop the constraint
ALTER TABLE translations DROP CONSTRAINT IF EXISTS translations_field_name_check;

-- Verify it's gone
SELECT conname, contype, pg_get_constraintdef(oid)
FROM pg_constraint
WHERE conrelid = 'translations'::regclass;

