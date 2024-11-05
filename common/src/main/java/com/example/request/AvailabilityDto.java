package com.example.request;

import java.io.Serializable;

public class AvailabilityDto implements Serializable {

    private static final long serialVersionUID = 1L;
    private boolean isAvailable ;
    private String reason;

    public AvailabilityDto(boolean isAvailable, String reason) {
        this.isAvailable = isAvailable;
        this.reason = reason;
    }

    public AvailabilityDto(){
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
