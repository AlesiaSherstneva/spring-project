package com.udemy.springcourse;

import com.udemy.springcourse.pojo.Item;
import com.udemy.springcourse.pojo.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class App {
    public static void main(String[] args) {
        Configuration configuration = new Configuration()
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Item.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();


        try {
            session.beginTransaction();

            Item item = session.get(Item.class, 5);
            System.out.println(item);

            Person person = item.getOwner();
            System.out.println(person);

            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
