package Main.Booking;

import Main.Main;
import Main.Data.FileDataRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Scanner;

public class Reservation {
    private String reservationId;
    private String guestName;
    private int roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    private static final String RESERVATION = Main.HOTEL_PATH + "/Reservation.txt";
    private static final String PAST_RESERVATIONS = Main.HOTEL_PATH + "/Past_Reservation.txt";

    public Reservation(String reservationId, String guestName, int roomNumber, LocalDate startDate, LocalDate endDate) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Reservation(String name, String resId) {
        String[] customerInfo = checkReservations(RESERVATION, name, resId);
        if (customerInfo == null) {
            customerInfo = checkReservations(PAST_RESERVATIONS, name, resId);
        }
        if (customerInfo != null) {
            this.reservationId = customerInfo[0];
            this.guestName = customerInfo[1];
            this.roomNumber = Integer.parseInt(customerInfo[2]);
            this.startDate = LocalDate.parse(customerInfo[3]);
            this.endDate = LocalDate.parse(customerInfo[4]);
        } else {
            System.out.println("Reservation not found for the given name and reservation ID.");
        }
    }

    private String[] checkReservations(String path, String nameInput, String resIdInput) {
        java.util.List<String[]> reservations;
        FileDataRepository fileDataRepository = new FileDataRepository(Path.of(Main.HOTEL_PATH));
        
        if (path.equals(RESERVATION)) {
            reservations = fileDataRepository.loadReservations();
        } else {
            reservations = fileDataRepository.loadPastReservations();
        }
        
        for (String[] reservation : reservations) {
            // Skip header row
            if (reservation[0].equals("reservationId")) {
                continue;
            }
            if (reservation[0].equals(resIdInput) && reservation[1].equals(nameInput)) {
                return reservation;
            }
        }
        
        return null;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return reservationId + "," + guestName + "," + roomNumber + "," + startDate + "," + endDate;
    }
}