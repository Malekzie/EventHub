CREATE TABLE registration_items (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    registration_id  BIGINT NOT NULL REFERENCES registrations(id),
    event_id         BIGINT NOT NULL REFERENCES events(id),
    quantity         INT           NOT NULL,
    unit_price       DECIMAL(10,2) NOT NULL,
    subtotal         DECIMAL(10,2) NOT NULL
);
