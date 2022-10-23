package com.udemy.springcourse.services;

import com.udemy.springcourse.pojos.Person;
import com.udemy.springcourse.repositories.PeopleRepository;
import com.udemy.springcourse.security.PersonDetails;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class PersonDetailsService implements UserDetailsService {
    private final PeopleRepository peopleRepository;

    @Override
    public UserDetails loadUserByUsername(String string) throws UsernameNotFoundException {
        Optional<Person> person = peopleRepository.findByUsername(string);

        if (person.isEmpty()) {
            throw new UsernameNotFoundException("User not found!");
        }

        return new PersonDetails(person.get());
    }
}
