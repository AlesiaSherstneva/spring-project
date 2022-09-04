package com.udemy.springcourse;

import com.udemy.springcourse.pojo.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class App {
    public static void main(String[] args) {
        Configuration configuration = new Configuration().addAnnotatedClass(Person.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();


        try (sessionFactory) {
            session.beginTransaction();

            List<Person> people = session.createQuery("FROM Person WHERE name LIKE 'T%'").getResultList();
            for(Person person: people) {
                System.out.println(person);
            }

            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
