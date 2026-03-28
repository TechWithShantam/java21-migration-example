package com.example.seatbooking;

public class Main {
    public static void main(String[] args) {
        SeatBookingService service = new SeatBookingService(5);
        service.simulateConcurrentBooking();
    }
}
