package com.employeeservice.controller;

import com.employeeservice.constants.EmployeeConstants;
import com.employeeservice.entity.Employee;
import com.employeeservice.repository.EmployeeRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.employeeservice.constants.EmployeeConstants.*;

@RestController
@Slf4j
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    Function<Long,ResponseStatusException > notFoundId = (id) -> {
        return new ResponseStatusException(HttpStatus.NOT_FOUND, "No Employee Available with the given Id - "+ id);
    };

    Function<String,ResponseStatusException > notFoundName = (name) -> {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No Employee Available with the given name - "+ name);
    };

    Supplier<ResponseStatusException > serverError = () -> {
        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "RunTimeException from Employee Service");
    };


    @GetMapping(GET_ALL_MOVIES_V1)
    @ApiOperation("Retrieves all the Employees")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "SuccessFul Retrieval of Employees")
            }
    )
    public List<Employee> allEmployees() {
        List<Employee> employees = new ArrayList<>();
        log.info("Recieved request for  retrieving all Employees");
        employeeRepository.findAll()
                .forEach(employees::add);
        return employees;
    }

    @ApiOperation("Retrieve an Employee using the Employee id.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the Employee for the id."),
                    @ApiResponse(code = 404, message = "No Employee found for the id that's passed."),
            }
    )
    @GetMapping(EMPLOYEE_BY_ID_PATH_PARAM_V1)
    public ResponseEntity<?> employeeById(@PathVariable Long id) {

        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            log.info("Response is {}.", employeeOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body(employeeOptional.get());

        } else {
            log.info("No Employee available with the given Employee Id - {}", id);
           throw notFoundId.apply(id);
        }
    }

    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "video not found")
    public class EmployeeNotFoundException extends RuntimeException {
    }


    @ApiOperation("Returns the Employees using the employee name passed as part of the request.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Returns the Employees using the FirstName or LastName of the employee."),
                    @ApiResponse(code = 404, message = "No Employee found for the name thats passed."),
            }
    )
    @GetMapping(EMPLOYEE_BY_NAME_QUERY_PARAM_V1)
    public ResponseEntity<?> movieByName(@RequestParam("employee_name") String name) {

        log.info("Received the request to search by Employee name - {} .", name);

        List<Employee> employees = employeeRepository.findByEmployeeName(name);
        if (CollectionUtils.isEmpty(employees)) {
            log.info("No Employee available for the given Employee name - {}.", name);
            throw notFoundName.apply(name);
        } else {
            log.info("Response is : {}", employees);
            return ResponseEntity.status(HttpStatus.OK).body(employees);

        }
    }

    @ApiOperation("Adds a new Employee.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "Employee Successfully added to the InMemory DB.")
            }
    )
    @PostMapping(ADD_EMPLOYEE_V1)
    public ResponseEntity<?> createMovie(@Valid @RequestBody Employee employee) {

        log.info("Received the request to add a new Employee in the service {} ", employee);
        Employee addedEmployee = employeeRepository.save(employee);
        log.info("Employee SuccessFully added to the DB. New Employee Details are {} .", employee);
        return ResponseEntity.status(HttpStatus.CREATED).body(addedEmployee);

    }

    @ApiOperation("Updates the Employee details.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Employee details are successfully updated to the DB."),
                    @ApiResponse(code = 404, message = "No Employee found for the id that's passed."),
            }
    )
    @PutMapping(EMPLOYEE_BY_ID_PATH_PARAM_V1)
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee updateEmployee) {
        log.info("Received the request to update the employee. Employee Id is {} and the updated Employee Details are {} ", id, updateEmployee);

        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if (employeeOptional.isPresent()) {
            Employee employeeToUpdate = employeeOptional.get();
            createEmployeeEntity(employeeToUpdate, updateEmployee);
            employeeRepository.save(employeeToUpdate);
            return ResponseEntity.status(HttpStatus.OK).body(employeeToUpdate);

        } else {
            log.info("No Employee available for the given Movie Id - {}.", id);
            throw notFoundId.apply(id);

        }


    }

    public void createEmployeeEntity(Employee employeeToUpdate, Employee updateEmployee) {
        if (checkEmptyNullString(updateEmployee.getFirstName()) && !updateEmployee.getFirstName().equals(employeeToUpdate.getFirstName())) {
            employeeToUpdate.setFirstName(updateEmployee.getFirstName());
        }
        if (checkEmptyNullString(updateEmployee.getLastName()) && !updateEmployee.getLastName().equals(employeeToUpdate.getLastName())) {
            employeeToUpdate.setLastName(updateEmployee.getLastName());
        }
        if(updateEmployee!=null && updateEmployee.getAge()!=employeeToUpdate.getAge()){
            employeeToUpdate.setAge(updateEmployee.getAge());
        }
        if (checkEmptyNullString(updateEmployee.getGender()) && !updateEmployee.getGender().equals(employeeToUpdate.getGender())) {
            employeeToUpdate.setGender(updateEmployee.getGender());
        }
        if (checkEmptyNullString(updateEmployee.getRole()) && !updateEmployee.getRole().equals(employeeToUpdate.getRole())) {
            employeeToUpdate.setRole(updateEmployee.getRole());
        }
    }

    @ApiOperation("Removes the Employee details.")
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "Employee details are successfully deleted from the DB."),
                    @ApiResponse(code = 404, message = "No Employee found for the year that's passed."),
            }
    )
    @DeleteMapping(EMPLOYEE_BY_ID_PATH_PARAM_V1)
    public ResponseEntity<?> deleteMovie(@PathVariable Long id) {

        log.info("Received the request to delete a Employee and the id is {} .", id);
        Optional<Employee> movieToUpdateOptional = employeeRepository.findById(id);
        if (movieToUpdateOptional.isPresent()) {
            employeeRepository.deleteById(id);
            log.info("Employee Successfully deleted from the DB");
            return ResponseEntity.status(HttpStatus.OK).body(EmployeeConstants.DELETE_MESSAGE);
        } else {
            log.info("No Employee available for the given Movie Id - {}.", id);
            throw  notFoundId.apply(id);
        }


    }

    @GetMapping(ERROR_ENDPOINT)
    public ResponseEntity<?> errorEndpoint() {

            throw serverError.get();
    }

    private boolean checkEmptyNullString(String input) {
        return !StringUtils.isEmpty(input) && !StringUtils.isEmpty(input.trim());
    }

}
