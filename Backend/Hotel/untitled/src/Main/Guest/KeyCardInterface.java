package Main.Guest;

import Main.Room.room;

/**
 * Contract for a key card capable of authenticating a guest for a specific room
 * and triggering the room unlock process.
 */
public interface KeyCardInterface {
    RoomAccessResult authenticateAndUnlock(Guest guest, room targetRoom);

    int getRoomnumber();

    boolean getStatus();

    Guest getOwner();
}
