package Main.Guest;

import Main.Booking.Reservation;
import Main.Employee.Employee;
import Main.Employee.FrontDesk;
import Main.Employee.Housekeeping;
import Main.Employee.HousekeepingRequest;
import Main.Employee.KitchenOrder;
import Main.Employee.KitchenPanel;
import Main.Main;
import Main.Room.room;

import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class GuestSession implements CheckingProcess {

    private static final List<String> SERVICE_TYPES = List.of(
        "Full Cleaning",
        "Quick Tidy",
        "Turn-Down",
        "Amenity Request Only"
    );

    private static final List<String> TIME_WINDOWS = List.of(
        "As soon as possible",
        "08:00 - 10:00",
        "10:00 - 12:00",
        "14:00 - 16:00",
        "18:00 - 20:00"
    );

    private static final List<String> AMENITY_OPTIONS = List.of(
        "Extra Towels",
        "Toiletries",
        "Extra Pillows",
        "Fresh Linens",
        "Coffee Pods",
        "Bottled Water"
    );

    @Override
    @SuppressWarnings("resource")
    public void checkin(Employee frontDesk) {
        Scanner scnr = new Scanner(System.in);

        System.out.print("Enter your name: ");
        String name = scnr.nextLine();

        System.out.println("Okay, and could you provide your id please?");
        int id = scnr.nextInt();
        scnr.nextLine(); // consume newline

        Guest guest = new Guest(name, id);

        boolean check = ((FrontDesk) frontDesk).verifyIdentity(guest);

        if (check) {
            System.out.println("Thank you. Give me a moment to verify your reservation");
            Reservation reserve = ((FrontDesk) frontDesk).verifyCheckIn(guest);

            if (reserve != null) {
                System.out.println("\nThanks for waiting, you have a reservation from "
                        + reserve.getStartDate() + " to " + reserve.getEndDate() + "\n");

                // Create a new session bill for this guest
                BillManager.createBillForRoom(reserve.getRoomNumber(), guest.getName());
                String location = Main.HOTEL_PATH;
                BillingManager.initBillingForStay(reserve, guest.getName(), location);

                ((FrontDesk) frontDesk).provideKeyCard(reserve.getRoomNumber(), guest);
                System.out.println("Here is your keycard for room " + reserve.getRoomNumber());
            } else {
                System.out.println("No reservation found for your details.");
            }
        } else {
            System.out.println("Sorry, we couldn't verify your identity.");
        }
    }

    @Override
    @SuppressWarnings("resource")
    public void checkout(Employee frontDesk) {
        Scanner scnr = new Scanner(System.in);

        LocalTime now = LocalTime.now();
        String greeting;

        if (now.isBefore(java.time.LocalTime.NOON))
            greeting = "Good morning";
        else if (now.isBefore(java.time.LocalTime.of(17, 0)))
            greeting = "Good afternoon";
        else if (now.isBefore(java.time.LocalTime.of(21, 0)))
            greeting = "Good evening";
        else
            greeting = "Good night";

        System.out.println(greeting + "! Checking out today?");

        System.out.print("Can I get the name on the reservation? ");
        String name = scnr.nextLine();

        System.out.print("And the ID you used during check-in? ");
        int id = scnr.nextInt();
        scnr.nextLine(); // consume leftover newline

        Guest guest = new Guest(name, id);

        boolean verified = ((FrontDesk) frontDesk).verifyIdentity(guest);

        if (!verified) {
            System.out.println("Hmm, I'm not finding a match with that name and ID. Could you double-check them?");
            return;
        }

        boolean checkedOut = ((FrontDesk) frontDesk).verifyCheckOut(guest);

        if (checkedOut) {
            System.out.println("Alright, " + guest.getName() + ", I found your reservation.");
            System.out.println("Your checkout is all set. I'll take your keycard—thank you!");

            // Print final bill for **this guest only**
            System.out.println("\nCalculating final bill...");
            BillManager.printBillForGuestByName(guest.getName());

            System.out.println("\nWe hope you had a pleasant stay. Safe travels!");
        } else {
            System.out.println("It looks like you don't have any active reservations right now.");
        }
    }

    /**
     * Called when guest accesses room via keycard.
     * Allows them to add MiniBar or RoomService charges.
     */
    @SuppressWarnings("resource")
    public static void roomAccess(int roomNumber, String guestName, room accessedRoom) {
        Scanner scnr = new Scanner(System.in);

        System.out.println("===== ROOM ACCESS =====");
        System.out.println("Welcome to your room! You can add charges for MiniBar or RoomService,");
        System.out.println("and you can request Housekeeping directly from here.");
        System.out.println("Type 'DONE' when finished.\n");

        // Define items and prices
        Map<String, Double> miniBarItems = new HashMap<>();
        miniBarItems.put("Chips", 2.0);
        miniBarItems.put("Chocolate", 3.0);
        miniBarItems.put("Soda", 1.5);
        miniBarItems.put("Water", 1.0);

        Map<String, Double> roomServiceItems = new HashMap<>();
        roomServiceItems.put("Sandwich", 5.0);
        roomServiceItems.put("Salad", 6.0);
        roomServiceItems.put("Pizza", 8.0);
        roomServiceItems.put("Coffee", 2.0);

    Map<String, Double> sessionCharges = new HashMap<>();
    Map<String, Integer> roomServiceCounts = new LinkedHashMap<>();
    double roomServiceTotal = 0.0;
    boolean housekeepingRequested = false;

        while (true) {
            System.out.print("Enter type (MiniBar / RoomService / Housekeeping) or DONE: ");
            String type = scnr.nextLine().trim();

            if (type.equalsIgnoreCase("DONE")) break;

            if (type.equalsIgnoreCase("MiniBar")) {
                System.out.println("Available MiniBar items:");
                miniBarItems.forEach((item, price) -> System.out.printf("- %s ($%.2f)\n", item, price));

                System.out.print("Select an item: ");
                String item = scnr.nextLine().trim();

                if (miniBarItems.containsKey(item)) {
                    double price = miniBarItems.get(item);
                    sessionCharges.put(item, sessionCharges.getOrDefault(item, 0.0) + price);
                    System.out.printf("%s charge of $%.2f added to your bill.\n", item, price);
                } else {
                    System.out.println("Invalid item.");
                }

            } else if (type.equalsIgnoreCase("RoomService")) {
                System.out.println("Available RoomService items:");
                roomServiceItems.forEach((item, price) -> System.out.printf("- %s ($%.2f)\n", item, price));

                System.out.print("Select an item: ");
                String item = scnr.nextLine().trim();

                if (roomServiceItems.containsKey(item)) {
                    double price = roomServiceItems.get(item);
                    sessionCharges.put(item, sessionCharges.getOrDefault(item, 0.0) + price);
                    roomServiceCounts.put(item, roomServiceCounts.getOrDefault(item, 0) + 1);
                    roomServiceTotal += price;
                    System.out.printf("%s charge of $%.2f added to your bill.\n", item, price);
                } else {
                    System.out.println("Invalid item.");
                }

            } else if (type.equalsIgnoreCase("Housekeeping")) {
                if (accessedRoom == null) {
                    System.out.println("Room context unavailable. Try again later or contact the front desk.");
                    continue;
                }

                boolean queued = handleHousekeepingFlow(scnr, accessedRoom, guestName);
                if (queued) {
                    housekeepingRequested = true;
                }
            } else {
                System.out.println("Invalid type. Choose MiniBar, RoomService, Housekeeping, or DONE.");
            }
        }

        // Write session charges to file under this guest's header
        BillManager.writeSessionBill(roomNumber, guestName, sessionCharges);

        if (!roomServiceCounts.isEmpty()) {
            KitchenOrder order = new KitchenOrder(
                    Main.HOTEL_PATH,
                    roomNumber,
                    guestName,
                    roomServiceCounts,
                    roomServiceTotal
            );
            KitchenPanel.submitOrder(order);
        }

        if (housekeepingRequested) {
            System.out.println("Housekeeping has been notified for your room. Someone will visit shortly.");
        }

        // Display current totals based on persisted file entries
        try {
            List<String[]> recorded = BillManager.readLastSessionForGuest(roomNumber, guestName);
            if (recorded.isEmpty()) {
                System.out.println("\nNo bill entries found for this guest yet.");
                return;
            }

            System.out.println("\n===== CURRENT BILL FOR ROOM " + roomNumber + " =====");
            double runningTotal = 0.0;
            for (String[] line : recorded) {
                if (line.length < 2) {
                    continue;
                }
                String label = line[0];
                String detail = line.length >= 3 ? line[1] : "";
                String amountStr = line.length >= 3 ? line[2] : line[1];

                double amount;
                try {
                    amount = Double.parseDouble(amountStr);
                } catch (NumberFormatException ex) {
                    continue;
                }

                runningTotal += amount;
                System.out.printf("%-14s %-14s $%6.2f%n", label, detail, amount);
            }
            System.out.println("----------------------------------------");
            System.out.printf("TOTAL $%6.2f%n%n", runningTotal);
        } catch (IOException e) {
            System.out.println("Unable to read current bill: " + e.getMessage());
        }
    }

    private static boolean handleHousekeepingFlow(Scanner sc,
                                                 room accessedRoom,
                                                 String guestName) {
        System.out.println("\n===== HOUSEKEEPING REQUEST =====");
        System.out.println("We'll gather a few details to schedule your service.");

        String serviceType = promptFromList(sc, SERVICE_TYPES, "Select a service type (1-" + SERVICE_TYPES.size() + "): ");
        if (serviceType == null) {
            return false;
        }

        String timeWindow = promptFromList(sc, TIME_WINDOWS, "Preferred time window (1-" + TIME_WINDOWS.size() + "): ");
        if (timeWindow == null) {
            return false;
        }

        boolean dndActive = askYesNo(sc, "Is your room currently set to 'Do Not Disturb'? (y/n): ");
        timeWindow = adjustTimeWindow(timeWindow, dndActive);

        List<String> amenities = promptAmenities(sc);

        String additionalNotes = "";
        if (askYesNo(sc, "Any additional notes or requests? (y/n): ")) {
            System.out.print("Enter notes: ");
            additionalNotes = sc.nextLine().trim();
        }

        System.out.println("\n--- Request Summary ---");
        System.out.println("Room: " + accessedRoom.getRoomNumber());
        System.out.println("Service: " + serviceType);
        System.out.println("Window: " + timeWindow);
        if (!amenities.isEmpty()) {
            System.out.println("Amenities: " + String.join(", ", amenities));
        } else {
            System.out.println("Amenities: None");
        }
        if (!additionalNotes.isEmpty()) {
            System.out.println("Notes: " + additionalNotes);
        }

        if (!askYesNo(sc, "Submit this housekeeping request? (y/n): ")) {
            System.out.println("Request cancelled.");
            return false;
        }

        String finalNotes = buildNotes(additionalNotes, dndActive);
        HousekeepingRequest request = new HousekeepingRequest(
                cloneRoomForTicket(accessedRoom),
                serviceType,
                timeWindow,
                amenities,
                finalNotes,
                true,
                guestName
        );

        boolean accepted = Housekeeping.submitGuestRequest(request);
        if (accepted) {
            System.out.println("Confirmed: Housekeeping has your request. You'll receive service " + timeWindow + ".");
        }
        return accepted;
    }

    private static String promptFromList(Scanner sc, List<String> options, String prompt) {
        for (int i = 0; i < options.size(); i++) {
            System.out.println((i + 1) + ". " + options.get(i));
        }
        System.out.print(prompt);
        String input = sc.nextLine().trim();
        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx >= 0 && idx < options.size()) {
                return options.get(idx);
            }
        } catch (NumberFormatException ignored) {
        }
        System.out.println("Invalid selection.");
        return null;
    }

    private static boolean askYesNo(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = sc.nextLine().trim().toLowerCase();
            if (input.equals("y") || input.equals("yes")) return true;
            if (input.equals("n") || input.equals("no")) return false;
            System.out.println("Please enter 'y' or 'n'.");
        }
    }

    private static String adjustTimeWindow(String selection, boolean dndActive) {
        LocalTime now = LocalTime.now();
        LocalTime serviceStart = LocalTime.of(7, 0);
        LocalTime serviceEnd = LocalTime.of(21, 0);

        if (selection.equalsIgnoreCase("As soon as possible")) {
            if (dndActive) {
                System.out.println("⚠️ Room is set to Do Not Disturb. Scheduling the earliest afternoon slot instead.");
                return "14:00 - 16:00 (after DND)";
            }
            if (now.isBefore(serviceStart) || now.isAfter(serviceEnd.minusMinutes(30))) {
                System.out.println("Housekeeping is unavailable right now. Scheduling for 08:00 - 10:00 next service window.");
                return "08:00 - 10:00 (next service window)";
            }
            return "As soon as possible (within 30 mins)";
        }

        Map<String, LocalTime[]> bounds = timeWindowBounds();
        LocalTime[] windowBounds = bounds.get(selection);
        if (windowBounds != null && now.isAfter(windowBounds[1])) {
            System.out.println("That time slot has passed. Scheduling the next available slot instead.");
            return "14:00 - 16:00";
        }

        if (now.isAfter(serviceEnd)) {
            System.out.println("Service hours have ended. Scheduling for tomorrow morning 08:00 - 10:00.");
            return "08:00 - 10:00 (next day)";
        }

        return selection;
    }

    private static Map<String, LocalTime[]> timeWindowBounds() {
        Map<String, LocalTime[]> bounds = new HashMap<>();
        bounds.put("08:00 - 10:00", new LocalTime[]{LocalTime.of(8, 0), LocalTime.of(10, 0)});
        bounds.put("10:00 - 12:00", new LocalTime[]{LocalTime.of(10, 0), LocalTime.of(12, 0)});
        bounds.put("14:00 - 16:00", new LocalTime[]{LocalTime.of(14, 0), LocalTime.of(16, 0)});
        bounds.put("18:00 - 20:00", new LocalTime[]{LocalTime.of(18, 0), LocalTime.of(20, 0)});
        return bounds;
    }

    private static List<String> promptAmenities(Scanner sc) {
        System.out.println("Optional amenities (separate multiple choices with commas, or press Enter for none):");
        for (int i = 0; i < AMENITY_OPTIONS.size(); i++) {
            System.out.println((i + 1) + ". " + AMENITY_OPTIONS.get(i));
        }
        System.out.print("Amenities: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()) {
            return Collections.emptyList();
        }

        String[] tokens = input.split(",");
        List<String> selections = new ArrayList<>();
        for (String token : tokens) {
            String trimmed = token.trim();
            try {
                int idx = Integer.parseInt(trimmed) - 1;
                if (idx >= 0 && idx < AMENITY_OPTIONS.size()) {
                    selections.add(AMENITY_OPTIONS.get(idx));
                }
            } catch (NumberFormatException e) {
                if (AMENITY_OPTIONS.contains(trimmed)) {
                    selections.add(trimmed);
                }
            }
        }
        return selections;
    }

    private static String buildNotes(String userNotes, boolean dndActive) {
        List<String> notes = new ArrayList<>();
        if (dndActive) {
            notes.add("Guest indicated Do Not Disturb was active. Ensure DND sign is cleared at arrival.");
        }
        if (userNotes != null && !userNotes.isEmpty()) {
            notes.add(userNotes);
        }
        return String.join(" | ", notes);
    }

    private static room cloneRoomForTicket(room original) {
        return new room(
                original.getAddress(),
                original.getRoomNumber(),
                original.getType(),
                original.getAvailability(),
                original.getStartDate(),
                original.getEndDate()
        );
    }
}
