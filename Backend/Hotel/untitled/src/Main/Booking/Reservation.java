package Main.Booking;

public class Reservation {
    private String reservationId;
    private String guestName;
    private String roomNumber;
    private String startDate;
    private String endDate;

    public Reservation(String reservationId, String guestName, String roomNumber, String startDate, String endDate) {
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

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    @Override
    public String toString() {
        return reservationId + "," + guestName + "," + roomNumber + "," + startDate + "," + endDate;
    }
}
