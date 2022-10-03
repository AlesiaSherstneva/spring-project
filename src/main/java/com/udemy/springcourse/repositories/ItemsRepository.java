package com.udemy.springcourse.repositories;

import com.udemy.springcourse.pojo.Item;
import com.udemy.springcourse.pojo.Person;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemsRepository {
    List<Item> findByItemName(String itemName);

    List<Item> findByOwner(Person owner);
}
