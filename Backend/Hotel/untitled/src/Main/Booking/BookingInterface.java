package Main.Booking;

import Main.Room.room;

public interface BookingInterface {
    boolean createReservation(String guestName, int guestId, room room);
    boolean modifyReservation(String reservationId);
    boolean cancelReservation(String reservationId);
    boolean checkAvailability(room room);
}
