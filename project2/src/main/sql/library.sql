CREATE DATABASE project2;

CREATE TABLE Person (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(250) UNIQUE,
    year INT NOT NULL CHECK ( year > 1900 AND year <= EXTRACT(year FROM now()))
);

INSERT INTO Person (name, year)
VALUES ('Иванов Иван Иванович', 1970),
       ('Петров Пётр Петрович', 1960),
       ('Алексеев Алексей Алексеевич', 1989),
       ('Познер Владимир Владимирович', 1944),
       ('Фёдоров Мирон Янович', 1985);

CREATE TABLE Book (
    id INT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    person_id INT REFERENCES Person(id) ON DELETE SET NULL,
    title VARCHAR(100) NOT NULL,
    author VARCHAR(250) NOT NULL,
    year INT NOT NULL CHECK ( year > 1445 AND year <= EXTRACT(year FROM now())),
    taken_at TIMESTAMP
);

INSERT INTO Book (title, author, year)
VALUES ('Над пропастью во ржи', 'Джером Сэлинджер', 1951),
       ('День опричника', 'Владимир Сорокин', 2006),
       ('Тайные виды на гору Фудзи', 'Владимир Пелевин', 2018),
       ('Философия Java', 'Брюс Эккель', 2018),
       ('Игра в бисер', 'Герман Гессе', 1943),
       ('Бытие и время', 'Мартин Хайдеггер', 1927),
       ('Психопатология обыденной жизни', 'Зигмунд Фрейд', 1904),
-- вторую книгу по психологии я добавила для тестирования поиска
       ('Психология влияния', 'Роберт Чалдини', 2022);