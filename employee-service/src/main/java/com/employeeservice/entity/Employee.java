package com.employeeservice.entity;

import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("Represents the ID which is unique to an Employee.")
    private Long id;

    @NotBlank
    @Column(name = "firstname")
    @ApiModelProperty("Represents the First Name of the Employee.")
    private String firstName;

    @NotBlank
    @Column(name = "lastname")
    @ApiModelProperty("Represents the Last Name of the Employee.")
    private String lastName;

    @ApiModelProperty("Represents the Age of the Employee.")
    private Integer age;

    @NotBlank
    @ApiModelProperty("Represents the gender of the Employee.")
    private String gender;

    @NotBlank
    @ApiModelProperty("Represents the role of the Employee.")
    private String role;

}
