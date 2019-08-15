package com.employeeservice.controller;


import com.employeeservice.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;
import java.util.stream.Collectors;

import static com.employeeservice.constants.EmployeeConstants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebClient
@DirtiesContext
@ActiveProfiles("test")
@SqlGroup({
        @Sql(scripts = "/data/sql/employeeSetUp.sql"),
        @Sql(scripts = {"/data/sql/tearDown.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
public class EmployeeControllerTestIT {


    @Autowired
    WebTestClient webTestClient;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void getAllItems(){

        List<Employee> employeeList = webTestClient.get()
                .uri(contextPath.concat(GET_ALL_MOVIES_V1))
                .exchange()
                .expectStatus().isOk()
                .returnResult(Employee.class)
                .getResponseBody()
                .toStream().collect(Collectors.toList());

        assertEquals(2, employeeList.size());

    }

    @Test
    void employeeById() {

        Employee movie = webTestClient.get().uri(contextPath.concat(EMPLOYEE_BY_ID_PATH_PARAM_V1), 1001)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Employee.class)
                .getResponseBody()
                .blockLast();

        assertEquals("Christian", movie.getFirstName());

    }

    @Test
    void employeeById_NotFound() {

        webTestClient.get().uri(contextPath.concat(EMPLOYEE_BY_ID_PATH_PARAM_V1), 123)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void employeeByFirstName() {

        List<Employee> movies = webTestClient.get().uri(uriBuilder -> uriBuilder.path(contextPath.concat(EMPLOYEE_BY_NAME_QUERY_PARAM_V1))
                .queryParam("employee_name", "Adam")
                .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Employee.class)
                .getResponseBody()
                .toStream().collect(Collectors.toList());

        assertEquals(1, movies.size());
    }

    @Test
    void employeeByLastName() {

        List<Employee> movies = webTestClient.get().uri(uriBuilder -> uriBuilder.path(contextPath.concat(EMPLOYEE_BY_NAME_QUERY_PARAM_V1))
                .queryParam("employee_name", "Sandler")
                .build())
                .exchange()
                .expectStatus().isOk()
                .returnResult(Employee.class)
                .getResponseBody()
                .toStream().collect(Collectors.toList());

        assertEquals(1, movies.size());
    }

    @Test
    void employeeByLastName_NotFound() {

        webTestClient.get().uri(uriBuilder -> uriBuilder.path(contextPath.concat(EMPLOYEE_BY_NAME_QUERY_PARAM_V1))
                .queryParam("employee_name", "ABC")
                .build())
                .exchange()
                .expectStatus().isNotFound();
    }




}
