package com.udemy.springcourse;

import com.udemy.springcourse.pojo.Item;
import com.udemy.springcourse.pojo.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.ArrayList;
import java.util.Collections;

public class App {
    public static void main(String[] args) {
        Configuration configuration = new Configuration()
                .addAnnotatedClass(Person.class)
                .addAnnotatedClass(Item.class);
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();


        try {
            session.beginTransaction();

            Person person = new Person("Test name", 30);
            Item item = new Item("Item from Hibernate 2", person);

            person.setItems(new ArrayList<>(Collections.singletonList(item)));

            session.save(person);
            session.save(item);

            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }
    }
}
