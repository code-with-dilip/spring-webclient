package com.employeeservice.config;

import springfox.documentation.service.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.StringVendorExtension;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Bean
    public Docket moviesAPI(){

        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.employeeservice.controller"))
                //.paths(regex("/rest.*"))
                .build()
                .apiInfo(apiInfo());
    }


    private ApiInfo apiInfo() {

        Contact contact = new Contact("Dilip Sundarraj Youtube Channel","https://www.youtube.com/codewithdilip","");

        StringVendorExtension listVendorExtension = new StringVendorExtension("Code With Dilip", "Online Instructor");
        ApiInfo apiInfo = new ApiInfo("Employee RestFul Service API",
                "Employee RestFul Service",
                "1.0",
                "",
                (springfox.documentation.service.Contact) contact,
                "Employee RestFul Service- Source Code"
                ,"https://github.com/code-with-dilip/spring-webclient/tree/master/employee-service",
                Arrays.asList(listVendorExtension));
        return apiInfo;
    }
}
