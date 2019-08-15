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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.employeeservice.constants.EmployeeConstants.*;

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

    @GetMapping(EMPLOYEE_BY_NAME_QUERY_PARAM_V1)
    public ResponseEntity<?> movieByName(@RequestParam("employee_name") String name) {

        log.info("Received the request to search by Employee name - {} .", name);

        List<Employee> employees = employeeRepository.findByEmployeeName(name);
        if (CollectionUtils.isEmpty(employees)) {
            log.info("No Employee available for the given Employee name - {}.", name);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        } else {
            log.info("Response is : {}", employees);
            return ResponseEntity.status(HttpStatus.OK).body(employees);

        }
    }


    }
