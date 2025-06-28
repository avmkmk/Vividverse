package com.vividverse.post;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.vividverse.post"}) // Explicitly scan post-related packages
public class VividVersePostServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VividVersePostServiceApplication.class, args);
    }

}
