package Main.Booking;

import Main.Room.room;
import java.io.*;
import java.time.LocalDate;
import java.util.*;
import Main.*;

public class Booking implements BookingInterface {
    private static String ROOM_FILE = Main.HOTEL_PATH+ "/" + "Room.txt";
    private static String RESERVATION_FILE = Main.HOTEL_PATH+ "/" + "Reservation.txt";
    private static String GUEST_FILE = Main.HOTEL_PATH+ "/" +"Guest.txt";

    // ===================== MAIN MENU =====================
    public static void handleBooking() {
        Scanner sc = new Scanner(System.in);

        // STEP 1: Hardcoded list of supported hotel cities
        List<String> hotelCities = new ArrayList<>();
        hotelCities.add("Chicago");
        hotelCities.add("DesMoines");

        System.out.println("\n===== SELECT HOTEL LOCATION =====");
        for (int i = 0; i < hotelCities.size(); i++) {
            System.out.println((i + 1) + ". " + hotelCities.get(i));
        }

        System.out.print("Select your city: ");
        int cityChoice;
        try {
            cityChoice = Integer.parseInt(sc.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Returning to menu.");
            return;
        }

        if (cityChoice < 1 || cityChoice > hotelCities.size()) {
            System.out.println("Invalid city selection. Returning to menu.");
            return;
        }

        String selectedCity = hotelCities.get(cityChoice - 1);
        Main.HOTEL_PATH = selectedCity;
        ROOM_FILE = Main.HOTEL_PATH+ "/" + "Room.txt";
        RESERVATION_FILE = Main.HOTEL_PATH+ "/" + "Reservation.txt";
        GUEST_FILE = Main.HOTEL_PATH+ "/" +"Guest.txt";

        System.out.println("📍 Selected hotel: " + selectedCity);

        // STEP 3: Proceed with booking flow for that location
        Booking bookingSystem = new Booking();
        boolean running = true;

        while (running) {
            System.out.println("\n===== HOTEL BOOKING SYSTEM (" + selectedCity + ") =====");
            System.out.println("1. Book a Room");
            System.out.println("2. Modify a Reservation");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");

            String choice = sc.nextLine();

            if (choice.equals("1")) {
                bookingSystem.bookRoomFlow();
            } else if (choice.equals("2")) {
                bookingSystem.modifyReservationFlow();
            } else if (choice.equals("3")) {
                bookingSystem.cancelReservationFlow();
            } else if (choice.equals("4")) {
                System.out.println("Returning to main menu...");
                running = false;
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ===================== BOOK FLOW =====================
    private void bookRoomFlow() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Guest Name: ");
        String guestName = sc.nextLine();

        System.out.print("Enter Guest ID: ");
        int guestId = Integer.parseInt(sc.nextLine());

        System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(sc.nextLine());

        System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(sc.nextLine());

        List<room> availableRooms = getAvailableRooms(checkIn, checkOut);

        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected dates.");
            return;
        }

        // Map room types to available rooms
        Map<String, List<room>> typeToRooms = new LinkedHashMap<>();
        for (room r : availableRooms) {
            typeToRooms.computeIfAbsent(r.getType(), k -> new ArrayList<>()).add(r);
        }

        // Display room types
        System.out.println("\nAvailable Room Types:");
        int index = 1;
        List<String> roomTypeMenu = new ArrayList<>();
        for (Map.Entry<String, List<room>> entry : typeToRooms.entrySet()) {
            System.out.println(index + ". " + entry.getKey() + " (" + entry.getValue().size() + " available)");
            roomTypeMenu.add(entry.getKey());
            index++;
        }

        // FIXED INPUT SECTION: accept number OR text
        System.out.print("\nSelect a room type to book (1-" + roomTypeMenu.size() + " or type name): ");
        String choice = sc.nextLine().trim().toLowerCase();

        String selectedType = null;

        // Case 1: user typed a number
        if (choice.matches("\\d+")) {
            int selectedIndex = Integer.parseInt(choice);
            if (selectedIndex >= 1 && selectedIndex <= roomTypeMenu.size()) {
                selectedType = roomTypeMenu.get(selectedIndex - 1);
            }
        } else {
            // Case 2: user typed the room type name
            for (String type : roomTypeMenu) {
                if (type.toLowerCase().equals(choice)) {
                    selectedType = type;
                    break;
                }
            }
        }

        if (selectedType == null) {
            System.out.println("Invalid input.");
            return;
        }

        // Pick the first available room of that type
        room selectedRoom = typeToRooms.get(selectedType).get(0);
        selectedRoom.setStartDate(checkIn);
        selectedRoom.setEndDate(checkOut);

        if (createReservation(guestName, guestId, selectedRoom)) {
            System.out.println("\nReservation created successfully for a " + selectedType + "!");
        } else {
            System.out.println("Could not create reservation.");
        }
    }

    // ===================== MODIFY FLOW =====================
    private void modifyReservationFlow() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Reservation ID to modify: ");
        String reservationId = sc.nextLine();

        if (!modifyReservation(reservationId)) {
            System.out.println("Reservation not found or could not be modified.");
        } else {
            System.out.println("Reservation updated successfully!");
        }
    }

    // ===================== CANCEL FLOW =====================
    private void cancelReservationFlow() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Reservation ID to cancel: ");
        String reservationId = sc.nextLine();

        if (!cancelReservation(reservationId)) {
            System.out.println("Reservation not found or could not be cancelled.");
        } else {
            System.out.println("Reservation cancelled successfully!");
        }
    }

