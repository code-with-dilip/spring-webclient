package com.employeeservice.controller;

import com.employeeservice.constants.EmployeeConstants;
import com.employeeservice.entity.Employee;
import com.employeeservice.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class EmployeeController {

    @Autowired
    EmployeeRepository employeeRepository;

    @GetMapping(EmployeeConstants.GET_ALL_MOVIES_V1)
    public List<Employee> allEmployees() {
        List<Employee> employees = new ArrayList<>();
        log.info("Recieved request for  retrieving all Employees");
        employeeRepository.findAll()
                .forEach(employees::add);
        return employees;
    }
}
