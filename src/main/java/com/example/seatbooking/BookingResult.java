package com.example.seatbooking;

public abstract class BookingResult {
    private BookingResult() {}

    public static final class Success extends BookingResult {
        private final Seat seat;
        public Success(Seat seat) {
            this.seat = seat;
        }
        public Seat getSeat() {
            return seat;
        }
    }

    public static final class Failure extends BookingResult {
        private final String reason;
        public Failure(String reason) {
            this.reason = reason;
        }
        public String getReason() {
            return reason;
        }
    }
}
