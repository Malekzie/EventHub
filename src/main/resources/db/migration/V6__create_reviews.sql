CREATE TABLE reviews (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id    BIGINT NOT NULL REFERENCES events(id),
    user_id     BIGINT REFERENCES users(id),
    rating      INT  NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment     VARCHAR(1000),
    created_at  TIMESTAMP
);
