-- // CB-1859 change recipe id to crn
-- Migration SQL that makes the change goes here.
ALTER TABLE recipe ADD COLUMN IF NOT EXISTS resourcecrn VARCHAR(255);
ALTER TABLE recipe ADD COLUMN IF NOT EXISTS creator VARCHAR(255);

-- //@UNDO
-- SQL to undo the change goes here.
ALTER TABLE recipe DROP COLUMN IF EXISTS resourcecrn;
ALTER TABLE recipe DROP COLUMN IF EXISTS creator;
