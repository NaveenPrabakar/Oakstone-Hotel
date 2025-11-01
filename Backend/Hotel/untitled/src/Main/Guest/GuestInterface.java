package Main.Guest;

import Main.Room.room;

public interface GuestInterface {
    boolean checkIn(room room);
    boolean checkOut(room room);
    //boolean makeReservation(Reservation reservation);
}
