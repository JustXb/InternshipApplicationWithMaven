package com.example.intershipapplicationwithmaven.console;

import com.example.intershipapplicationwithmaven.service.BookingService;

import org.springframework.stereotype.Component;


import java.util.Locale;
import java.util.Scanner;

@Component
public class ConsoleScanner {


    private final Scanner scanner;
    private final BookingService bookingService;

    public ConsoleScanner(BookingService bookingService) {
        this.scanner = new Scanner(System.in);  // Ввод через консоль
        this.bookingService = bookingService;
    }

    private String scan() {
        return scanner.nextLine().toUpperCase(Locale.ROOT);
    }

    public void checkCommand() {
        while (true) {
            String scannedCommand = this.scan();

            try {
                Commands command = Commands.valueOf(scannedCommand);

                // Выполнение команды
                switch (command) {
                    case HELP:
                        Commands.printHelp();
                        break;
                    case CREATE:
                        bookingService.createGuest();
                        break;
                    case READ:
                        bookingService.readGuest();
                        break;
                    case READALL:
                        bookingService.readGuests();
                        break;
                    case UPDATE:
                        bookingService.updateGuest();
                        break;
                    case DELETE:
                        bookingService.deleteGuestByDB();
                        break;
                    case CHECKIN:
                        bookingService.checkIn();
                        break;
                    case EXIT:
                        System.out.println("Exiting the application...");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Unknown command: " + scannedCommand);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown command entered. Try again.");
            } catch (Exception exception) {
                System.out.println("An unexpected error occurred: " + exception.getMessage());
            }
        }
    }
}
