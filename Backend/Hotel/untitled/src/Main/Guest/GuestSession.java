package Main.Guest;

import Main.Booking.Reservation;
import Main.Employee.Employee;
import Main.Employee.FrontDesk;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class GuestSession implements CheckingProcess {

    @Override
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
    public static void roomAccess(int roomNumber, String guestName) {
        Scanner scnr = new Scanner(System.in);

        System.out.println("===== ROOM ACCESS =====");
        System.out.println("Welcome to your room! You can add charges for MiniBar or RoomService.");
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

        while (true) {
            System.out.print("Enter charge type (MiniBar / RoomService) or DONE: ");
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
                    System.out.printf("%s charge of $%.2f added to your bill.\n", item, price);
                } else {
                    System.out.println("Invalid item.");
                }

            } else {
                System.out.println("Invalid type. Only MiniBar or RoomService allowed.");
            }
        }

        // Write session charges to file under this guest's header
        BillManager.writeSessionBill(roomNumber, guestName, sessionCharges);

        // Display session totals
        System.out.println("\n===== CURRENT BILL FOR ROOM " + roomNumber + " =====");
        double total = 0.0;
        for (Map.Entry<String, Double> entry : sessionCharges.entrySet()) {
            System.out.printf("%-12s $%6.2f\n", entry.getKey(), entry.getValue());
            total += entry.getValue();
        }
        System.out.println("----------------------------------------");
        System.out.printf("TOTAL $%6.2f\n\n", total);
    }

}
