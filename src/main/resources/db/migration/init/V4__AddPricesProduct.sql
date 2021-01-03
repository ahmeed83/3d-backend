ALTER TABLE PRODUCT
    ADD COLUMN price_assemble VARCHAR(20), 
    ADD COLUMN only_shop_available BOOLEAN DEFAULT false;