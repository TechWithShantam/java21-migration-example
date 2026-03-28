package com.example.seatbooking;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class AnalyticsService {
    private final SeatBookingService bookingService;

    public AnalyticsService(SeatBookingService bookingService) {
        this.bookingService = bookingService;
    }

    public int getTotalSeats() {
        return bookingService.getAvailableSeats().size() + getBookedSeatsCount();
    }

    public int getBookedSeatsCount() {
        int count = 0;
        for (Seat seat : bookingService.getSeats()) {
            if (seat.isBooked()) count++;
        }
        return count;
    }

    public int getAvailableSeatsCount() {
        return bookingService.getAvailableSeats().size();
    }

    public int getTotalUsers() {
        return bookingService.getUsers().size();
    }

    public int getTotalBookings() {
        return bookingService.getBookings().size();
    }

    public Map<String, Integer> getBookingsPerUser() {
        Map<String, Integer> map = new HashMap<>();
        for (Booking b : bookingService.getBookings()) {
            String name = b.getUser().getName();
            map.put(name, map.getOrDefault(name, 0) + 1);
        }
        return map;
    }

    public String downloadAnalyticsReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Total seats: ").append(getTotalSeats()).append("\n");
        sb.append("Booked seats: ").append(getBookedSeatsCount()).append("\n");
        sb.append("Available seats: ").append(getAvailableSeatsCount()).append("\n");
        sb.append("Total users: ").append(getTotalUsers()).append("\n");
        sb.append("Total bookings: ").append(getTotalBookings()).append("\n");
        sb.append("Bookings per user: ").append(getBookingsPerUser()).append("\n");
        return sb.toString();
    }
}
