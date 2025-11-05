package Main.Room;

import Main.Guest.Guest;

import java.time.LocalDate;

public class room implements RoomInterface {
    private int roomNumber;
    private String type;
    private String availability; // "Available" or "Reserved"
    private LocalDate  startDate; // YYYY-MM-DD or "null"
    private LocalDate endDate;   // YYYY-MM-DD or "null"
    private boolean locked;

    public room(int roomNumber, String type, String availability, LocalDate  startDate, LocalDate  endDate) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.availability = availability;
        this.startDate = startDate;
        this.endDate = endDate;
        this.locked = true;
    }

    public static room fromString(String line) {
        String[] parts = line.split(",");
        int number = Integer.parseInt(parts[0]);
        LocalDate start = parts[3].equals("null") ? null : LocalDate.parse(parts[3]);
        LocalDate end = parts[4].equals("null") ? null : LocalDate.parse(parts[4]);
        return new room(number, parts[1], parts[2], start, end);
    }

    public int getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public String getAvailability() { return availability; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public boolean isLocked() { return locked; }

    public void setAvailability(String availability) { this.availability = availability; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void lock() {
        locked = true;
        availability = "Reserved";
    }
    public boolean unlock() {
        if (!locked) {
            return false;
        }
        locked = false;
        availability = "Occupied";
        return true;
    }

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
