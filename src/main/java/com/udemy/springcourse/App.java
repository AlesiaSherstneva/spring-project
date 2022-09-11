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

            Person person = session.get(Person.class, 2);
            Item item = new Item("Item from Hibernate", person);
            person.getItems().add(item);

            session.save(item);

            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
