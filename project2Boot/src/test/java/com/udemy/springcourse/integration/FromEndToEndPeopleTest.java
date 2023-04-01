package com.udemy.springcourse.integration;

import com.udemy.springcourse.pojo.Book;
import com.udemy.springcourse.pojo.Person;
import com.udemy.springcourse.services.PeopleService;
import com.udemy.springcourse.util.H2databaseInitTest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.Random.class)
@SuppressWarnings("unchecked")
public class FromEndToEndPeopleTest extends H2databaseInitTest {
    private final MockMvc mockMvc;
    private MvcResult mvcResult;
    private ModelAndView modelAndView;

    @Autowired
    private PeopleService peopleService;

    @Autowired
    public FromEndToEndPeopleTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void showPeopleTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/people"))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("people"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/show", modelAndView.getViewName());

        List<Person> receivedPeople = (List<Person>) modelAndView.getModel().get("people");
        assertEquals(3, receivedPeople.size());
    }

    @Test
    public void showPersonWithBooksTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/people/{id}", 1))
                .andExpectAll(
                        model().size(2),
                        model().attributeExists("person"),
                        model().attributeExists("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/profile", modelAndView.getViewName());

        Person receivedPerson = (Person) modelAndView.getModel().get("person");
        assertEquals("Первый Тестовый Читатель", receivedPerson.getName());
        assertEquals(1990, receivedPerson.getYear());

        List<Book> receivedBooks = (List<Book>) modelAndView.getModel().get("books");
        assertEquals(1, receivedBooks.size());
        assertEquals("Второе название", receivedBooks.get(0).getTitle());
    }

    @Test
    public void showPersonWithoutBooksTest() throws Exception {
        mvcResult = mockMvc.perform(get("/library/people/{id}", 2))
                .andExpectAll(
                        model().size(1),
                        model().attributeExists("person"),
                        model().attributeDoesNotExist("books"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/profile", modelAndView.getViewName());

        Person receivedPerson = (Person) modelAndView.getModel().get("person");
        assertEquals("Второй Тестовый Читатель", receivedPerson.getName());
        assertEquals(1975, receivedPerson.getYear());
    }

    @Test
    public void createPersonWithNotUniqueNameTest() throws Exception {
        List<Person> peopleInBase = peopleService.findAll();
        assertEquals(3, peopleInBase.size());

        Person personToSave = new Person();
        personToSave.setName("Третий Тестовый Читатель");
        personToSave.setYear(1982);

        mvcResult = mockMvc.perform(post("/library/people")
                        .flashAttr("person", personToSave))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToSave),
                        model().attributeErrorCount("person", 1),
                        model().attributeHasFieldErrors("person", "name"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/new", modelAndView.getViewName());

        peopleInBase = peopleService.findAll();
        assertEquals(3, peopleInBase.size());
    }

    @Test
    public void createPersonWithUniqueNameTest() throws Exception {
        List<Person> peopleInBase = peopleService.findAll();
        assertEquals(3, peopleInBase.size());

        Person personToSave = new Person();
        personToSave.setName("Новый Тестовый Читатель");
        personToSave.setYear(1982);

        mockMvc.perform(post("/library/people")
                        .flashAttr("person", personToSave))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people"));

        peopleInBase = peopleService.findAll();
        assertEquals(4, peopleInBase.size());
        assertEquals("Новый Тестовый Читатель", peopleInBase.get(3).getName());
        assertEquals(1982, peopleInBase.get(3).getYear());
    }

    @Test
    public void updatePersonWithNotUniqueNewNameTest() throws Exception {
        Person personToUpdate = peopleService.findOneById(1);
        assertEquals("Первый Тестовый Читатель", personToUpdate.getName());

        personToUpdate.setName("Третий Тестовый Читатель");
        mvcResult = mockMvc.perform(patch("/library/people/{id}", personToUpdate.getId())
                        .flashAttr("person", personToUpdate))
                .andExpectAll(
                        model().size(1),
                        model().attribute("person", personToUpdate),
                        model().attributeErrorCount("person", 1),
                        model().attributeHasFieldErrors("person", "name"),
                        status().isOk())
                .andReturn();

        modelAndView = mvcResult.getModelAndView();
        assertNotNull(modelAndView);
        assertEquals("people/edit", modelAndView.getViewName());

        personToUpdate = peopleService.findOneById(1);
        assertEquals("Первый Тестовый Читатель", personToUpdate.getName());
    }

    @Test
    public void updatePersonWithNoChangedNameTest() throws Exception {
        Person personToUpdate = peopleService.findOneById(3);
        assertEquals("Третий Тестовый Читатель", personToUpdate.getName());

        personToUpdate.setYear(2002);
        mockMvc.perform(patch("/library/people/{id}", personToUpdate.getId())
                        .flashAttr("person", personToUpdate))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people"));

        personToUpdate = peopleService.findOneById(3);
        assertEquals(2002, personToUpdate.getYear());
    }

    @Test
    public void updatePersonWithUniqueNewNameTest() throws Exception {
        Person personToUpdate = peopleService.findOneById(2);
        assertEquals("Второй Тестовый Читатель", personToUpdate.getName());

        personToUpdate.setName("Новый Тестовый Читатель");
        mockMvc.perform(patch("/library/people/{id}", personToUpdate.getId())
                        .flashAttr("person", personToUpdate))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people")
                );

        personToUpdate = peopleService.findOneById(2);
        assertEquals("Новый Тестовый Читатель", personToUpdate.getName());
    }

    @Test
    public void deletePersonTest() throws Exception {
        Person personToDelete = peopleService.findOneById(3);

        List<Person> peopleInBase = peopleService.findAll();
        assertEquals(3, peopleInBase.size());
        assertTrue(peopleInBase.contains(personToDelete));

        mockMvc.perform(delete("/library/people/{id}", personToDelete.getId()))
                .andExpectAll(
                        status().is3xxRedirection(),
                        redirectedUrl("/library/people")
                );

        peopleInBase = peopleService.findAll();
        assertEquals(2, peopleInBase.size());
        assertFalse(peopleInBase.contains(personToDelete));
    }
}