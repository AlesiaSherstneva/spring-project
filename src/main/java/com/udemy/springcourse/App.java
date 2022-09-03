package com.udemy.springcourse;

import com.udemy.springcourse.pojo.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {
        Configuration configuration = new Configuration().addAnnotatedClass(Person.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();


        try (sessionFactory) {
            session.beginTransaction();

            session.save(new Person("Tom", 20));
            session.save(new Person("Test1", 30));
            session.save(new Person("Mike", 35));
            session.save(new Person("John", 50));
            session.save(new Person("Katy", 18));

            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
