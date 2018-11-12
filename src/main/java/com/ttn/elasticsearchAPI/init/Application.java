package com.ttn.elasticsearchAPI.init;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@Slf4j
@ComponentScan("com.ttn.elasticsearchAPI")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}

