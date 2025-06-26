CREATE TABLE card_info (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    number VARCHAR(16) NOT NULL,
    holder VARCHAR(100) NOT NULL,
    expiration_date DATE NOT NULL
);
