package Main.Guest;

import Main.Room.room;

public class Guest implements GuestInterface {


    @Override
    public boolean checkIn(room room) {
        return false;
    }

    @Override
    public boolean checkOut(room room) {
        return false;
    }
}
