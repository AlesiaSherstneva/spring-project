CREATE TABLE IF NOT EXISTS Person (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(250) UNIQUE,
    year INT NOT NULL CHECK (year > 1900 AND year <= EXTRACT(year FROM now()))
);