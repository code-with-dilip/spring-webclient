package com.employeeservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class Employee {

    @GeneratedValue
    @Id
    private Long id;
    @NotBlank
    @Column(name = "firstname")
    private String firstName;
    @NotBlank
    @Column(name = "lastname")
    private String lastName;
    private Integer age;
    @NotBlank
    private String gender;
    @NotBlank
    private String role;

}
