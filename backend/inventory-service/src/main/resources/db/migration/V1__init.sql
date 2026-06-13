CREATE TABLE inventory_items
(
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id         BIGINT NOT NULL UNIQUE,
    available_quantity INT    NOT NULL DEFAULT 0,
    reserved_quantity  INT    NOT NULL DEFAULT 0,
    version            BIGINT NOT NULL DEFAULT 0,
    updated_at         TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;
CREATE TABLE stock_movements
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id    BIGINT      NOT NULL,
    order_id      BIGINT,
    quantity      INT         NOT NULL,
    movement_type VARCHAR(20) NOT NULL,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;
CREATE TABLE reservations
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id   BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity   INT    NOT NULL,
    status     VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP   DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_order_product(order_id, product_id)
) ENGINE=InnoDB;