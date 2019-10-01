package com.learnwebclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnwebclient.dto.Employee;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import javax.management.relation.Role;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class EmployeeRestClientUnitTest {

    @Rule
    public final MockWebServer mockBackEnd = new MockWebServer();

    EmployeeRestClient employeeRestClient;
    WebClient webClient;

    @BeforeEach
    public void setUp(){

        final String baseUrl=String.format("http://localhost:%s", mockBackEnd.getPort());
        webClient = WebClient.create(baseUrl);
        employeeRestClient = new EmployeeRestClient(webClient);

    }

    @Test
    void retrieveEmployeeById() throws IOException {

        //given
        Integer employeeId = 1;
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/employee.json")));
        mockBackEnd.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        //when
        Employee employee = employeeRestClient.retrieveEmployeeById(employeeId);

        //then
        assertEquals("Chris", employee.getFirstName());
    }
}
