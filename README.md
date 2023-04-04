# spring-project

Я прошла курс ["Spring - Полный курс. Boot, Hibernate, Security, REST"](https://www.udemy.com/course/spring-alishev/), 
преподаватель Наиль Алишев, платформа Udemy.com. Процесс прохождения размещался в этом репозитории.

В ходе обучения были выполнены три учебных проекта.

> **Warning**  
> Для тех, кто тоже проходит этот курс и копирует себе мой репозиторий. Предупреждаю: в первом и втором проекте у меня 
не такая реализация деплоя, как у преподавателя. Я не запускаю Tomcat из-под среды разработки, а устанавливаю war-файл
на Tomcat в своей файловой системе. Поэтому без скачивания Tomcat и настройки прав доступа у вас мои проекты не
запустятся. Учитывайте это, редактируйте pom.xml или гуглите настройку и запуск Tomcat.

**Update:** на основании курса 
["Spring Boot Unit Testing with JUnit, Mockito and MockMVC"](https://www.udemy.com/course/spring-boot-unit-testing/)
я покрыла все проекты юнит- и интеграционными тестами. Поскольку на курсе меня научили тестировать только 
SpringBoot-приложения, с другими конфигурациями пришлось повозиться, много гуглить. 

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

Отчёт о покрытии тестами:

![project1-jacoco.png](project1/jacoco.png)

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

Отчёт о покрытии тестами:

![project2-jacoco.png](project2/jacoco.png)

### project2.5

В папке project2Boot находится проект №2, переписанный на Spring Boot.

Приложение собирается в jar-файл, запускается из метода main, разворачивается в контекстном пути 
http://localhost:8080/library

Также возможен запуск из командной строки. Файл project2Boot.jar в папке target: java -jar project2Boot.jar

Отчёт о покрытии тестами:

![project2.5-jacoco.png](project2Boot/jacoco.png)

## project3

![project3-task.jpg](project3/img.jpg)

---

Функционал реализован в полном объёме.

RestClient я реализовала не так, как это сделал преподаватель. В его реализации для парсинга полученных показаний 
используются такие же DTO, как и в RestAPI. У меня же реализован парсинг из строки с помощью регулярного выражения.

Приложение собирается в jar-файл, запускается из метода main.

Использованные технологии: Java 11 SE, Apache Maven, Apache Tomcat, PostgreSQL, Hibernate, Spring Core, Spring MVC,
Spring Data, Spring Boot, Spring Web, Lombok, XChart.

---

С картинками в этом проекте сложно, так как UI не реализовывался. Не снимать же скриншоты Postman :smile:! Лучшее,
что я смогла придумать, это показать, как из пустых, только что созданных таблиц в базе данных RestAPI через 
запуск RestClient создаётся график тысячи случайных температур.

![project3-img1.gif](project3/img1.gif)

Отчёт о покрытии тестами:

![project3-jacoco.png](project3/jacoco.png)