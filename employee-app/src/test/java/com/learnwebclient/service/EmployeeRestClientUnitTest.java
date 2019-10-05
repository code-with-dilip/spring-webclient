package com.learnwebclient.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnwebclient.dto.Employee;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.Rule;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import javax.management.relation.Role;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
    void retrieveAllEmployees() throws IOException {

        //given
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/allemployees.json")));
        mockBackEnd.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        //when
        List<Employee> employees = employeeRestClient.retrieveAllEmployees();

        //then
        assertEquals(2, employees.size());
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


    @Test
    void retrieveEmployeeByName() throws IOException {

        //given
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/employee.json")));
        mockBackEnd.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));

        //when
        List<Employee> employees =employeeRestClient.retrieveEmployeeByName("Chris");

        //then
        assertEquals(1, employees.size());
    }

    @Test
    void addNewEmployee() throws IOException {

        //given
        Employee employee = new Employee(null,"Iron", "Man", 54, "male", "Architect");
        String body = new String(Files.readAllBytes(Paths.get("src/test/resources/new-employee.json")));
        mockBackEnd.enqueue(new MockResponse()
                .setBody(body)
                .addHeader("Content-Type", "application/json"));


        Employee employee1 = employeeRestClient.addNewEmployee(employee);
        assertTrue(employee1.getId()!=null);

    }

    @Test
    void addNewEmployee_BadRequest(){
        //given
        Employee employee = new Employee(null,null, "Man", 54, "male", "Architect");
        mockBackEnd.enqueue(new MockResponse()
                .setResponseCode(400)
                .setBody("Please pass all the input fields : [firstName]")
                .addHeader("Content-Type", "application/json"));

        //then
        String expectedErrorMessage = "Please pass all the input fields : [firstName]";
        Assertions.assertThrows(WebClientResponseException.class, () ->  employeeRestClient.addNewEmployee(employee), expectedErrorMessage);

    }

    @Test
    void updateEmployee(){

        //given
        int employeeId = 2;
        Employee employee = new Employee(null,"Chris1", null, null, null, null);

        //when
        Employee updatedEmployee = employeeRestClient.updateEmployee(employeeId, employee);

        //then
        assertEquals("Chris1", updatedEmployee.getFirstName());

    }



}
