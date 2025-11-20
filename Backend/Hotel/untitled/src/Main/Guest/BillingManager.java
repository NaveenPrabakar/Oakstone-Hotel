package Main.Guest;

import Main.Booking.Reservation;
import Main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class BillingManager {

    private static final String ROOM_FILE = Main.HOTEL_PATH + "/Room.txt";
    private static final String PRICE_FILE = Main.HOTEL_PATH + "/RoomPrices.txt";
    private static final double SECURITY_DEPOSIT = 50.0;

    /**
     * Called at check-in.
     * Creates bill file, then writes:
     * - security deposit
     * - daily room charge for each night of stay
     */
    public static void initBillingForStay(Reservation reservation, String guestName, String location) {
        int roomNumber = reservation.getRoomNumber();

        // Fetch room type
        String roomType = getRoomType(roomNumber);

        // Fetch daily rate
        double dailyRate = getRoomRate(roomType);

        // Prepare line entries
        List<String> entries = generateInitialCharges(reservation, dailyRate);

        // Write to bill file
        appendCharges(roomNumber, guestName, entries);
    }


    // ======================================================
    // INTERNAL HELPERS
    // ======================================================

    private static String getRoomType(int roomNumber) {
        try (Scanner sc = new Scanner(new File(ROOM_FILE))) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();

                if (line.startsWith("roomNumber")) continue;

                String[] parts = line.split(",");

                int num = Integer.parseInt(parts[0]);
                String type = parts[1];

                if (num == roomNumber) {
                    return type;
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading Room.txt: " + e.getMessage());
        }
        return null;
    }

    private static double getRoomRate(String type) {
        try (Scanner sc = new Scanner(new File(PRICE_FILE))) {

            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.startsWith("Hotel") || line.startsWith("=====")) continue;
                String[] parts = line.split(",");

                String roomType = parts[0];
                double price = Double.parseDouble(parts[1]);

                if (roomType.equalsIgnoreCase(type)) {
                    return price;
                }
            }

        } catch (Exception e) {
            System.out.println("Error reading RoomPrices.txt: " + e.getMessage());
        }
        return 0.0;
    }

    private static List<String> generateInitialCharges(Reservation r, double dailyRate) {
        List<String> lines = new ArrayList<>();

        // Security Deposit
        lines.add("SECURITY_DEPOSIT (RECEIVED AT CHECK-IN)," + SECURITY_DEPOSIT);
        lines.add("SECURITY_DEPOSIT (REFUNDED AT CHECK-OUT)," + SECURITY_DEPOSIT);

        // Daily room charges
        LocalDate date = r.getStartDate();
        LocalDate end = r.getEndDate();

        while (date.isBefore(end)) {
            lines.add("ROOM_CHARGE," + date + "," + dailyRate);
            date = date.plusDays(1);
        }

        return lines;
    }

    private static String billFileForRoom(int roomNumber) {
        return Main.HOTEL_PATH + "/Bills/Room" + roomNumber + "_Bill.txt";
    }

    public static void appendCharges(int roomNumber, String guestName, List<String> entries) {
        try (PrintWriter out = new PrintWriter(new FileWriter(billFileForRoom(roomNumber), true))) {
            for (String line : entries) {
                out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error writing bill: " + e.getMessage());
        }
    }
}
