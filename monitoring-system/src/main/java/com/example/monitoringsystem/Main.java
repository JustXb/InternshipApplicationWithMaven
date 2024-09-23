package com.example.monitoringsystem;

import com.example.monitoringsystem.transport.server.MonitoringServer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner run(ApplicationContext ctx) {
        return args -> {
            MonitoringServer monitoringServer = ctx.getBean(MonitoringServer.class);
            monitoringServer.start();
        };
    }
}
