package Main.Employee;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.UUID;

public class KitchenOrder {
    private static final DateTimeFormatter TS_FORMAT = DateTimeFormatter.ofPattern("MMM dd HH:mm");

    private final String id;
    private final int roomNumber;
    private final String guestName;
    private final String hotelPath;
    private final LinkedHashMap<String, Integer> items;
    private final double totalAmount;
    private final LocalDateTime createdAt;

    public KitchenOrder(String hotelPath,
                        int roomNumber,
                        String guestName,
                        Map<String, Integer> orderedItems,
                        double totalAmount) {
        this.id = UUID.randomUUID().toString();
        this.hotelPath = hotelPath;
        this.roomNumber = roomNumber;
        this.guestName = guestName;
        this.items = new LinkedHashMap<>(orderedItems);
        this.totalAmount = totalAmount;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getHotelPath() {
        return hotelPath;
    }

    public Map<String, Integer> getItems() {
        return new LinkedHashMap<>(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean belongsToHotel(String hotelPath) {
        return this.hotelPath != null && this.hotelPath.equalsIgnoreCase(hotelPath);
    }

    public String formatLineItem() {
        StringJoiner joiner = new StringJoiner(", ");
        for (Map.Entry<String, Integer> entry : items.entrySet()) {
            joiner.add(entry.getKey() + " x" + entry.getValue());
        }
        return "Room " + roomNumber + " | " + guestName + " | " + joiner +
                String.format(" | Total $%.2f | %s", totalAmount, createdAt.format(TS_FORMAT));
    }
}
