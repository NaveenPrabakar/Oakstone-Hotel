package Main.Guest;

import Main.Main;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class BillManager {
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static String billsDirForHotel() {
        return Main.HOTEL_PATH + "/Bills";
    }

    private static String billFileForRoom(int roomNumber) {
        return billsDirForHotel() + "/Room" + roomNumber + "_Bill.txt";
    }

    private static void ensureBillsDir() throws IOException {
        Path dir = Path.of(billsDirForHotel());
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
        }
    }

    // Create a new bill session for a guest
    public static void createBillForRoom(int roomNumber, String guestName) {
        try {
            ensureBillsDir();
            File billFile = new File(billFileForRoom(roomNumber));
            boolean isNew = billFile.createNewFile();

            try (PrintWriter out = new PrintWriter(new FileWriter(billFile, true))) {
                String ts = LocalDateTime.now().format(TS_FMT);
                if (isNew) {
                    out.println("# Bill file for Room " + roomNumber);
                    out.println("# Format: DESCRIPTION,AMOUNT");
                }
                out.println("# Guest: " + guestName + " @ " + ts);
            }
        } catch (IOException e) {
            System.out.println("Error creating bill file: " + e.getMessage());
        }
    }

    // Add charges for a session
    public static void writeSessionBill(int roomNumber, String guestName, Map<String, Double> sessionCharges) {
        try {
            ensureBillsDir();
            File billFile = new File(billFileForRoom(roomNumber));
            if (!billFile.exists()) {
                createBillForRoom(roomNumber, guestName);
            }
            try (PrintWriter out = new PrintWriter(new FileWriter(billFile, true))) {
                for (Map.Entry<String, Double> entry : sessionCharges.entrySet()) {
                    String desc = entry.getKey().replaceAll(",", " ");
                    double amt = entry.getValue();
                    if (amt > 0) {
                        out.printf(Locale.US, "%s,%.2f%n", desc, amt);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error writing session bill: " + e.getMessage());
        }
    }

    // Reads only the last session for the given guest
    public static List<String[]> readLastSessionForGuest(int roomNumber, String guestName) throws IOException {
        List<String[]> items = new ArrayList<>();
        File billFile = new File(billFileForRoom(roomNumber));
        if (!billFile.exists()) return items;

        List<String> lines = Files.readAllLines(billFile.toPath());
        boolean inGuestSession = false;

        // Scan from top to bottom to find the last session for the given guest
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i).trim();
            if (line.startsWith("# Guest:")) {
                inGuestSession = line.toLowerCase().contains(guestName.toLowerCase());
                continue;
            }
            if (inGuestSession && !line.startsWith("#") && !line.isEmpty()) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    items.add(new String[]{parts[0].trim(), parts[1].trim()});
                }
            }
        }
        return items;
    }

    public static void printBillForGuestByName(String guestName) {
        int room = lookupRoomNumberFromPastReservation(guestName);
        if (room == -1) {
            System.out.println("No past reservation found for " + guestName + ". Could not locate bill.");
            return;
        }

        try {
            List<String[]> items = readLastSessionForGuest(room, guestName);
            System.out.println("\n===== BILL FOR " + guestName + " (ROOM " + room + ") =====");
            if (items.isEmpty()) {
                System.out.println("No charges recorded for this guest.");
            } else {
                double total = 0;
                for (String[] it : items) {
                    double amt = 0;
                    try {
                        amt = Double.parseDouble(it[1]);
                    } catch (NumberFormatException ignored) {}
                    System.out.printf(Locale.US, "%-25s $%7.2f%n", it[0], amt);
                    total += amt;
                }
                System.out.println("----------------------------------------");
                System.out.printf(Locale.US, "%-25s $%7.2f%n", "TOTAL", total);
            }
        } catch (IOException e) {
            System.out.println("Error reading bill: " + e.getMessage());
        }
    }

    // Lookup room number logic unchanged
    public static int lookupRoomNumberFromPastReservation(String guestName) {
        String pastPath = Main.HOTEL_PATH + "/" + "Past_Reservation.txt";
        File pastFile = new File(pastPath);
        if (!pastFile.exists()) return -1;

        int foundRoom = -1;
        String foundDate = null;

        try (BufferedReader br = new BufferedReader(new FileReader(pastFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty() || line.startsWith("reservationId")) continue;
                String[] parts = line.split(",");
                if (parts.length >= 6) {
                    String rGuest = parts[1].trim();
                    if (rGuest.equalsIgnoreCase(guestName.trim())) {
                        String actualOut = parts[5].trim();
                        if (foundDate == null || actualOut.compareTo(foundDate) >= 0) {
                            foundDate = actualOut;
                            try {
                                foundRoom = Integer.parseInt(parts[2].trim());
                            } catch (NumberFormatException ex) {
                                foundRoom = -1;
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            return -1;
        }
        return foundRoom;
    }

    public static boolean deleteBillForRoom(int roomNumber) {
        File f = new File(billFileForRoom(roomNumber));
        return f.exists() && f.delete();
    }
}
