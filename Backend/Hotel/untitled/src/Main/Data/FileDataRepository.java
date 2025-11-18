package Main.Data;

import Main.Booking.Reservation;
import Main.Guest.Guest;
import Main.Room.room;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileDataRepository implements DataRepository {

    private final Path basePath;

    public FileDataRepository(Path hotelPath) {
        this.basePath = hotelPath;
    }

    @Override
    public List<String[]> loadGuests() {
        try {
            return loadFile(basePath.resolve("Guest.txt").toString());
        } catch (IOException e) {
            System.out.println("Error loading Guests: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String[]> loadReservations() {
        try {
            return loadFile(basePath.resolve("Reservation.txt").toString());
        } catch (IOException e) {
            System.out.println("Error loading Reservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String[]> loadPastReservations() {
        try {
            return loadFile(basePath.resolve("Past_Reservation.txt").toString());
        } catch (IOException e) {
            System.out.println("Error loading Past Reservations: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<String[]> loadRooms() {
        try {
            return loadFile(basePath.resolve("Room.txt").toString());
        } catch (IOException e) {
            System.out.println("Error loading Rooms: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    private List<String[]> loadFile(String filePath) throws IOException {
        List<String[]> data = new ArrayList<>();
        List<String> lines = Files.readAllLines(Paths.get(filePath));

        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                data.add(line.split(","));
            }
        }
        return data;
    }
}
