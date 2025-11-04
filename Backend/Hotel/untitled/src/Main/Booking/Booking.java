package Main.Booking;

import Main.Room.room;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class Booking implements BookingInterface {

    private static final String ROOM_FILE = "Backend/Hotel/untitled/src/Main/Room.txt";
    private static final String RESERVATION_FILE = "Backend/Hotel/untitled/src/Main/Reservation.txt";
    private static final String GUEST_FILE = "Backend/Hotel/untitled/src/Main/Guest.txt";

    // ===================== MAIN MENU =====================
    public static void handleBooking() {
        Booking bookingSystem = new Booking();
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== HOTEL BOOKING SYSTEM =====");
            System.out.println("1. Book a Room");
            System.out.println("2. Modify a Reservation");
            System.out.println("3. Cancel a Reservation");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");

            String choice = sc.nextLine();
            switch (choice) {
                case "1" -> bookingSystem.bookRoomFlow();
                case "2" -> bookingSystem.modifyReservationFlow();
                case "3" -> bookingSystem.cancelReservationFlow();
                case "4" -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    // ===================== BOOK FLOW =====================
    private void bookRoomFlow() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter Guest Name: ");
        String guestName = sc.nextLine();

        System.out.print("Enter Check-in Date (YYYY-MM-DD): ");
        LocalDate checkIn = LocalDate.parse(sc.nextLine());

        System.out.print("Enter Check-out Date (YYYY-MM-DD): ");
        LocalDate checkOut = LocalDate.parse(sc.nextLine());

        List<room> availableRooms = getAvailableRooms(checkIn, checkOut);
        if (availableRooms.isEmpty()) {
            System.out.println("No rooms available for the selected dates.");
            return;
        }

        System.out.println("\nAvailable Rooms:");
        for (room r : availableRooms) {
            System.out.println(r.getRoomNumber() + " (" + r.getType() + ")");
        }

        System.out.print("\nSelect Room Number to book: ");
        String roomNumber = sc.nextLine();

        room selectedRoom = availableRooms.stream()
                .filter(r -> r.getRoomNumber() == Integer.parseInt(roomNumber))
                .findFirst().orElse(null);

        if (selectedRoom == null) {
            System.out.println("Invalid room selection.");
            return;
        }

        selectedRoom.setStartDate(checkIn);
        selectedRoom.setEndDate(checkOut);


        if (createReservation(guestName, selectedRoom)) {
            System.out.println("\nReservation created successfully!");
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
    public boolean createReservation(String guestName, room room) {
        try {
            // Append reservation to file
            String reservationId = UUID.randomUUID().toString();
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(RESERVATION_FILE, true))) {
                bw.write(reservationId + "," + guestName + "," + room.getRoomNumber() + "," +
                        room.getStartDate() + "," + room.getEndDate());
                bw.newLine();
            }

            addGuestIfNew(guestName);
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
                    line = parts[0] + "," + parts[1] + "," + parts[2] + "," +
                            newCheckIn + "," + newCheckOut;
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
                    availableRooms.add(new room(roomNumber, type, "Available", null, null));
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

    private void addGuestIfNew(String guestName) {
        boolean exists = false;
        try (BufferedReader br = new BufferedReader(new FileReader(GUEST_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().equalsIgnoreCase(guestName)) {
                    exists = true;
                    break;
                }
            }
        } catch (IOException ignored) {}

        if (!exists) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(GUEST_FILE, true))) {
                bw.write(guestName);
                bw.newLine();
            } catch (IOException e) {
                System.out.println("Error writing guest: " + e.getMessage());
            }
        }
    }
}
