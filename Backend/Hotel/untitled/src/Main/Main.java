package Main;

import java.io.FileNotFoundException;
import java.util.*;
import Main.Booking.Booking;
import java.util.Scanner;
import java.io.File;
import Main.Employee.*;
import Main.Guest.CheckSystemController;
import Main.Guest.Guest;
import Main.Guest.KeyCard;
import Main.Guest.RoomAccessResult;
import Main.Room.room;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        File file = new File("Backend/Hotel/untitled/src/Main/Employee.txt");


        Scanner scnr = new Scanner(file);

        ArrayList<Employee> employees = new ArrayList<>();

        //Get List of Employees Working
        while(scnr.hasNextLine()){
            String line = scnr.nextLine();
            String[] employee = line.split(" ");

            int id = Integer.parseInt(employee[0]);
            String name = employee[1] + employee[2];
            String position = employee[3];

            if(position.equals("FrontDesk")){
                employees.add(new frontdeskteam(id, name));
            }
            else if (position.equals("Cleaner")){
                employees.add(new cleaningstaffteam(id, name));
            }

        }

        while (running) {
            printMenu();

            System.out.print("\nSelect an option (1–5) or 0 to exit: ");
            String choice = sc.nextLine().trim();

            if(choice.equals("1")){
                Booking.handleBooking();
                break;
            }
            else if(choice.equals("2")) {
                System.out.println("Check In option selected.");
                Employee frontDesk = null;

                //Find available FrontDesk Employee
                for (Employee e : employees) {
                    if (e.role().equals("FrontDesk")) {
                        frontDesk = e;
                        break;
                    }
                }

                CheckSystemController.RunCheckin(frontDesk);
                break;
            }
            else if(choice.equals("6")) {
                handleRoomAccess(sc);
                break;
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
        System.out.println("===== ROOM ACCESS AUTHENTICATION =====");

        System.out.print("Guest name: ");
        String guestName = sc.nextLine().trim();

        System.out.print("Guest id (press Enter if unknown): ");
        int guestId = parseNumber(sc.nextLine().trim());

        System.out.print("Room number: ");
        int roomNumber = parseNumber(sc.nextLine().trim());

        Guest guest = new Guest(guestName, guestId);

        Optional<room> targetRoom = loadRoom(roomNumber);
        if (targetRoom.isEmpty()) {
            System.out.println(RoomAccessResult.SYSTEM_ERROR.getMessage());
            System.out.println("Maintenance notified. Please visit the front desk.");
            return;
        }

        Optional<KeyCard> keyCard = findKeyCardFor(guest, roomNumber);
        if (keyCard.isEmpty()) {
            System.out.println(RoomAccessResult.SYSTEM_ERROR.getMessage());
            System.out.println("Please request assistance at the front desk.");
            return;
        }

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

    private static Optional<KeyCard> findKeyCardFor(Guest guest, int roomNumber) {
        File source = resolveDataFile("Keycard.txt");
        if (!source.exists()) {
            return Optional.empty();
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
                boolean isActive = Boolean.parseBoolean(tokens[1]);
                String ownerName = rebuildName(tokens);

                if (recordedRoom != roomNumber) {
                    continue;
                }

                Guest owner = new Guest(ownerName, -1);
                KeyCard card = new KeyCard(recordedRoom, isActive, owner);

                if (guest.matches(owner)) {
                    return Optional.of(card);
                }
            }
        } catch (FileNotFoundException e) {
            return Optional.empty();
        }

        return Optional.empty();
    }

    private static Optional<room> loadRoom(int roomNumber) {
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

    private static File resolveDataFile(String fileName) {
        File rooted = new File("Backend/Hotel/untitled/src/Main/" + fileName);
        if (rooted.exists()) {
            return rooted;
        }
        return new File("src/Main/" + fileName);
    }

    private static int parseNumber(String value) {
        if (value == null || value.isEmpty()) {
            return -1;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private static String rebuildName(String[] tokens) {
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

