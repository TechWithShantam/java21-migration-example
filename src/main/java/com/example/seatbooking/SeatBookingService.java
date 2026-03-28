
package com.example.seatbooking;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;

public class SeatBookingService {
    private final List<Seat> seats = new CopyOnWriteArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private int successfulBookings = 0;
    private int failedBookings = 0;
    private int cancellations = 0;

    // OOP: Expose seats, users, bookings for analytics
    public List<Seat> getSeats() {
        return new ArrayList<Seat>(seats);
    }

    public List<User> getUsers() {
        return new ArrayList<User>(users);
    }

    public List<Booking> getBookings() {
        return new ArrayList<Booking>(bookings);
    }

    public SeatBookingService(int totalSeats) {
        for (int i = 1; i <= totalSeats; i++) {
            seats.add(new Seat(i, false));
        }
    }

    public void registerUser(User user) {
        users.add(user);
    }

    public synchronized BookingResult bookSeat(int seatNumber, User user) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getNumber() == seatNumber && !seat.isBooked()) {
                seats.set(i, new Seat(seat.getNumber(), true));
                Booking booking = new Booking(user, seats.get(i));
                bookings.add(booking);
                successfulBookings++;
                return new BookingResult.Success(seat);
            }
        }
        failedBookings++;
        return new BookingResult.Failure("Seat already booked or not found");
    }

    public synchronized BookingResult cancelSeat(int seatNumber, User user) {
        for (int i = 0; i < seats.size(); i++) {
            Seat seat = seats.get(i);
            if (seat.getNumber() == seatNumber && seat.isBooked()) {
                seats.set(i, new Seat(seat.getNumber(), false));
                cancellations++;
                // Remove booking for this user and seat
                bookings.removeIf(b -> b.getUser().equals(user) && b.getSeat().getNumber() == seatNumber);
                return new BookingResult.Success(seat);
            }
        }
        return new BookingResult.Failure("Seat not booked or not found");
    }

    public synchronized List<Integer> getAvailableSeats() {
        List<Integer> available = new ArrayList<>();
        for (Seat seat : seats) {
            if (!seat.isBooked()) {
                available.add(seat.getNumber());
            }
        }
        return available;
    }

    public synchronized BookingResult bookGroup(List<Integer> seatNumbers, User user) {
        List<Integer> unavailable = new ArrayList<>();
        for (int seatNumber : seatNumbers) {
            boolean found = false;
            for (Seat seat : seats) {
                if (seat.getNumber() == seatNumber) {
                    found = true;
                    if (seat.isBooked()) {
                        unavailable.add(seatNumber);
                    }
                    break;
                }
            }
            if (!found) unavailable.add(seatNumber);
        }
        if (!unavailable.isEmpty()) {
            failedBookings++;
            return new BookingResult.Failure("Seats unavailable: " + unavailable);
        }
        for (int i = 0; i < seats.size(); i++) {
            if (seatNumbers.contains(seats.get(i).getNumber()) && !seats.get(i).isBooked()) {
                seats.set(i, new Seat(seats.get(i).getNumber(), true));
                bookings.add(new Booking(user, seats.get(i)));
            }
        }
        successfulBookings += seatNumbers.size();
        return new BookingResult.Success(null);
    }

    public List<Booking> getBookingsForUser(User user) {
        List<Booking> result = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.getUser().equals(user)) {
                result.add(b);
            }
        }
        return result;
    }

    public void simulateConcurrentBooking() {
        // Create users
        User alice = new User(1, "Alice");
        User bob = new User(2, "Bob");
        registerUser(alice);
        registerUser(bob);

        List<Thread> threads = new ArrayList<>();
        for (int i = 1; i <= seats.size(); i++) {
            int seatNum = i;
            User user = (i % 2 == 0) ? alice : bob;
            Thread t = new Thread(() -> {
                BookingResult result = bookSeat(seatNum, user);
                if (result instanceof BookingResult.Success) {
                    System.out.println(user.getName() + " booked seat: " + seatNum);
                } else if (result instanceof BookingResult.Failure) {
                    BookingResult.Failure f = (BookingResult.Failure) result;
                    System.out.println(user.getName() + " failed: " + f.getReason());
                }
            });
            threads.add(t);
            t.start();
        }
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Simulate group booking and cancellation
        System.out.println("\n--- Group Booking Attempt ---");
        List<Integer> group = new ArrayList<>();
        group.add(1);
        group.add(2);
        BookingResult groupResult = bookGroup(group, alice);
        if (groupResult instanceof BookingResult.Success) {
            System.out.println("Group booking successful for seats: " + group);
        } else if (groupResult instanceof BookingResult.Failure) {
            System.out.println("Group booking failed: " + ((BookingResult.Failure) groupResult).getReason());
        }

        System.out.println("\n--- Cancellation Attempt ---");
        BookingResult cancelResult = cancelSeat(1, alice);
        if (cancelResult instanceof BookingResult.Success) {
            System.out.println("Cancellation successful for seat 1");
        } else if (cancelResult instanceof BookingResult.Failure) {
            System.out.println("Cancellation failed: " + ((BookingResult.Failure) cancelResult).getReason());
        }

        System.out.println("\n--- Available Seats ---");
        System.out.println(getAvailableSeats());

        System.out.println("\n--- Booking Statistics ---");
        System.out.println("Successful bookings: " + successfulBookings);
        System.out.println("Failed bookings: " + failedBookings);
        System.out.println("Cancellations: " + cancellations);

        System.out.println("\n--- Bookings for Alice ---");
        for (Booking b : getBookingsForUser(alice)) {
            System.out.println("Alice has seat: " + b.getSeat().getNumber());
        }
    }
}
