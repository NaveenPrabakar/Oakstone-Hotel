package Main.Room;
import Main.Guest.Guest;

public interface RoomInterface {
    boolean setStatus(String status);
    boolean assignToGuest(Guest guest);
}
