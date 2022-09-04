package com.udemy.springcourse;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class App {
    public static void main(String[] args) {
        Configuration configuration = new Configuration();
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();


        try (sessionFactory) {
            session.beginTransaction();



            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