    // ===================== INTERFACE IMPLEMENTATION =====================
    @Override
    public boolean createReservation(String guestName, int guestId, room room) {
        try {
            // Generate a unique reservation ID
            String reservationId = UUID.randomUUID().toString();

            // Append reservation to Reservation.txt (without guestId)
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(RESERVATION_FILE, true))) {
                bw.write(reservationId + "," + guestName + "," + room.getRoomNumber() + "," + room.getStartDate() + "," + room.getEndDate());
                bw.newLine();
            }

            // Add guest to Guest.txt if they are new
            addGuestIfNew(guestName, guestId);

            return true;
        } catch (IOException e) {
            System.out.println("Error creating reservation: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean modifyReservation(String reservationId) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter new Check-in Date (YYYY-MM-DD): ");
        LocalDate newCheckIn = LocalDate.parse(sc.nextLine());

        System.out.print("Enter new Check-out Date (YYYY-MM-DD): ");
        LocalDate newCheckOut = LocalDate.parse(sc.nextLine());

        List<String> updatedReservations = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(RESERVATION_FILE))) {
            String header = br.readLine();
            updatedReservations.add(header);

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(reservationId)) {
                    line = parts[0] + "," + parts[1] + "," + parts[2] + "," + newCheckIn + "," + newCheckOut;
                    found = true;
                }
                updatedReservations.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error modifying reservation: " + e.getMessage());
            return false;
        }

        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(RESERVATION_FILE))) {
                for (String r : updatedReservations) {
                    bw.write(r);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error saving modified reservation: " + e.getMessage());
                return false;
            }
        }

        return found;
    }

    @Override
    public boolean cancelReservation(String reservationId) {
        List<String> updatedReservations = new ArrayList<>();
        boolean found = false;

        try (BufferedReader br = new BufferedReader(new FileReader(RESERVATION_FILE))) {
            String header = br.readLine();
            updatedReservations.add(header);

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(reservationId)) {
                    found = true;
                    continue; // skip
                }
                updatedReservations.add(line);
            }
        } catch (IOException e) {
            System.out.println("Error cancelling reservation: " + e.getMessage());
            return false;
        }

        if (found) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(RESERVATION_FILE))) {
                for (String r : updatedReservations) {
                    bw.write(r);
                    bw.newLine();
                }
            } catch (IOException e) {
                System.out.println("Error updating reservations: " + e.getMessage());
                return false;
            }
        }

        return found;
    }

    @Override
    public boolean checkAvailability(room room) {
        LocalDate start = room.getStartDate();
        LocalDate end = room.getEndDate();
        return isRoomAvailable(room.getRoomNumber(), start, end);
    }

    // ===================== HELPER METHODS =====================
    private List<room> getAvailableRooms(LocalDate requestedStart, LocalDate requestedEnd) {
        List<room> availableRooms = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(ROOM_FILE))) {
            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                int roomNumber = Integer.parseInt(parts[0]);
                String type = parts[1];

                if (isRoomAvailable(roomNumber, requestedStart, requestedEnd)) {
                    availableRooms.add(new room(null, roomNumber, type, "Available", null, null));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading rooms: " + e.getMessage());
        }

        return availableRooms;
    }

    private boolean isRoomAvailable(int roomNumber, LocalDate requestedStart, LocalDate requestedEnd) {
        try (BufferedReader br = new BufferedReader(new FileReader(RESERVATION_FILE))) {
            br.readLine(); // skip header

            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (Integer.parseInt(parts[2]) == roomNumber) {
                    LocalDate existingStart = LocalDate.parse(parts[3]);
                    LocalDate existingEnd = LocalDate.parse(parts[4]);

                    if (!requestedEnd.isBefore(existingStart) && !requestedStart.isAfter(existingEnd)) {
                        return false;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations: " + e.getMessage());
        }

        return true;
    }

    private void addGuestIfNew(String guestName, int guestId) {
        boolean exists = false;

        try (BufferedReader br = new BufferedReader(new FileReader(GUEST_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2 && parts[0].trim().equalsIgnoreCase(guestName) && Integer.parseInt(parts[1].trim()) == guestId) {
                    exists = true;
                    break;
                }
            }
        } catch (IOException ignored) {
        }

        if (!exists) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(GUEST_FILE, true))) {
                bw.write(guestName + "," + guestId);
                bw.newLine();
            } catch (IOException e) {
                System.out.println("Error writing guest: " + e.getMessage());
            }
        }
    }
}