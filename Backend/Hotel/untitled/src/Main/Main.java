package Main;

import java.io.FileNotFoundException;
import java.util.*;
import java.util.Scanner;
import java.io.File;

import Main.Booking.Booking;
import Main.Employee.*;
import Main.Guest.CheckSystemController;
import Main.Room.room;
import Main.Guest.Guest;
import Main.Guest.KeyCard;
import Main.Guest.RoomAccessResult;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        File file = new File("Employee.txt");
        Scanner scnr = new Scanner(file);

        ArrayList<Employee> employees = new ArrayList<>();
        ArrayList<Housekeeping> housekeepings = new ArrayList<>();
        frontdeskteam FDManager = null;
        Housekeeping HKManager = null;

        // Get List of Employees Working
        while (scnr.hasNextLine()) {
            String line = scnr.nextLine();
            String[] employee = line.split(" ");

            if (employee.length < 4) continue; // prevent index error

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
            printMenu();

            System.out.print("\nSelect an option (1–6) or 0 to exit: ");
            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                Booking.handleBooking();
            } else if (choice.equals("2")) {
                System.out.println("Check In option selected.");
                Employee frontDesk = null;

                // Find available FrontDesk Employee
                for (Employee e : employees) {
                    if (e.role().equals("FrontDesk")) {
                        frontDesk = e;
                    }
                }

                CheckSystemController.RunCheckin(frontDesk);

            } else if (choice.equals("6")) {
                handleRoomAccess(sc);
            } else if (choice.equals("3")) {
                System.out.println("Check Out option selected.");
                Employee frontDesk = null;

                // Find available FrontDesk Employee
                for (Employee e : employees) {
                    if (e.role().equals("FrontDesk")) {
                        frontDesk = e;
                    }
                }

                // Call the checkout system
                CheckSystemController.RunCheckOut(frontDesk);
                Thread.sleep(90000);

            } else if (choice.equals("0")) {
                break;

            } else if (choice.equals("9")) {
                int RoomNumber = 10;
                int i = 0;
                while (i < 3) {
                    housekeepings.add(new Housekeeping(i, "alpha"));
                    System.out.println("added new housekeeping " + i);
                    i++;
                }
                while (true) {
                    housekeepings.get(0).addToCleanQueue(new room(RoomNumber, null, null, null, null));
                    RoomNumber += 1;
                }

            } else {
                System.out.println("That's not an option try again");
                continue;
            }

            if (running) {
                System.out.println("\nPress Enter to return to main menu...");
                sc.nextLine();
            }
        }
        scnr.close();
    }

    private static void printMenu() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║             HOTEL ERP SYSTEM           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Book                                ║");
        System.out.println("║ 2. Check In                            ║");
        System.out.println("║ 3. Check Out                           ║");
        System.out.println("║ 4. Front Desk                          ║");
        System.out.println("║ 5. Cleaning Staff                      ║");
        System.out.println("║ 6. Access Room (Key Card)              ║");
        System.out.println("║ 0. Exit                                ║");
        System.out.println("╚════════════════════════════════════════╝");
    }

    private static void handleRoomAccess(Scanner sc) {
        new RoomAccessFlow(sc).execute();
    }

    private static final class RoomAccessFlow {
        private static final String DATA_ROOT = "Backend/Hotel/untitled/src/Main/";
        private static final String FALLBACK_ROOT = "src/Main/";

        private final Scanner input;

        RoomAccessFlow(Scanner input) {
            this.input = input;
        }

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
                System.out.println("Access denied: incomplete information provided.");
                System.out.println("Indicator flashes red. Please contact the front desk.");
                return;
            }

            Optional<KeyCard> keyCard = findMatchingCard(issuedCards, roomNumber, guestName);
            if (keyCard.isEmpty()) {
                System.out.println(RoomAccessResult.INVALID_GUEST.getMessage());
                System.out.println("Indicator flashes red. Please contact the front desk.");
                return;
            }

            Optional<room> targetRoom = loadRoom(roomNumber);
            if (targetRoom.isEmpty()) {
                System.out.println(RoomAccessResult.SYSTEM_ERROR.getMessage());
                System.out.println("Maintenance notified. Please visit the front desk.");
                return;
            }

            Guest guest = new Guest(guestName, -1);
            RoomAccessResult result = guest.scanKeyCard(keyCard.get(), targetRoom.get());
            System.out.println(result.getMessage());

            switch (result) {
                case ACCESS_GRANTED:
                    System.out.println("Door for room " + roomNumber + " is now unlocked.");
                    break;
                case ROOM_ALREADY_UNLOCKED:
                    System.out.println("Door was already unlocked. Please enter.");
                    break;
                case SYSTEM_ERROR:
                    System.out.println("Maintenance notified. Please visit the front desk.");
                    break;
                default:
                    System.out.println("Indicator flashes red. Please contact the front desk.");
                    break;
            }
        }

        private List<KeyCard> loadIssuedKeyCards() {
            File source = resolveDataFile("Keycard.txt");
            List<KeyCard> cards = new ArrayList<>();
            if (!source.exists()) {
                return cards;
            }

            try (Scanner scanner = new Scanner(source)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] tokens = line.split("\\s+");
                    if (tokens.length < 3) {
                        continue;
                    }

                    int recordedRoom = parseNumber(tokens[0]);
                    if (recordedRoom <= 0) {
                        continue;
                    }

                    boolean isActive = Boolean.parseBoolean(tokens[1]);
                    String ownerName = rebuildName(tokens);
                    if (ownerName.isEmpty()) {
                        continue;
                    }

                    cards.add(new KeyCard(recordedRoom, isActive, new Guest(ownerName, -1)));
                }
            } catch (FileNotFoundException e) {
                cards.clear();
            }

            return cards;
        }

        private Optional<KeyCard> findMatchingCard(List<KeyCard> cards, int roomNumber, String guestName) {
            KeyCard match = null;
            for (KeyCard card : cards) {
                if (card.getRoomnumber() == roomNumber &&
                        card.getOwner().getName().equalsIgnoreCase(guestName)) {
                    match = card;
                }
            }
            return Optional.ofNullable(match);
        }

        private Optional<room> loadRoom(int roomNumber) {
            if (roomNumber <= 0) {
                return Optional.empty();
            }

            File source = resolveDataFile("Room.txt");
            if (!source.exists()) {
                return Optional.empty();
            }

            try (Scanner scanner = new Scanner(source)) {
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    String[] parts = line.split(",");
                    if (parts.length < 2) {
                        continue;
                    }

                    int recordedRoom = parseNumber(parts[0].trim());
                    if (recordedRoom != roomNumber) {
                        continue;
                    }

                    String type = parts[1].trim();
                    room entry = new room(recordedRoom, type, "Reserved", null, null);
                    entry.lock();
                    return Optional.of(entry);
                }
            } catch (FileNotFoundException e) {
                return Optional.empty();
            }

            return Optional.empty();
        }

        private File resolveDataFile(String fileName) {
            String[] prefixes = {
                    "",
                    DATA_ROOT,
                    FALLBACK_ROOT,
                    "Backend/Hotel/untitled/",
                    "Backend/Hotel/"
            };

            for (String prefix : prefixes) {
                File candidate = prefix.isEmpty() ? new File(fileName) : new File(prefix + fileName);
                if (candidate.exists()) {
                    return candidate;
                }
            }

            return new File(DATA_ROOT + fileName);
        }

        private int parseNumber(String value) {
            if (value == null || value.isEmpty()) {
                return -1;
            }
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                return -1;
            }
        }

        private String rebuildName(String[] tokens) {
            if (tokens.length <= 2) {
                return "";
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 2; i < tokens.length; i++) {
                if (i > 2) {
                    builder.append(' ');
                }
                builder.append(tokens[i]);
            }
            return builder.toString();
        }
    }
}