package Main.Room;

import Main.Guest.Guest;

public class room implements RoomInterface {
    private String roomNumber;
    private String type;
    private String availability; // "Available" or "Reserved"
    private String startDate; // YYYY-MM-DD or "null"
    private String endDate;   // YYYY-MM-DD or "null"

    public room(String roomNumber, String type, String availability, String startDate, String endDate) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.availability = availability;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static room fromString(String line) {
        String[] parts = line.split(",");
        return new room(parts[0], parts[1], parts[2], parts[3], parts[4]);
    }

    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public String getAvailability() { return availability; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }

    public void setAvailability(String availability) { this.availability = availability; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }

    @Override
    public boolean setStatus(String status) {
        this.availability = status;
        return true;
    }

    @Override
    public boolean assignToGuest(Guest guest) {
        this.availability = "Reserved";
        return true;
    }

    @Override
    public String toString() {
        return roomNumber + "," + type + "," + availability + "," + startDate + "," + endDate;
    }
}
