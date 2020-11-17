-- TODO: CREATE TYPE ROLE AS ENUM ('ADMIN', 'EMPLOYEE', 'CUSTOMER');

CREATE TABLE IF NOT EXISTS APPLICATION_USER
(
    id         UUID PRIMARY KEY    NOT NULL,
    created_at DATE                NOT NULL,
    updated_at DATE,
    user_name  VARCHAR(100) UNIQUE NOT NULL,
    password   VARCHAR(100)        NOT NULL,
    role       VARCHAR(10)         NOT NULL
        CHECK (role = 'ADMIN' OR
               role = 'EMPLOYEE' OR
               role = 'CUSTOMER'),
    is_enabled BOOL                NOT NULL
);

CREATE TABLE IF NOT EXISTS CATEGORY
(
    id           UUID PRIMARY KEY    NOT NULL,
    created_at   DATE                NOT NULL,
    updated_at   DATE,
    name         VARCHAR(100) UNIQUE NOT NULL,
    pic_location VARCHAR(500)        NOT NULL
);

CREATE TABLE IF NOT EXISTS PRODUCT
(
    id           UUID PRIMARY KEY    NOT NULL,
    created_at   DATE                NOT NULL,
    updated_at   DATE,
    category_id  UUID                NOT NULL,
    name         VARCHAR(100)  NOT NULL,
    pic_location VARCHAR(500)        NOT NULL,
    price        VARCHAR(20)         NOT NULL,
    old_price    VARCHAR(20),
    description  VARCHAR(500),
    out_of_stock BOOLEAN,
    sale         BOOLEAN,
    recommended  BOOLEAN,
    FOREIGN KEY (category_id) REFERENCES CATEGORY (id)
);

CREATE TYPE order_state AS ENUM ('RECEIVED', 'IN_PROGRESS', 'SHIPPED', 'DELIVERED' , 'COMPLETED');

CREATE TABLE IF NOT EXISTS ORDER3D
(
    id             UUID PRIMARY KEY NOT NULL,
    created_at     DATE             NOT NULL,
    updated_at     DATE,
    total_amount   DOUBLE PRECISION NOT NULL,
    order_track_id VARCHAR(20)      NOT NULL,
    order_state    order_state      NOT NULL,
    company_name   VARCHAR(100),
    name           VARCHAR(100)     NOT NULL,
    city           VARCHAR(100)     NOT NULL,
    district       VARCHAR(100)     NOT NULL,
    district2      VARCHAR(100),
    mobile_number  VARCHAR(20)      NOT NULL,
    email          VARCHAR(200),
    notes          VARCHAR(500)
);

CREATE TABLE order3d_products
(
    orders_id   UUID REFERENCES ORDER3D (id) ON UPDATE CASCADE ON DELETE CASCADE,
    products_id UUID REFERENCES PRODUCT (id) ON UPDATE CASCADE,
    count       numeric          NOT NULL DEFAULT 1,
    amount      DOUBLE PRECISION NOT NULL,
    CONSTRAINT order_product_pkey PRIMARY KEY (orders_id, products_id) -- explicit pk
);
