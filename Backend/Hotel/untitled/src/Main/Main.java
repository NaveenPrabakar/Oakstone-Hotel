package Main;

import java.io.FileNotFoundException;
import java.util.*;
import java.io.File;

import Main.Booking.Booking;
import Main.Employee.*;
import Main.Guest.CheckSystemController;
import Main.Room.room;
import Main.Guest.Guest;
import Main.Guest.KeyCard;
import Main.Guest.RoomAccessResult;

public class Main {
    public static String HOTEL_PATH = "";

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("====================================");
            System.out.println("   Welcome to the HOTEL ERP System   ");
            System.out.println("====================================");
            System.out.print("Are you a Guest or a Worker? (G/W) or Q to quit: ");
            String userType = sc.nextLine().trim().toUpperCase();

            if (userType.equals("G")) {
                runGuestPortal(sc);
            }
            else if (userType.equals("W")) {
                System.out.print("Enter the Hotel Address you’re working at: ");
                String hotelAddress = sc.nextLine().trim();
                HOTEL_PATH = hotelAddress.isEmpty() ? "123 Main St, Iowa" : hotelAddress;
                runWorkerPortal(sc, HOTEL_PATH);
            }
            else if (userType.equals("Q")) {
                System.out.println("Exiting system... Goodbye!");
                running = false;
            }
            else {
                System.out.println("Invalid selection. Please choose Guest (G), Worker (W), or Quit (Q).");
            }
        }

        sc.close();
    }

    private static void runGuestPortal(Scanner sc) throws FileNotFoundException, InterruptedException {
        boolean running = true;

        while (running) {
            System.out.println("\n╔════════════════════════════════════╗");
            System.out.println("║           GUEST PORTAL             ║");
            System.out.println("╠════════════════════════════════════╣");
            System.out.println("║ 1. Book a Room                     ║");
            System.out.println("║ 2. Check In                        ║");
            System.out.println("║ 3. Check Out                       ║");
            System.out.println("║ 4. Access Room (Key Card)          ║");
            System.out.println("║ 0. Exit                            ║");
            System.out.println("╚════════════════════════════════════╝");
            System.out.print("Select an option: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                Booking.handleBooking();
            } else if (choice.equals("2")) {
                System.out.println("Check In option selected.");
                CheckSystemController.RunCheckin(null);
            } else if (choice.equals("3")) {
                System.out.println("Check Out option selected.");
                CheckSystemController.RunCheckOut(null);
            } else if (choice.equals("4")) {
                handleRoomAccess(sc);
            } else if (choice.equals("0")) {
                System.out.println("Thank you for visiting our hotel. Goodbye!");
                running = false;
            } else {
                System.out.println("Invalid option. Please choose 0–4.");
            }
        }
    }

    // ============================================================
    //  Worker Portal (Employees, Data Team, Executives, etc.)
    // ============================================================
    private static void runWorkerPortal(Scanner sc, String hotelAddress) throws FileNotFoundException, InterruptedException {
        boolean running = true;

        File file = new File(Main.HOTEL_PATH + "/" + "Employee.txt");
        Scanner scnr = new Scanner(file);

        ArrayList<Employee> employees = new ArrayList<>();
        ArrayList<Housekeeping> housekeepings = new ArrayList<>();
        frontdeskteam FDManager = null;
        Housekeeping HKManager = null;

        // Load employee data
        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            String[] employee = line.split(" ");
            if (employee.length < 4) continue;

            int id = Integer.parseInt(employee[0]);
            String name = employee[1] + employee[2];
            String position = employee[3];

            if (position.equals("FrontDesk")) {
                FDManager = new frontdeskteam(id, name);
                employees.add(FDManager);
            } else if (position.equals("Cleaner")) {
                HKManager = new Housekeeping(id, name);
                employees.add(HKManager);
            }
        }

        if (FDManager != null && HKManager != null) {
            FDManager.addHouseKeepingManager(HKManager);
        }

        while (running) {
            printWorkerMenu();

            System.out.print("\nSelect an option (1–8) or 0 to exit: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                Booking.handleBooking();
            } else if (choice.equals("2")) {
                Employee frontDesk = new frontdeskteam(1, "FrontDesk");
                CheckSystemController.RunCheckin(frontDesk);

            } else if (choice.equals("3")) {

                Employee frontDesk = new frontdeskteam(1, "FrontDesk");
                CheckSystemController.RunCheckOut(frontDesk);

            } else if (choice.equals("4")) {
                System.out.print("Enter your name to log in: ");
                String name = sc.nextLine().trim();
                frontdeskteam.processQueue(name);

            } else if (choice.equals("5")) {
                System.out.print("Enter your name to log in as cleaner: ");
                String cleanerName = sc.nextLine().trim();
                Housekeeping.processCleaning(cleanerName);

            } else if (choice.equals("6")) {
                handleRoomAccess(sc);

            } else if (choice.equals("7")) {
                System.out.print("Enter your name to log in as Executive: ");
                String name = sc.nextLine().trim();
                ExecutiveController.runExecutivePanel(name);

            } else if (choice.equals("8")) {
                System.out.print("Enter your name to log in as Data Team member: ");
                String name = sc.nextLine().trim();
                DataTeamController.runDataAnalysis(name);

            } else if (choice.equals("0")) {
                System.out.println("Logging out of worker panel...");
                running = false;
            } else {
                System.out.println("Invalid option. Please enter 0–8.");
            }
        }
    }

    private static void printWorkerMenu() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║             HOTEL ERP SYSTEM           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 4. Front Desk Panel                    ║");
        System.out.println("║ 5. Cleaning Panel                      ║");
        System.out.println("║ 7. Executive Panel                     ║");
        System.out.println("║ 8. Data Team Panel                     ║");
        System.out.println("║ 0. Exit                                ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void handleRoomAccess(Scanner sc) {
        System.out.print("Enter the hotel location you’re accessing (e.g., Chicago, DesMoines): ");
        String hotel = sc.nextLine().trim();

        if (hotel.isEmpty()) {
            System.out.println("No location entered. Returning to menu.");
            return;
        }

        Main.HOTEL_PATH = hotel;
        new RoomAccessFlow(sc).execute();
    }


    private static final class RoomAccessFlow {
        private static final String DATA_ROOT = "Backend/Hotel/untitled/src/Main/";
        private static final String FALLBACK_ROOT = "src/Main/";
        private final Scanner input;

        RoomAccessFlow(Scanner input) { this.input = input; }

        void execute() {
            System.out.println("===== ROOM ACCESS AUTHENTICATION =====");
            List<KeyCard> issuedCards = loadIssuedKeyCards();
            if (issuedCards.isEmpty()) {
                System.out.println("No key cards are currently issued. Please visit the front desk.");
                return;
            }

            System.out.println("Key cards on file:");
            for (KeyCard card : issuedCards) {
                System.out.println("  Room " + card.getRoomnumber() + " -> " + card.getOwner().getName());
            }

            System.out.print("Room number: ");
            int roomNumber = parseNumber(input.nextLine().trim());
            System.out.print("Name on key card: ");
            String guestName = input.nextLine().trim();

            if (roomNumber <= 0 || guestName.isEmpty()) {
                System.out.println("Access denied: incomplete information.");
                return;
            }

            Optional<KeyCard> keyCard = findMatchingCard(issuedCards, roomNumber, guestName);
            if (keyCard.isEmpty()) {
                System.out.println(RoomAccessResult.INVALID_GUEST.getMessage());
                return;
            }

            Optional<room> targetRoom = loadRoom(roomNumber);
            if (targetRoom.isEmpty()) {
                System.out.println(RoomAccessResult.SYSTEM_ERROR.getMessage());
                return;
            }

            Guest guest = new Guest(guestName, -1);
            RoomAccessResult result = guest.scanKeyCard(keyCard.get(), targetRoom.get());
            System.out.println(result.getMessage());
        }

        private List<KeyCard> loadIssuedKeyCards() {
            File source = resolveDataFile(Main.HOTEL_PATH +"/"+"Keycard.txt");
            List<KeyCard> cards = new ArrayList<>();
            if (!source.exists()) return cards;

            try (Scanner scanner = new Scanner(source)) {
                while (scanner.hasNextLine()) {
                    String[] tokens = scanner.nextLine().trim().split("\\s+");
                    if (tokens.length >= 3) {
                        int recordedRoom = parseNumber(tokens[0]);
                        boolean isActive = Boolean.parseBoolean(tokens[1]);
                        String ownerName = rebuildName(tokens);
                        cards.add(new KeyCard(recordedRoom, isActive, new Guest(ownerName, -1)));
                    }
                }
            } catch (FileNotFoundException e) {
                cards.clear();
            }

            return cards;
        }

        private Optional<KeyCard> findMatchingCard(List<KeyCard> cards, int roomNumber, String guestName) {
            return cards.stream()
                    .filter(c -> c.getRoomnumber() == roomNumber &&
                            c.getOwner().getName().equalsIgnoreCase(guestName))
                    .findFirst();
        }

        private Optional<room> loadRoom(int roomNumber) {
            File source = resolveDataFile(Main.HOTEL_PATH +"/"+"Room.txt");
            if (!source.exists()) return Optional.empty();

            try (Scanner scanner = new Scanner(source)) {
                if (scanner.hasNextLine()) scanner.nextLine();
                while (scanner.hasNextLine()) {
                    String[] parts = scanner.nextLine().split(",");
                    if (parts.length >= 2) {
                        int recordedRoom = parseNumber(parts[0].trim());
                        if (recordedRoom == roomNumber) {
                            String type = parts[1].trim();
                            room entry = new room(Main.HOTEL_PATH, recordedRoom, type, "Reserved", null, null);
                            entry.lock();
                            return Optional.of(entry);
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                return Optional.empty();
            }

            return Optional.empty();
        }

        private File resolveDataFile(String fileName) {
            String[] prefixes = {"", DATA_ROOT, FALLBACK_ROOT, "Backend/Hotel/untitled/", "Backend/Hotel/"};
            for (String prefix : prefixes) {
                File candidate = prefix.isEmpty() ? new File(fileName) : new File(prefix + fileName);
                if (candidate.exists()) return candidate;
            }
            return new File(DATA_ROOT + fileName);
        }

        private int parseNumber(String value) {
            try { return Integer.parseInt(value); }
            catch (NumberFormatException ex) { return -1; }
        }

        private String rebuildName(String[] tokens) {
            return String.join(" ", Arrays.copyOfRange(tokens, 2, tokens.length));
        }
    }
}
