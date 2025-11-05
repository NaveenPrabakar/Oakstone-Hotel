package Main.Guest;

import Main.Room.room;

public class Guest {
    private String name;
    private int id;

    public Guest(String name, int id){
        this.name = name;
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public int getid(){
        return id;
    }

    public RoomAccessResult scanKeyCard(KeyCard keyCard, room targetRoom) {
        if (keyCard == null) {
            return RoomAccessResult.SYSTEM_ERROR;
        }
        return keyCard.authenticateAndUnlock(this, targetRoom);
    }

    public boolean matches(Guest other) {
        if (other == null) {
            return false;
        }
        boolean bothIdsKnown = id > 0 && other.id > 0;
        if (bothIdsKnown) {
            return id == other.id;
        }
        return name.equalsIgnoreCase(other.name);
    }
}
