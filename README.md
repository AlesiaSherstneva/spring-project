# spring-project

Я прохожу курс ["Spring - Полный курс. Boot, Hibernate, Security, REST"](https://www.udemy.com/course/spring-alishev/), 
преподаватель Наиль Алишев, платформа Udemy.com. Процесс и результаты буду размещать в этом репозитории.

Вообще, конечно, все эти темы я проходила на курсе Java Enterprise Development в IT-Academy, и в выпускном проекте
их применяла. Но это было "галопом по Европам", на каждую технологию одно-два занятия. Сейчас хочу разобраться в 
Spring подробно и не спеша. Повторить старое и, вероятно, узнать что-то новое.

В ходе обучения заявлены три учебных проекта. По мере выполнения буду их выкладывать.

## project1

![project1-task.jpg](project1/img.jpg)

---

Функционал реализован в полном объёме.

Приложение устанавливается на Tomcat-9, разворачивается в контекстном пути http://localhost:8080/library

Использованные технологии: Java 11 SE, Apache Maven, Apache Tomcat, JDBC, PostgreSQL, Spring Core, Spring MVC, Lombok,
Thymeleaf, HTML, CSS.

---

Титульные страницы:

![project1-img1.gif](project1/img1.gif)

Создание и редактирование читателя (с валидацией):

![project1-img2.gif](project1/img2.gif)

Создание и редактирование книги (с валидацией):

![project1-img3.gif](project1/img3.gif)

Профиль читателя, профиль книги, освобождение книги при удалении читателя:

![project1-img4.gif](project1/img4.gif)

## project2

![project2-task.jpg](project2/img.jpg)

---

Функционал реализован в полном объёме.

Приложение устанавливается на Tomcat-9, разворачивается в контекстном пути http://localhost:8080/library

Использованные технологии: Java 11 SE, Apache Maven, Apache Tomcat, PostgreSQL, Hibernate, Spring Core, Spring MVC,
Spring Data, Lombok, Thymeleaf, HTML, CSS.

---

Основной CRUD-функционал приложения не изменился.

Чтобы не вводить вручную параметры в адресную строку, я добавила в представление для книг форму ввода параметров 
сортировки/паджинации:

![project2-img1.gif](project2/img1.gif)

Страница поиска книг:

![project2-img2.gif](project2/img2.gif)

Если читатель взял книгу более 10 дней назад, книга считается просроченной и подсвечивается на его странице красным
цветом. Для демонстрации решения я воспользовалась SQL-запросом, вручную заменив дату взятия книги. Видео записывалось
15 октября 2022 года.

![project2-img3.gif](project2/img3.gif)

### project2.5

В папке project2Boot находится проект №2, переписанный на Spring Boot.

Приложение собирается в jar-файл, запускается из метода main, разворачивается в контекстном пути 
http://localhost:8080/library

Также возможен запуск из командной строки. Файл project2Boot.jar в папке target: java -jar project2Boot.jar 

