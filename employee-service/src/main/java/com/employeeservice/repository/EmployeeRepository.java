package com.employeeservice.repository;

import com.employeeservice.entity.Employee;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface EmployeeRepository extends CrudRepository<Employee, Long> {

    @Query("select m from Employee m where m.firstName like %?1% or m.lastName like %?1%")
    List<Employee> findByEmployeeName(String name);
}
