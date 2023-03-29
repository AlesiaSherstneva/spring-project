package com.udemy.springcourse.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.springcourse.dto.MeasurementDTO;
import com.udemy.springcourse.dto.SensorDTO;
import com.udemy.springcourse.pojos.Measurement;
import com.udemy.springcourse.services.MeasurementsService;
import com.udemy.springcourse.services.SensorsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.modelmapper.ModelMapper;
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
import java.util.Random;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Sql({"classpath:jdbc/drop-tables.sql", "classpath:jdbc/create-tables.sql"})
@TestPropertySource("classpath:application-test.properties")
@TestMethodOrder(MethodOrderer.Random.class)
public class FromEndToEndMeasurementsTest {
    private final MockMvc mockMvc;
    private final Random random;

    @Autowired
    private MeasurementsService measurementsService;

    @Autowired
    private SensorsService sensorsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private MeasurementDTO testMeasurementDTO;

    @Autowired
    public FromEndToEndMeasurementsTest(WebApplicationContext webApplicationContext) {
        random = new Random();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    public void setUp() {
        testMeasurementDTO = new MeasurementDTO();
        testMeasurementDTO.setValue(random.nextDouble() * 200 - 100);
        testMeasurementDTO.setRaining(random.nextBoolean());
    }

    @Test
    public void getMeasurementsTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/measurements"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$[0].id").doesNotExist(),
                        jsonPath("$[0].value", is(25.35)),
                        jsonPath("$[0].raining", is(true)),
                        jsonPath("$[0].sensor.name", is("Test sensor")),
                        jsonPath("$[0].created_at").doesNotExist(),
                        jsonPath("$[1].id").doesNotExist(),
                        jsonPath("$[1].value", is(-18.84)),
                        jsonPath("$[1].raining", is(false)),
                        jsonPath("$[1].sensor.name", is("Test sensor")),
                        jsonPath("$[1].created_at").doesNotExist());
    }

    @Test
    public void showRainyDaysTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/measurements/rainyDaysCount"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", is(1)));
    }

    @Test
    public void createMeasurementWithWrongSensorTest() throws Exception {
        List<Measurement> measurementsInBase = measurementsService.findAll();
        assertEquals(2, measurementsInBase.size());

        SensorDTO wrongSensorDTO = new SensorDTO();
        wrongSensorDTO.setName("Wrong sensor");

        testMeasurementDTO.setSensor(wrongSensorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message", containsString("Такой сенсор не зарегистрирован!")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));

        measurementsInBase = measurementsService.findAll();
        assertEquals(2, measurementsInBase.size());
    }

    @Test
    public void createValidMeasurementTest() throws Exception {
        List<Measurement> measurementsInBase = measurementsService.findAll();
        assertEquals(2, measurementsInBase.size());

        SensorDTO validSensorDTO = modelMapper.map(sensorsService.findOneByName("Test sensor"), SensorDTO.class);
        testMeasurementDTO.setSensor(validSensorDTO);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        measurementsInBase = measurementsService.findAll();
        assertEquals(3, measurementsInBase.size());
    }
}