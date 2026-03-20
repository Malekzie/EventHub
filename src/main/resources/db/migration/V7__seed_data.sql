INSERT INTO categories (name) VALUES
    ('Music'),
    ('Technology'),
    ('Sports'),
    ('Arts'),
    ('Food & Drink');

INSERT INTO events (name, description, ticket_price, is_active, event_date, created_at, updated_at, category_id) VALUES
    ('Spring Tech Summit',  'Annual tech conference',         49.99, TRUE, '2026-05-15 09:00:00', NOW(), NOW(), 2),
    ('Jazz Night',          'Live jazz in the park',          15.00, TRUE, '2026-04-10 19:00:00', NOW(), NOW(), 1),
    ('City Marathon',       '5k and 10k running event',       25.00, TRUE, '2026-06-01 07:00:00', NOW(), NOW(), 3),
    ('Art Gallery Opening', 'Contemporary local artists',      0.00, TRUE, '2026-04-05 18:00:00', NOW(), NOW(), 4),
    ('Food Festival',       'International cuisine showcase', 10.00, TRUE, '2026-04-25 12:00:00', NOW(), NOW(), 5);

INSERT INTO users (email, first_name, last_name, created_at) VALUES
    ('alice@example.com', 'Alice', 'Smith',   NOW()),
    ('bob@example.com',   'Bob',   'Johnson', NOW());
