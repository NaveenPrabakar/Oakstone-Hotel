package Main.Guest;

import Main.Room.room;

public class KeyCard {

    private int roomnumber;
    Boolean status;
    Guest owner;
    public KeyCard(int roomnumber, Boolean status, Guest owner){
        this.roomnumber = roomnumber;
        this.status = status;
        this.owner = owner;
    }

    public int getRoomnumber() {
        return roomnumber;
    }

    public Boolean getStatus(){
        return status;
    }

    public Guest getOwner(){
        return owner;
    }

    public RoomAccessResult authenticateAndUnlock(Guest guest, room targetRoom) {
        if (guest == null || targetRoom == null) {
            return RoomAccessResult.SYSTEM_ERROR;
        }
        if (!Boolean.TRUE.equals(status)) {
            return RoomAccessResult.CARD_INACTIVE;
        }
        if (!owner.matches(guest)) {
            return RoomAccessResult.INVALID_GUEST;
        }
        if (targetRoom.getRoomNumber() != roomnumber) {
            return RoomAccessResult.ROOM_MISMATCH;
        }
        boolean unlocked = targetRoom.unlock();
        return unlocked ? RoomAccessResult.ACCESS_GRANTED : RoomAccessResult.ROOM_ALREADY_UNLOCKED;
    }

    @Override
    public String toString(){
        return roomnumber + " " + status + " " + owner.getName();
    }
}
