package com.vividverse.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // Import ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = {"com.vividverse.user"}) // Explicitly scan user-related packages
public class VividVerseUserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VividVerseUserServiceApplication.class, args);
    }

}