package com.employeeservice.controller;


import com.employeeservice.entity.Employee;
import org.junit.jupiter.api.BeforeEach;
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

import static com.employeeservice.constants.EmployeeConstants.GET_ALL_MOVIES_V1;
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


}
