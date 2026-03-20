CREATE TABLE registrations (
    id                  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    user_id             BIGINT REFERENCES users(id),
    registration_date   TIMESTAMP,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    total_amount        DECIMAL(10,2)
);
