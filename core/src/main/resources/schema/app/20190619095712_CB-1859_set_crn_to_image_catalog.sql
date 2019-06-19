-- // CB-1859 set crn to image catalog
-- Migration SQL that makes the change goes here.
ALTER TABLE imagecatalog ADD COLUMN IF NOT EXISTS resourcecrn VARCHAR(255);
ALTER TABLE imagecatalog ADD COLUMN IF NOT EXISTS creator VARCHAR(255);


-- //@UNDO
-- SQL to undo the change goes here.
ALTER TABLE imagecatalog DROP COLUMN IF EXISTS resourcecrn;
ALTER TABLE imagecatalog DROP COLUMN IF EXISTS creator;
