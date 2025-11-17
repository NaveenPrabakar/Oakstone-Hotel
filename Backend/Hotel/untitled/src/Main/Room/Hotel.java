package Main.Room;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Hotel {
    public String address;
    private Map<String, Double> roomPrices;

    public Hotel(String address) {
        this.address = address;
        this.roomPrices = new HashMap<>();
        roomPrices.put("Single", 100.0);
        roomPrices.put("Double", 150.0);
        roomPrices.put("Suite", 250.0);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Double> getRoomPrices() {
        return roomPrices;
    }

    public void viewPrices() {
        System.out.println("\n===== CURRENT ROOM PRICES =====");
        for (Map.Entry<String, Double> entry : roomPrices.entrySet()) {
            System.out.println(entry.getKey() + " Room: $" + entry.getValue() + " per night");
        }
    }

    public void setPrices() {
        Scanner sc = new Scanner(System.in);
        System.out.println("\n===== SET ROOM PRICES =====");

        for (String type : roomPrices.keySet()) {
            System.out.print("Enter new price for " + type + " room (current: $" + roomPrices.get(type) + "): ");
            String input = sc.nextLine().trim();

            try {
                double newPrice = Double.parseDouble(input);
                roomPrices.put(type, newPrice);
                System.out.println("Updated " + type + " room price to $" + newPrice);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Skipping " + type + " room.");
            }
        }
    }

    public void savePricesToFile() {
        try {
            java.io.PrintWriter writer = new java.io.PrintWriter("RoomPrices.txt");
            writer.println("Hotel Address: " + address);
            writer.println("===== Room Prices =====");

            for (Map.Entry<String, Double> entry : roomPrices.entrySet()) {
                writer.println(entry.getKey() + "," + entry.getValue());
            }

            writer.close();
            System.out.println("Room prices saved to RoomPrices.txt");
        } catch (Exception e) {
            System.out.println("Error saving prices: " + e.getMessage());
        }
    }

    public void loadPricesFromFile() {
        try {
            java.io.File file = new java.io.File("RoomPrices.txt");
            if (!file.exists()) return;

            java.util.Scanner sc = new java.util.Scanner(file);

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (line.contains(",")) {
                    String[] parts = line.split(",");
                    String type = parts[0].trim();
                    double price = Double.parseDouble(parts[1].trim());
                    roomPrices.put(type, price);
                }
            }

            sc.close();
            System.out.println("Room prices loaded from file.");
        } catch (Exception e) {
            System.out.println("Error loading prices: " + e.getMessage());
        }
    }
}