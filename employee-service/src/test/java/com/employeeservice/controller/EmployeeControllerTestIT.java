package com.employeeservice.controller;


import com.employeeservice.constants.EmployeeConstants;
import com.employeeservice.entity.Employee;
import com.fasterxml.jackson.core.JsonProcessingException;
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
import reactor.core.publisher.Mono;

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

    @Autowired
    EmployeeController employeeController;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Test
    void getAllItems() {

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

    @Test
    void createMovie() {

        //given
        Employee employee = new Employee(null, "Chris", "Evans", 50, "male", "Lead Engineer");

        //when
        webTestClient.post().uri(contextPath.concat(ADD_EMPLOYEE_V1))
                .body(Mono.just(employee), Employee.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty();
    }

    @Test
    void createMovie_Validating_Input_Data() throws JsonProcessingException {

        //given
        Employee newMovie = new Employee(null, "", null, null,"female", "Manager" );
        String expectedErrorMessage = "Please pass all the input fields : [firstName, lastName]";

        //when
        webTestClient.post().uri(contextPath.concat(ADD_EMPLOYEE_V1))
                .body(Mono.just(newMovie), Employee.class)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody(String.class)
                .isEqualTo(expectedErrorMessage);
    }

    @Test
    void createEmployeeEntity(){
        Employee employeeToUpdate = new Employee(null, "Chris", "Evans", 50, "male", "Lead Engineer");
        Employee updateEmployee = new Employee(null, "Chris1", "Evans1", 51, "male1", "Lead Engineer1");

        employeeController.createEmployeeEntity(employeeToUpdate,updateEmployee);

        assertEquals(employeeToUpdate.getFirstName(), updateEmployee.getFirstName());
        assertEquals(employeeToUpdate.getLastName(), updateEmployee.getLastName());
        assertEquals(employeeToUpdate.getAge(), updateEmployee.getAge());
        assertEquals(employeeToUpdate.getRole(), updateEmployee.getRole());
    }

    @Test
    void updateEmployee() {

        //given
        String firstMName = "Chris1";
        String lastMName = "Evans1";
        Integer age = 51;
        String gender = "male1";
        String role = "Lead Engineer1";
        Employee updateEmployee = new Employee(null, firstMName, lastMName, age, gender, role);

        //when
        webTestClient.put().uri(contextPath.concat(EMPLOYEE_BY_ID_PATH_PARAM_V1), 1000)
                .syncBody(updateEmployee)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo(firstMName)
                .jsonPath("$.lastName").isEqualTo(lastMName)
                .jsonPath("$.age").isEqualTo(age)
                .jsonPath("$.gender").isEqualTo(gender)
                .jsonPath("$.role").isEqualTo(role);


        webTestClient.get().uri(contextPath.concat(EMPLOYEE_BY_ID_PATH_PARAM_V1), 1000)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.firstName").isEqualTo(firstMName)
                .jsonPath("$.lastName").isEqualTo(lastMName)
                .jsonPath("$.age").isEqualTo(age)
                .jsonPath("$.gender").isEqualTo(gender)
                .jsonPath("$.role").isEqualTo(role);
    }

    @Test
    void deleteMovie() {

        webTestClient.delete().uri(contextPath.concat(EMPLOYEE_BY_ID_PATH_PARAM_V1), 1000)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(DELETE_MESSAGE);

    }

    @Test
    void deleteMovie_invalidMovieId() {

        webTestClient.delete().uri(contextPath.concat(EMPLOYEE_BY_ID_PATH_PARAM_V1), 2000)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void errorEndpoint() {

        webTestClient.get().uri(contextPath+ERROR_ENDPOINT)
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
