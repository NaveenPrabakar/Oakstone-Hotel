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
                if (parts.length == 3) {
                    items.add(new String[]{parts[0].trim(), parts[1].trim(), parts[2].trim()});
                } else if (parts.length == 4) {
                    items.add(new String[]{parts[0].trim(), parts[1].trim() + " " + parts[2].trim(), parts[3].trim()});
                }
                else {
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
            if (items.isEmpty()) {
                System.out.println("No charges recorded for this guest.");
                return;
            }

            final int WIDTH = 37;

            // ---------- HEADER ----------
            System.out.println("=".repeat(WIDTH+5));
            String header1 = "FINAL BILL - ROOM " + room;
            String header2 = "Guest: " + guestName;

            int pad1 = (WIDTH - header1.length()) / 2;
            int pad2 = (WIDTH - header2.length()) / 2;

            if (pad1 < 0) pad1 = 0;
            if (pad2 < 0) pad2 = 0;

            System.out.println(" ".repeat(pad1) + header1);
            System.out.println(" ".repeat(pad2) + header2);
            System.out.println("=".repeat(WIDTH+5));
            System.out.println();

            // ---- Group charges ----
            double securityDeposit = 0;
            List<String[]> roomCharges = new ArrayList<>();
            List<String[]> minibarCharges = new ArrayList<>();
            List<String[]> roomServiceCharges = new ArrayList<>();

            for (String[] it : items) {
                switch (it[0]) {
                    case "SECURITY_DEPOSIT":
                        securityDeposit += Double.parseDouble(it[1]);
                        break;

                    case "ROOM_CHARGE":
                        roomCharges.add(it);
                        break;

                    case "Water":
                    case "Soda":
                    case "Chips":
                    case "Chocolate":
                        minibarCharges.add(it);
                        break;

                    case "Sandwich":
                    case "Salad":
                    case "Pizza":
                    case "Coffee":
                        roomServiceCharges.add(it);
                        break;
                }
            }

            double subtotal = 0.0;

            // ---------- SECURITY DEPOSIT ----------
            if (securityDeposit > 0) {
                int pad = (WIDTH - "SECURITY DEPOSIT".length() - 10) / 2;
                if (pad < 0) pad = 0;
                System.out.println("=".repeat(pad+4) + "==== SECURITY DEPOSIT " + "=".repeat(pad + 6));

                System.out.printf("%12s %28s%n",
                        "Deposit", String.format("$%7.2f", securityDeposit));

                subtotal += securityDeposit;
            }

            // ---------- ROOM CHARGES ----------
            if (!roomCharges.isEmpty()) {
                int pad = (WIDTH - "ROOM CHARGES".length() - 10) / 2;
                if (pad < 0) pad = 0;
                System.out.println("=".repeat(pad+4) + "==== ROOM CHARGES " + "=".repeat(pad+6));

                for (String[] rc : roomCharges) {
                    double amt = Double.parseDouble(rc[2]);
                    System.out.printf("%12s %28s%n",
                            rc[1], String.format("$%7.2f", amt));
                    subtotal += amt;
                }
            }

            // ---------- MINI BAR ----------
            if (!minibarCharges.isEmpty()) {
                int pad = (WIDTH - "MINI BAR".length() - 10) / 2;
                if (pad < 0) pad = 0;
                System.out.println("=".repeat(pad+4) + "==== MINI BAR " + "=".repeat(pad+6));

                for (String[] mb : minibarCharges) {
                    double amt = Double.parseDouble(mb[1]);
                    System.out.printf("%12s %28s%n",
                            mb[0], String.format("$%7.2f", amt));
                    subtotal += amt;
                }
            }

            // ---------- ROOM SERVICE ----------
            if (!roomServiceCharges.isEmpty()) {
                int pad = (WIDTH - "ROOM SERVICE".length() - 10) / 2;
                if (pad < 0) pad = 0;
                System.out.println("=".repeat(pad+4) + "==== ROOM SERVICE " + "=".repeat(pad+6));

                for (String[] rs : roomServiceCharges) {
                    double amt = Double.parseDouble(rs[1]);
                    System.out.printf("%12s %28s%n",
                            rs[0], String.format("$%7.2f", amt));
                    subtotal += amt;
                }
            }

            // ---------- SUBTOTAL ----------
            System.out.println("-".repeat(WIDTH+5));
            System.out.printf("%12s %28s%n",
                    "SUBTOTAL", String.format("$%7.2f", subtotal));

            // ---------- TAX ----------
            double taxRate = 0;

            if (Main.HOTEL_PATH.equals("Chicago")) {
                taxRate = 0.06 + 0.045 + 0.06 + 0.025 + 0.02 + 0.01;
            } else if (Main.HOTEL_PATH.equals("DesMoines")) {
                taxRate = 0.05 + 0.07;
            }

            double taxAmount = subtotal * taxRate;

            System.out.printf("%12s %28s%n",
                    "TAX", String.format("$%7.2f", taxAmount));

            System.out.println("-".repeat(WIDTH+5));

            // ---------- GRAND TOTAL ----------
            double grandTotal = subtotal + taxAmount;
            System.out.printf("%12s %28s%n",
                    "GRAND TOTAL", String.format("$%7.2f", grandTotal));

            System.out.println("=".repeat(WIDTH+5));
            System.out.println();

        } catch (Exception e) {
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
