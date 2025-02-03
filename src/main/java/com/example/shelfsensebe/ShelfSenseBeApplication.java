package com.example.shelfsensebe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ShelfSenseBeApplication
{

    public static void main(String[] args)
    {
        SpringApplication.run(ShelfSenseBeApplication.class, args);
    }

}
