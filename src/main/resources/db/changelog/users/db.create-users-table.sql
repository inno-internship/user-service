CREATE TABLE users (
  id UUID PRIMARY KEY NOT NULL,
  name VARCHAR(100) NOT NULL,
  surname VARCHAR(100) NOT NULL,
  birth_date DATE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL
);