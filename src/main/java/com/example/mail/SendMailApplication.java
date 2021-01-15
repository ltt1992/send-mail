package com.example.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SendMailApplication {

    public static void main(String[] args) {
        SpringApplication.run(SendMailApplication.class, args);
    }

}
