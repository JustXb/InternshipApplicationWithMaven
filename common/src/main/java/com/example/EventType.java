package com.example;

public enum EventType {
    CREATED("Event = create"),
    MISTAKE("Event = mistake"),
    SUCCESS("Event = success")
;

    private final String description;

    EventType(String description) {
        this.description = description;
    }

    public void printHelp() {
        for (EventType event : EventType.values()) {
            System.out.println(event.name() + "," + event.description);
        }
    }
}
