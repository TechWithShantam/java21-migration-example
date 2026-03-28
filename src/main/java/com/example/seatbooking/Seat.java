package com.example.seatbooking;

public class Seat {
	private final int number;
	private final boolean booked;

	public Seat(int number, boolean booked) {
		this.number = number;
		this.booked = booked;
	}

	public int getNumber() {
		return number;
	}

	public boolean isBooked() {
		return booked;
	}
}
