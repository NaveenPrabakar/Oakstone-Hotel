package Main.Booking;

import java.time.LocalDate;

public class Reservation {
    private String reservationId;
    private String guestName;
    private int roomNumber;
    private LocalDate startDate;
    private LocalDate endDate;

    public Reservation(String reservationId, String guestName, int roomNumber, LocalDate startDate, LocalDate endDate) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomNumber = roomNumber;
        this.startDate = startDate;
        this.endDate = endDate;
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