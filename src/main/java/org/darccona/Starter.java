package org.darccona;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@EnableAutoConfiguration
@Configuration
@ComponentScan
public class Starter {

    public static void main(String[] args) throws Throwable {
        SpringApplication.run(Starter.class, args);
    }

}