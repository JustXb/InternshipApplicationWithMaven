package com.internshipbooking.transport.dto.request;

public class BookingDto {

    private final int guestId;
    private final int hotelId;

    public BookingDto(int guestId, int hotelId) {
        this.guestId = guestId;
        this.hotelId = hotelId;
    }

    public int getGuestId() {
        return guestId;
    }

    public int getHotelId() {
        return hotelId;
    }
}
