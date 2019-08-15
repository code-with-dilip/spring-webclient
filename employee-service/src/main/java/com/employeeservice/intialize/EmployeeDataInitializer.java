package com.employeeservice.intialize;

import com.employeeservice.entity.Employee;
import com.employeeservice.repository.EmployeeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
public class EmployeeDataInitializer implements CommandLineRunner {

    @Autowired
    EmployeeRepository employeeRepository;

    @Override
    public void run(String... args) throws Exception {

        Employee employee1 = new Employee(null, "Chris", "Evans", 50, "male", "Lead Engineer");
        Employee employee2 = new Employee(null, "Adam", "Sandler", 50, "male", "Senior Engineer");
        Employee employee3 = new Employee(null, "Jenny", "Richards", 32, "female", "Senior Engineer");
        Employee employee4 = new Employee(null, "Amy", "Adams", 44, "female", "Manager");
        List<Employee> employeeList = Arrays.asList(employee1,employee2,employee3,employee4);
        log.info("********* Employee RestFul Service Initial Data Starts *********");
        employeeRepository.saveAll(employeeList);
        employeeRepository.findAll().forEach((employee-> log.info(""+employee)));
        log.info("********* Employee RestFul Service Initial Data Ends *********");


    }
}
