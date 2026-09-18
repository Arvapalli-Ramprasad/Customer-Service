package org.example.customerservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//In current Spring Cloud versions, the client starter can often auto-configure discovery
// without explicitly adding this annotation, but keeping the annotation while learning makes
// the intention clear.

//What happens internally?
//
//This is important.
//
//When Customer Service started:
//
//CustomerServiceApplication
//          ↓
//Spring Boot starts
//          ↓
//Eureka Client starts
//          ↓
//Connects to Eureka Server
//          ↓
//Registers customer-service
//          ↓
//Eureka stores its location

@SpringBootApplication
@EnableDiscoveryClient //@EnableDiscoveryClient tells Spring:
// This application participates in service discovery.
public class CustomerServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerServiceApplication.class, args);

//        for killing the process if running
//        netstat -ano | findstr :8091
//        taskkill /PID 9680 /F

    }

}
