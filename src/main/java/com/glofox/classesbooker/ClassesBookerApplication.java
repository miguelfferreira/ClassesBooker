package com.glofox.classesbooker;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition
public class ClassesBookerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClassesBookerApplication.class, args);
    }

}
