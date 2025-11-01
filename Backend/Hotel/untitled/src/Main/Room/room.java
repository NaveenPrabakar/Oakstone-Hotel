package Main.Room;

import Main.Guest.Guest;

public class room implements RoomInterface {
    @Override
    public boolean setStatus(String status) {
        return false;
    }

    @Override
    public boolean assignToGuest(Guest guest) {
        return false;
    }
}
