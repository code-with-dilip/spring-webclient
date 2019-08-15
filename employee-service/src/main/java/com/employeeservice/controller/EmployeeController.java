package com.employeeservice.controller;

import com.employeeservice.entity.Employee;
import com.employeeservice.repository.EmployeeRepository;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.employeeservice.constants.EmployeeConstants.GET_ALL_MOVIES_V1;
import static com.employeeservice.constants.EmployeeConstants.EMPLOYEE_BY_ID_PATH_PARAM_V1;

@RestController
@Slf4j
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

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

    @GetMapping(EMPLOYEE_BY_ID_PATH_PARAM_V1)
    public ResponseEntity<?> employeeById(@PathVariable Long id) {

        Optional<Employee> employeeOptional = employeeRepository.findById(id);
        if(employeeOptional.isPresent()){
            log.info("Response is {}.",employeeOptional.get());
            return ResponseEntity.status(HttpStatus.OK).body(employeeOptional.get());

        }else{
            log.info("No Employee available with the given Employee Id - {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    }
