package com.udemy.springcourse.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.springcourse.dto.SensorDTO;
import com.udemy.springcourse.pojos.Sensor;
import com.udemy.springcourse.repositories.SensorsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Sql({"classpath:jdbc/drop-tables.sql", "classpath:jdbc/create-tables.sql"})
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.Random.class)
public class FromEndToEndSensorsTest {
    private final MockMvc mockMvc;

    @Autowired
    private SensorsRepository sensorsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private SensorDTO testSensorDTO;

    @Autowired
    public FromEndToEndSensorsTest(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    public void setUp() {
        testSensorDTO = new SensorDTO();
    }

    @Test
    public void createEmptySensorTest() throws Exception {
        List<Sensor> sensorsInBase = sensorsRepository.findAll();
        assertEquals(1, sensorsInBase.size());

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message", containsString("Имя сенсора не должно быть пустым!")));

        sensorsInBase = sensorsRepository.findAll();
        assertEquals(1, sensorsInBase.size());
    }

    @Test
    public void createSensorWithNotValidName() throws Exception {
        List<Sensor> sensorsInBase = sensorsRepository.findAll();
        assertEquals(1, sensorsInBase.size());

        // create sensor with name contains less than 3 characters
        testSensorDTO.setName("s");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Имя сенсора должно содержать от 3 до 30 символов")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));

        // create sensor with name contains more than 30 characters
        testSensorDTO.setName("sensor".repeat(6));

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Имя сенсора должно содержать от 3 до 30 символов")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));

        sensorsInBase = sensorsRepository.findAll();
        assertEquals(1, sensorsInBase.size());
    }

    @Test
    public void createSensorWithNotUniqueNameTest() throws Exception {
        List<Sensor> sensorsInBase = sensorsRepository.findAll();
        assertEquals(1, sensorsInBase.size());

        testSensorDTO.setName("Test sensor");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Такой сенсор уже зарегистрирован!")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));

        sensorsInBase = sensorsRepository.findAll();
        assertEquals(1, sensorsInBase.size());
    }

    @Test
    public void createValidSensorTest() throws Exception {
        List<Sensor> sensorsInBase = sensorsRepository.findAll();
        assertEquals(1, sensorsInBase.size());

        testSensorDTO.setName("A valid sensor");

        mockMvc.perform(MockMvcRequestBuilders.post("/sensors/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testSensorDTO)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON));

        sensorsInBase = sensorsRepository.findAll();
        assertEquals(2, sensorsInBase.size());
    }
}