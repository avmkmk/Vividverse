package com.vividverse.comment; 

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.vividverse.comment"}) // Explicitly scan comment-related packages
public class VividVerseCommentServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VividVerseCommentServiceApplication.class, args);
    }

}