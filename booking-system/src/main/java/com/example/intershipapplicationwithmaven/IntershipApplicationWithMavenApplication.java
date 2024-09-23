package com.example.intershipapplicationwithmaven;

import com.example.intershipapplicationwithmaven.console.ConsoleScanner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IntershipApplicationWithMavenApplication implements CommandLineRunner {
    private final ConsoleScanner consoleScanner;

    public IntershipApplicationWithMavenApplication(ConsoleScanner consoleScanner) {
        this.consoleScanner = consoleScanner;
    }

    public static void main(String[] args) {
        SpringApplication.run(IntershipApplicationWithMavenApplication.class, args);
    }



    @Override
    public void run(String... args) throws Exception {
        System.out.println("Введите help для получения помощи");
        consoleScanner.checkCommand();
    }
}

