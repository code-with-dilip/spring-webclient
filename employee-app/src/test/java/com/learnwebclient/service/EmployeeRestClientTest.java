package com.learnwebclient.service;

import com.learnwebclient.dto.Employee;
import com.learnwebclient.exception.ClientDataException;
import com.learnwebclient.exception.EmployeeServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.retry.RetryExhaustedException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class EmployeeRestClientTest {

    private String baseUrl = "http://localhost:8081/employeeservice";
    WebClient webClient = WebClient.create(baseUrl);
    EmployeeRestClient employeeRestClient = new EmployeeRestClient(webClient);;

    @Test
    void retrieveAllEmployees(){
        List<Employee> employeeList = employeeRestClient.retrieveAllEmployees();
        assertTrue(employeeList.size()>0);
    }

    @Test
    void retrieveEmployeeById(){
        int employeeId = 1;
        Employee employee = employeeRestClient.retrieveEmployeeById(employeeId);
        assertEquals("Chris", employee.getFirstName());
    }

    @Test
    void retrieveEmployeeById_NotFound(){
        int employeeId = 100;
        Assertions.assertThrows(WebClientResponseException.class, () -> employeeRestClient.retrieveEmployeeById(employeeId));

    }

    @Test
    void retrieveEmployeeById_WithRetry(){
        int employeeId = 100;
        Assertions.assertThrows(EmployeeServiceException.class, () -> employeeRestClient.retrieveEmployeeById_WithRetry(employeeId));

    }

    @Test
    void retrieveEmployeeById_Custom_Error_Handling_Client_Data_Exception(){

        int employeeId = 100;
        Assertions.assertThrows(ClientDataException.class, () -> employeeRestClient.retrieveEmployeeById_Custom_Error_Handling(employeeId));
    }


    @Test
    void retrieveEmployeeByName(){
        List<Employee> employees =employeeRestClient.retrieveEmployeeByName("Sandler");
        assertEquals(1, employees.size());
    }

    @Test
    void retrieveEmployeeByName_NotFound(){
        Assertions.assertThrows(WebClientResponseException.class, () -> employeeRestClient.retrieveEmployeeByName("ABC"));
    }

    @Test
    void addNewEmployee(){
        Employee employee = new Employee(null,"Iron", "Man", 54, "male", "Architect");

        Employee employee1 = employeeRestClient.addNewEmployee(employee);
        System.out.println("employee1  : " + employee1);
        assertTrue(employee1.getId()!=null);

    }

    @Test
    void addNewEmployee_BadRequest(){
        Employee employee = new Employee(null,null, "Man", 54, "male", "Architect");

        String expectedErrorMessage = "Please pass all the input fields : [firstName]";
        Assertions.assertThrows(WebClientResponseException.class, () ->  employeeRestClient.addNewEmployee(employee), expectedErrorMessage);

    }

    @Test
    void addNewEmployee_custom_Error_Handling(){
        Employee employee = new Employee(null,null, "Man", 54, "male", "Architect");
        Assertions.assertThrows(ClientDataException.class, () ->  employeeRestClient.addNewEmployee_custom_Error_Handling(employee));

    }

    @Test
    void updateEmployee(){

        int employeeId = 2;
        Employee employee = new Employee(null,"Chris1", null, null, null, null);
        Employee updatedEmployee = employeeRestClient.updateEmployee(employeeId, employee);
        assertEquals("Chris1", updatedEmployee.getFirstName());

    }

    @Test
    void updateEmployee_NotFound(){

        int employeeId = 100;
        Employee employee = new Employee(null,"Chris1", null, null, null, null);
        Assertions.assertThrows(WebClientResponseException.class,() ->  employeeRestClient.updateEmployee(employeeId, employee));

    }

    @Test
    void deleteEmployee(){

        Employee employee = new Employee(null,"Iron", "Man", 54, "male", "Architect");
        Employee employee1 = employeeRestClient.addNewEmployee(employee);
        String message = employeeRestClient.deleteEmployeeById(employee1.getId().intValue());
        String expectedMessage = "Employee deleted successfully.";
        assertEquals(expectedMessage,message);
    }

    @Test
    void deleteEmployee_NotFound(){

        Assertions.assertThrows(WebClientResponseException.class,() -> employeeRestClient.deleteEmployeeById(100));
    }

    @Test
    void errorEndpoint(){

        Assertions.assertThrows(EmployeeServiceException.class,() -> employeeRestClient.errorEndpoint());
    }

    @Test
    void errorEndpoint_withRetry(){

        Assertions.assertThrows(RetryExhaustedException.class,() -> employeeRestClient.errorEndpoint_fixedRetry());
    }


}
