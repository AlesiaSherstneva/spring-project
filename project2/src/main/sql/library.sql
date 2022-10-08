CREATE DATABASE project2;

CREATE TABLE Person (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(250) UNIQUE,
    year INT NOT NULL CHECK ( year > 1900 AND year <= EXTRACT(year FROM now()))
);

INSERT INTO person (name, year)
VALUES ('Иванов Иван Иванович', 1970),
       ('Петров Пётр Петрович', 1960),
       ('Алексеев Алексей Алексеевич', 1989),
       ('Познер Владимир Владимирович', 1944),
       ('Фёдоров Мирон Янович', 1985);

CREATE TABLE book (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    person_id INT REFERENCES Person(id) ON DELETE SET NULL,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(250) NOT NULL,
    year INT NOT NULL CHECK ( year > 1445 AND year <= EXTRACT(year FROM now()))
    );

INSERT INTO book (title, author, year)
VALUES ('Над пропастью во ржи', 'Джером Сэлинджер', 1951),
       ('День опричника', 'Владимир Сорокин', 2006),
       ('Тайные виды на гору Фудзи', 'Владимир Пелевин', 2018),
       ('Философия Java', 'Брюс Эккель', 2018),
       ('Психопатология обыденной жизни', 'Зигмунд Фрейд', 1904),
       ('Игра в бисер', 'Герман Гессе', 1943),
       ('Бытие и время', 'Мартин Хайдеггер', 1927);