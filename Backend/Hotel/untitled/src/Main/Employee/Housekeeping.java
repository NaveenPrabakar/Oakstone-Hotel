package Main.Employee;

import Main.Room.room;
import Main.Main;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class Housekeeping extends Employee implements CleaningStaff {

    // Each cleaning request is stored as a "ticket"
    private static final LinkedBlockingQueue<HousekeepingRequest> cleaningQueue = new LinkedBlockingQueue<>();

    private static boolean active = true;
    private static String currentHotelAddress = null;

    public Housekeeping(int id, String name) {
        super(id, name, "CleaningStaff");
    }

    // Add a cleaning task (ticket)
    public void addToCleanQueue(room roomToClean) {
        if (roomToClean == null) {
            return;
        }

        HousekeepingRequest autoTurnover = new HousekeepingRequest(
                cloneRoom(roomToClean),
                "Turnover Cleaning",
                "Next Available",
                List.of(),
                "Auto-generated after checkout",
                false,
                "System"
        );

        if (enqueueRequest(autoTurnover)) {
            System.out.println("Cleaning request added for Room "
                    + roomToClean.getRoomNumber()
                    + " (" + safeAddress(roomToClean) + ")");
        }
    }

    public static boolean submitGuestRequest(HousekeepingRequest request) {
        boolean accepted = enqueueRequest(request);
        if (accepted) {
            System.out.println("Guest-initiated housekeeping request queued for Room "
                    + request.getRoomInfo().getRoomNumber() + ".");
        }
        return accepted;
    }

    private static boolean enqueueRequest(HousekeepingRequest request) {
        if (request == null || request.getRoomInfo() == null) {
            System.out.println("Unable to queue housekeeping request without room information.");
            return false;
        }

        for (HousekeepingRequest pending : cleaningQueue) {
            if (sameRoom(pending.getRoomInfo(), request.getRoomInfo())
                    && pending.getServiceType().equalsIgnoreCase(request.getServiceType())
                    && pending.getTimeWindow().equalsIgnoreCase(request.getTimeWindow())) {
                System.out.println("A similar housekeeping request is already queued for Room "
                        + request.getRoomInfo().getRoomNumber() + ".");
                return false;
            }
        }

        try {
            cleaningQueue.put(request);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Failed to queue housekeeping request.");
            return false;
        }
    }

    private static room cloneRoom(room original) {
        return new room(
                original.getAddress(),
                original.getRoomNumber(),
                original.getType(),
                original.getAvailability(),
                original.getStartDate(),
                original.getEndDate()
        );
    }

    private static boolean sameRoom(room a, room b) {
        if (a == null || b == null) {
            return false;
        }
        String addressA = safeAddress(a);
        String addressB = safeAddress(b);
        return a.getRoomNumber() == b.getRoomNumber() && addressA.equalsIgnoreCase(addressB);
    }

    private static String safeAddress(room r) {
        return r.getAddress() == null ? "Unknown" : r.getAddress();
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

    }


    private static void viewTickets() {
        if (cleaningQueue.isEmpty()) {
            System.out.println("📭 No cleaning requests at this time.");
            return;
        }

        System.out.println("\nCleaning Tickets for " + currentHotelAddress + ":");
        List<HousekeepingRequest> matchingRequests = new ArrayList<>();
        int index = 1;

        for (HousekeepingRequest request : cleaningQueue) {
            room ticketRoom = request.getRoomInfo();
            if (ticketRoom != null && safeAddress(ticketRoom).equalsIgnoreCase(currentHotelAddress)) {
                System.out.println(index + ". " + request.formatTicketLine());
                matchingRequests.add(request);
                index++;
            }
        }

        if (matchingRequests.isEmpty()) {
            System.out.println("No cleaning tasks for this hotel.");
        }
    }

    private static void handleCleaning(Scanner sc) {
        if (cleaningQueue.isEmpty()) {
            System.out.println("No cleaning requests at this time.");
            return;
        }

        List<HousekeepingRequest> available = new ArrayList<>();
        for (HousekeepingRequest request : cleaningQueue) {
            room ticketRoom = request.getRoomInfo();
            if (ticketRoom != null && safeAddress(ticketRoom).equalsIgnoreCase(currentHotelAddress)) {
                available.add(request);
            }
        }

        if (available.isEmpty()) {
            System.out.println("No cleaning requests for " + currentHotelAddress);
            return;
        }

        System.out.println("\nSelect a room to clean:");
        for (int i = 0; i < available.size(); i++) {
            HousekeepingRequest req = available.get(i);
            System.out.println((i + 1) + ". " + req.formatTicketLine());
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
        if (choice < 1 || choice > available.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        HousekeepingRequest selected = available.get(choice - 1);
        cleaningQueue.remove(selected);
        performCleaning(selected);
    }

    private static void performCleaning(HousekeepingRequest request) {
        room roomToClean = request.getRoomInfo();
        synchronized (roomToClean) {
            System.out.println("Cleaning started for Room " + roomToClean.getRoomNumber() + "...");
            System.out.println("Service: " + request.getServiceType() + " | Window: " + request.getTimeWindow());
            if (!request.getAmenities().isEmpty()) {
                System.out.println("Amenities to deliver: " + String.join(", ", request.getAmenities()));
            }
            if (request.getNotes() != null && !request.getNotes().isEmpty()) {
                System.out.println("Notes: " + request.getNotes());
            }
            try {
                Thread.sleep(1500); // Simulate cleaning
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Cleaning complete for Room " + roomToClean.getRoomNumber());
        }
    }

    @SuppressWarnings("unused")
    private static void fireAllWorkers() {
        active = false;
        System.out.println("All housekeeping operations halted.");
    }
}
