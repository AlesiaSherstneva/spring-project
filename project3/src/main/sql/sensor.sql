CREATE DATABASE project3;

CREATE TABLE Sensor (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(30) NOT NULL UNIQUE
);

CREATE TABLE Measurement (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    sensor_id INT REFERENCES Sensor(id) ON DELETE CASCADE,
    value FLOAT NOT NULL CHECK ( value >= -100 AND value <= 100 ),
    raining BOOLEAN NOT NULL,
    created_at TIMESTAMP
);