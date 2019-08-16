package com.learnwebclient.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    private Long id;

    private String firstName;

    private String lastName;

    private Integer age;

    private String gender;

    private String role;
}


