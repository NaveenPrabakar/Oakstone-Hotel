package Main.Employee;

import Main.Data.DataRepository;
import Main.Main;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import Main.Data.FileDataRepository;
import Main.Data.ReportWriter;


public class DataPanel extends Employee implements DataTeam {
    private static final String REPORT_FOLDER = Main.HOTEL_PATH + "/Reports/";
    private final DataRepository repo;
    private final ReportWriter reportWriter = new ReportWriter();


    private List<String[]> guests = new ArrayList<>();
    private List<String[]> reservations = new ArrayList<>();
    private List<String[]> pastReservations = new ArrayList<>();
    private List<String[]> rooms = new ArrayList<>();
    private Map<String, Integer> guestVisitCount = new HashMap<>();

    private static final LinkedBlockingQueue<String> requestQueue = new LinkedBlockingQueue<>();

    public DataPanel(int staffID, String name, String role) {

        super(staffID, name, role);
        this.repo = new FileDataRepository(Paths.get(Main.HOTEL_PATH));
    }

    public static void addRequest(String request) {
        try {
            requestQueue.put(request);
            System.out.println("New report request added: " + request);
        } catch (InterruptedException e) {
            System.out.println(" Failed to add request: " + e.getMessage());
        }
    }

    public static void viewRequests() {
        if (requestQueue.isEmpty()) {
            System.out.println("No pending report requests.");
            return;
        }

        System.out.println("\n===== PENDING REPORT REQUESTS =====");
        int i = 1;
        for (String req : requestQueue) {
            System.out.println(i++ + ". " + req);
        }
    }

    public static void completeRequest(int index) {
        Scanner sc = new Scanner(System.in);

        if (index <= 0 || index > requestQueue.size()) {
            System.out.println("Invalid request number.");
            return;
        }

        List<String> list = new ArrayList<>(requestQueue);
        String done = list.get(index - 1);

        requestQueue.remove(done);
        System.out.println("Request completed: " + done);

        System.out.print("Enter a closing note to send to executives: ");
        String note = sc.nextLine().trim();

        if (note.isEmpty()) {
            note = "(No additional notes provided.)";
        }

        ExecutivePanel.notifications.add("Request Completed: " + done + " | Note: " + note);

        System.out.println("Closing note sent to executives.");
    }


    @Override
    public void pullData() {
        guests = repo.loadGuests();
        reservations = repo.loadReservations();
        pastReservations = repo.loadPastReservations();
        rooms = repo.loadRooms();

        System.out.println("Data pulled successfully from all sources.");
    }

    @Override
    public void analyzeData() {
        if (guests.isEmpty() || (reservations.isEmpty() && pastReservations.isEmpty())) {
            System.out.println("Please pull data first before analysis.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("\n--- Choose Data Analysis Type ---");
        System.out.println("1. Guest Data Analysis");
        System.out.println("2. Reservation Data Analysis");
        System.out.println("3. Past Reservation Analysis");
        System.out.print("Enter choice (1–3): ");

        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 1) {
            analyzeGuestData();
        } else if (choice == 2) {
            analyzeReservationData();
        } else if (choice == 3) {
            analyzePastReservationData();
        } else {
            System.out.println("Invalid choice. Please enter 1–3.");
        }
    }

    @Override
    public void writeReport() {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n--- Report Options ---");
        System.out.println("1. Create New Report");
        System.out.println("2. Edit Existing Report");
        System.out.print("Choose (1–2): ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice == 2) {
            editExistingReport();
            return;
        }

        System.out.print("Enter report file name (without extension): ");
        String filename = sc.nextLine().trim();
        if (filename.isEmpty()) filename = "DataReport";

        System.out.println("\n--- Write Your Report Below ---");
        System.out.println("(Blank line finishes writing)");
        System.out.println("--------------------------------");

        List<String> content = new ArrayList<>();

        while (true) {
            String line = sc.nextLine();
            if (line.trim().isEmpty()) break;
            content.add(line);
        }

        reportWriter.createReport(REPORT_FOLDER + filename, content);
    }


    private void editExistingReport() {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the report filename (including .txt): ");
        String filename = sc.nextLine().trim();

        reportWriter.editReport(REPORT_FOLDER + filename);
    }

    private void analyzeGuestData() {
        System.out.println("\n--- Guest Data Analysis ---");
        System.out.println("Total Guests: " + guests.size());

        Set<String> uniqueGuests = new HashSet<>();
        int duplicates = 0;

        for (String[] g : guests) {
            String guestName = g[0].trim();
            if (!uniqueGuests.add(guestName)) {
                duplicates++;
            }
        }

        System.out.println("Unique Guests: " + uniqueGuests.size());
        System.out.println("Duplicate Entries Found: " + duplicates);
    }

    private void analyzeReservationData() {
        System.out.println("\n--- Reservation Data Analysis ---");
        System.out.println("Active Reservations: " + reservations.size());

        Map<String, Integer> roomBookings = new HashMap<>();
        for (String[] record : reservations) {
            if (record.length >= 3) {
                String roomNumber = record[2].trim();
                roomBookings.put(roomNumber, roomBookings.getOrDefault(roomNumber, 0) + 1);
            }
        }

        System.out.println("Bookings per Room:");
        for (Map.Entry<String, Integer> entry : roomBookings.entrySet()) {
            System.out.println("Room " + entry.getKey() + ": " + entry.getValue() + " active reservations");
        }

        if (!rooms.isEmpty()) {
            Map<String, Integer> typeCount = new HashMap<>();
            for (String[] r : rooms) {
                if (r.length >= 2) {
                    String type = r[1].trim();
                    typeCount.put(type, typeCount.getOrDefault(type, 0) + 1);
                }
            }

            System.out.println("\nRoom Types Available:");
            for (Map.Entry<String, Integer> entry : typeCount.entrySet()) {
                System.out.println(entry.getKey() + ": " + entry.getValue() + " rooms");
            }
        }
    }

    private void analyzePastReservationData() {
        System.out.println("\n--- Past Reservation Data Analysis ---");
        System.out.println("Total Past Reservations: " + pastReservations.size());

        guestVisitCount.clear();
        for (String[] record : pastReservations) {
            if (record.length >= 2) {
                String guestName = record[1].trim();
                guestVisitCount.put(guestName, guestVisitCount.getOrDefault(guestName, 0) + 1);
            }
        }

        System.out.println("Guest Visit Counts:");
        for (Map.Entry<String, Integer> entry : guestVisitCount.entrySet()) {
            System.out.println(entry.getKey() + " - " + entry.getValue() + " visits");
        }
    }

    public void deleteReport() {
        System.out.println("\n===== DELETE REPORT =====");

        File folder = new File(REPORT_FOLDER);
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("No reports folder found.");
            return;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No reports available to delete.");
            return;
        }

        System.out.println("\nAvailable Reports:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        Scanner sc = new Scanner(System.in);
        System.out.print("\nEnter the number of the report to DELETE: ");
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice < 1 || choice > files.length) {
            System.out.println("Invalid choice.");
            return;
        }

        String filename = files[choice - 1].getName();
        reportWriter.deleteReport(REPORT_FOLDER + filename);
    }

}