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

            Person person = session.get(Person.class, 2);
            session.delete(person);

            session.getTransaction().commit();
        }
    }
}
