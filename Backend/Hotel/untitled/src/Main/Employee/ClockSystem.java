package Main.Employee;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.Duration;
import java.util.*;
import java.io.*;

public class ClockSystem {
    private HashMap<Integer, LocalDateTime> clockInTimes = new HashMap<>();
    private HashMap<Integer, Long> totalWorkedMinutes = new HashMap<>();
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
    private File file;

    public ClockSystem(String filePath) {
        file = new File(filePath);
        loadFromFile();
    }

    private void loadFromFile() {
        if (!file.exists()) return;

        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNextLine()) {
                String[] parts = sc.nextLine().split(",");
                if (parts.length != 3) continue;

                int empId = Integer.parseInt(parts[0]);
                String clockInStr = parts[1];
                long minutes = Long.parseLong(parts[2]);

                totalWorkedMinutes.put(empId, minutes);

                if (!clockInStr.equals("null")) {
                    clockInTimes.put(empId, LocalDateTime.parse(clockInStr, formatter));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading clock data: " + e.getMessage());
        }
    }

    private void saveToFile() {
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int empId : totalWorkedMinutes.keySet()) {
                LocalDateTime inTime = clockInTimes.getOrDefault(empId, null);
                String clockInStr = inTime == null ? "null" : inTime.format(formatter);
                pw.println(empId + "," + clockInStr + "," + totalWorkedMinutes.get(empId));
            }
        } catch (Exception e) {
            System.out.println("Error saving clock data: " + e.getMessage());
        }
    }

    public void clockIn(int employeeId) {
        if (clockInTimes.containsKey(employeeId)) {
            System.out.println("Already clocked in at: " + clockInTimes.get(employeeId).format(formatter));
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        clockInTimes.put(employeeId, now);
        saveToFile();
        System.out.println("Clocked in at: " + now.format(formatter));
    }

    public void clockOut(int employeeId) {
        if (!clockInTimes.containsKey(employeeId)) {
            System.out.println("Employee hasn't clocked in yet.");
            return;
        }

        LocalDateTime inTime = clockInTimes.get(employeeId);
        LocalDateTime now = LocalDateTime.now();
        long minutesWorked = Duration.between(inTime, now).toMinutes();

        totalWorkedMinutes.put(employeeId, totalWorkedMinutes.getOrDefault(employeeId, 0L) + minutesWorked);
        clockInTimes.remove(employeeId);
        saveToFile();

        System.out.println("Clocked out at: " + now.format(formatter) + " | Session minutes: " + minutesWorked);
    }

    public void showHoursWorked(int employeeId) {
        long totalMinutes = totalWorkedMinutes.getOrDefault(employeeId, 0L);
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        System.out.println("⏱ Total hours worked: " + hours + "h " + minutes + "m");
    }
}