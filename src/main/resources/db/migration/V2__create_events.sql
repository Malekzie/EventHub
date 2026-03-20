CREATE TABLE events (
    id            BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name          VARCHAR(255)   NOT NULL,
    description   VARCHAR(2000),
    ticket_price  DECIMAL(10,2),
    is_active     BOOLEAN        DEFAULT TRUE,
    event_date    TIMESTAMP,
    created_at    TIMESTAMP,
    updated_at    TIMESTAMP,
    category_id   BIGINT REFERENCES categories(id)
);
