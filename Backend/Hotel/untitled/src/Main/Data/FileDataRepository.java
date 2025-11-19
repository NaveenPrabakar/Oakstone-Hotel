package Main.Data;

import Main.Booking.Reservation;
import Main.Employee.Employee;
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

    @Override
    public List<Employee> loadEmployees() {
        List<Employee> employees = new ArrayList<>();

        Path employeeFile = basePath.resolve("Employee.txt");

        try {
            List<String> lines = Files.readAllLines(employeeFile);

            for (String line : lines) {
                if (line.trim().isEmpty()) continue;

                employees.add(parseEmployee(line));
            }

        } catch (IOException e) {
            System.out.println("Error loading Employees: " + e.getMessage());
        }

        return employees;
    }

    private Employee parseEmployee(String line) {
        String[] parts = line.trim().split(" ");

        int id = Integer.parseInt(parts[0]);

        // role, username, password are always last 3 tokens
        String role = parts[parts.length - 3];
        String username = parts[parts.length - 2];
        String password = parts[parts.length - 1];

        // name is everything between id and role
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 1; i < parts.length - 3; i++) {
            nameBuilder.append(parts[i]);
            if (i < parts.length - 4) nameBuilder.append(" ");
        }

        String name = nameBuilder.toString();

        return new Employee(id, name, role, username, password);
    }

    @Override
    public boolean addEmployee(Employee emp) {
        Path employeeFile = basePath.resolve("Employee.txt");

        try {
            String line = "\n" + emp.getId() + " "
                    + emp.getName() + " "
                    + emp.role() + " "
                    + emp.getUsername() + " "
                    + emp.getPassword();

            Files.write(employeeFile, line.getBytes(), java.nio.file.StandardOpenOption.APPEND);

            return true;

        } catch (IOException e) {
            System.out.println("Error writing employee: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int getNextEmployeeId() {
        List<Employee> list = loadEmployees();
        if (list.isEmpty()) return 1;
        return list.get(list.size() - 1).getId() + 1;
    }





}
