package Main.Employee;

import Main.Room.room;
import Main.Main;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Housekeeping extends Employee implements CleaningStaff {

    // Each cleaning request is stored as a "ticket"
    private static final LinkedBlockingQueue<room> cleaningQueue = new LinkedBlockingQueue<>();

    private static String currentCleaner = null;
    private static boolean active = true;
    private static String currentHotelAddress = null;

    public Housekeeping(int id, String name) {
        super(id, name, "CleaningStaff");
    }

    // Add a cleaning task (ticket)
    public void addToCleanQueue(room roomToClean) {
        try {
            cleaningQueue.put(roomToClean);
            System.out.println("Cleaning request added for Room "
                    + roomToClean.getRoomNumber()
                    + " (" + roomToClean.getAddress() + ")");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void processCleaning(String cleanerName) {
        if (!active) {
            System.out.println("Housekeeping is currently inactive.");
            return;
        }

        if (Main.HOTEL_PATH == null || Main.HOTEL_PATH.isEmpty()) {
            System.out.println("Hotel location not set. Please log in through your assigned hotel.");
            return;
        }

        currentCleaner = cleanerName;
        currentHotelAddress = Main.HOTEL_PATH;

        System.out.println("\n===== HOUSEKEEPING PANEL =====");
        System.out.println("Welcome, " + cleanerName + " (" + currentHotelAddress + ")");
        boolean running = true;
        Scanner sc = new Scanner(System.in);

        while (running) {
            System.out.println("\n--- Options ---");
            System.out.println("1. View Cleaning Tickets");
            System.out.println("2. Clean a Room");
            System.out.println("3. Exit Housekeeping Panel");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                viewTickets();
            }
            else if (choice.equals("2")) {
                handleCleaning(sc);
            }
            else if (choice.equals("3")) {
                System.out.println("Logging out of housekeeping...");
                running = false;
            }
            else {
                System.out.println("Invalid choice. Please select 1–3.");
            }
        }

        currentCleaner = null;
    }


    private static void viewTickets() {
        if (cleaningQueue.isEmpty()) {
            System.out.println("📭 No cleaning requests at this time.");
            return;
        }

        System.out.println("\nCleaning Tickets for " + currentHotelAddress + ":");
        List<room> matchingRooms = new ArrayList<>();
        int index = 1;

        for (room r : cleaningQueue) {
            if (r.getAddress().equalsIgnoreCase(currentHotelAddress)) {
                System.out.println(index + ". Room " + r.getRoomNumber() + " (" + r.getType() + ")");
                matchingRooms.add(r);
                index++;
            }
        }

        if (matchingRooms.isEmpty()) {
            System.out.println("No cleaning tasks for this hotel.");
        }
    }

    private static void handleCleaning(Scanner sc) {
        if (cleaningQueue.isEmpty()) {
            System.out.println("No cleaning requests at this time.");
            return;
        }

        List<room> availableRooms = new ArrayList<>();
        for (room r : cleaningQueue) {
            if (r.getAddress().equalsIgnoreCase(currentHotelAddress)) {
                availableRooms.add(r);
            }
        }

        if (availableRooms.isEmpty()) {
            System.out.println("No cleaning requests for " + currentHotelAddress);
            return;
        }

        System.out.println("\nSelect a room to clean:");
        for (int i = 0; i < availableRooms.size(); i++) {
            System.out.println((i + 1) + ". Room " + availableRooms.get(i).getRoomNumber());
        }

        System.out.print("Enter room number to clean (or 0 to cancel): ");
        String input = sc.nextLine().trim();

        int choice;
        try {
            choice = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }

        if (choice == 0) return;
        if (choice < 1 || choice > availableRooms.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        room selectedRoom = availableRooms.get(choice - 1);
        cleaningQueue.remove(selectedRoom);
        performCleaning(selectedRoom);
    }

    private static void performCleaning(room roomToClean) {
        synchronized (roomToClean) {
            System.out.println("Cleaning started for Room " + roomToClean.getRoomNumber() + "...");
            try {
                Thread.sleep(1500); // Simulate cleaning
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Cleaning complete for Room " + roomToClean.getRoomNumber());
        }
    }

    private static void fireAllWorkers() {
        active = false;
        System.out.println("All housekeeping operations halted.");
    }
}
