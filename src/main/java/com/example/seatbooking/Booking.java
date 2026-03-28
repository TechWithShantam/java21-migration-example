package com.example.seatbooking;

public class Booking {
    private final User user;
    private final Seat seat;

    public Booking(User user, Seat seat) {
        this.user = user;
        this.seat = seat;
    }

    public User getUser() {
        return user;
    }

    public Seat getSeat() {
        return seat;
    }
}