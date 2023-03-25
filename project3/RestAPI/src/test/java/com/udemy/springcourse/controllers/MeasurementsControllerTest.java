package com.udemy.springcourse.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.udemy.springcourse.dto.MeasurementDTO;
import com.udemy.springcourse.pojos.Measurement;
import com.udemy.springcourse.pojos.Sensor;
import com.udemy.springcourse.services.MeasurementsService;
import com.udemy.springcourse.services.SensorsService;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatchers;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.Random.class)
class MeasurementsControllerTest {
    private final MockMvc mockMvc;
    private final Random random;

    @MockBean
    private SensorsService sensorsService;

    @MockBean
    private MeasurementsService measurementsService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ObjectMapper objectMapper;

    private Measurement testMeasurement;
    private MeasurementDTO testMeasurementDTO;

    @Autowired
    public MeasurementsControllerTest(WebApplicationContext webApplicationContext) {
        random = new Random();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @BeforeEach
    public void setUp() {
        Sensor sensor = new Sensor();
        sensor.setName("Test sensor");

        testMeasurement = new Measurement();
        testMeasurement.setId(random.nextInt(100));
        testMeasurement.setValue(random.nextDouble());
        testMeasurement.setRaining(random.nextBoolean());
        testMeasurement.setSensor(sensor);
        testMeasurement.setCreatedAt(LocalDateTime.now());

        testMeasurementDTO = modelMapper.map(testMeasurement, MeasurementDTO.class);
    }

    @Test
    public void getMeasurementsTest() throws Exception {
        List<Measurement> receivedMeasurements = new ArrayList<>(Collections.singletonList(testMeasurement));
        when(measurementsService.findAll()).thenReturn(receivedMeasurements);

        mockMvc.perform(MockMvcRequestBuilders.get("/measurements"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$[0].id").doesNotExist(),
                        jsonPath("$[0].value", is(testMeasurementDTO.getValue())),
                        jsonPath("$[0].raining", is(testMeasurementDTO.getRaining())),
                        jsonPath("$[0].sensor.name", is(testMeasurementDTO.getSensor().getName())),
                        jsonPath("$[0].created_at").doesNotExist());

        verify(measurementsService, times(1)).findAll();
    }

    @Test
    public void showRainyDaysTest() throws Exception {
        long rainyDays = Math.abs(random.nextLong());
        when(measurementsService.countRainyDays()).thenReturn(rainyDays);

        mockMvc.perform(MockMvcRequestBuilders.get("/measurements/rainyDaysCount"))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$", is(rainyDays)));

        verify(measurementsService, times(1)).countRainyDays();
    }

    @Test
    public void createMeasurementWithWrongValueTest() throws Exception {
        // set value greater than 100
        testMeasurementDTO.setValue((long) random.nextInt(20_000) + 101);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Значение не может быть больше, чем 100")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));

        // set value smaller than -100
        testMeasurementDTO.setValue(testMeasurementDTO.getValue() * (-1));

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Значение не может быть меньше, чем -100")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));
    }

    @Test
    public void createMeasurementWithWrongRainingTest() throws Exception {
        testMeasurementDTO.setRaining(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Значение должно быть true или false")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));
    }

    @Test
    public void createMeasurementWithWrongSensorTest() throws Exception {
        testMeasurementDTO.setSensor(null);

        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().is4xxClientError(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        jsonPath("$.message",
                                containsString("Должен быть указан сенсор!")),
                        jsonPath("$.timestamp", instanceOf(Long.class)));
    }

    @Test
    public void createValidMeasurementTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/measurements/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testMeasurementDTO)))
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );

        verify(sensorsService, times(1)).findOneByNameOrElseThrowException(anyString());
        verify(measurementsService, times(1)).save(ArgumentMatchers.any(Measurement.class));
    }

    @AfterEach
    public void tearDown() {
        verifyNoMoreInteractions(sensorsService, measurementsService);
    }
}