package com.example.intershipapplicationwithmaven.console;

public enum Commands {

    HELP("Enter the 'help' to see all commands"),
    READ("Read by id"),
    UPDATE("Update guest by ID"),
    DELETE("Delete guest by ID"),
    CREATE("To create request for guest validation"),
    CHECKIN("Check in the hotel"),
    EXIT ("Exit from application")
    ;

    private final String description;

    Commands(String description) {
        this.description = description;
    }


    public static void printHelp() {
        for (Commands command : Commands.values()) {
            System.out.println(command.name() + "," + command.description);
        }
    }




}
