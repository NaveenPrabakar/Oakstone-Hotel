package Main.Employee;

import Main.Guest.*;
import Main.Booking.*;
import Main.Room.room;

public interface FrontDesk {
    public Reservation verifyCheckIn(Guest guest);
    public boolean verifyIdentity(Guest guest);
    public void provideKeyCard(int roomnumber, Guest owner);
    public boolean verifyCheckOut(Guest guest);
    public void revokeKeyCard(int roomnumber, Guest guest);
    public room alertCleaningStaff(int roomNumber);
}