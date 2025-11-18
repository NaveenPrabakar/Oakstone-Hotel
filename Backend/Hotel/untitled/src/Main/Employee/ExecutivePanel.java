package Main.Employee;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import Main.*;

import Main.Room.*;

public class ExecutivePanel extends Employee implements ExecutiveTeam {

    public static final List<String> notifications = new ArrayList<>();

    public ExecutivePanel(int StaffID, String name, String role) {
        super(StaffID, name, role);
    }

    @Override
    public void viewReports() {
        System.out.println("\n===== EXECUTIVE REPORT VIEWER =====");

        File folder = new File(Main.HOTEL_PATH + "/Reports/");
        if (!folder.exists() || !folder.isDirectory()) {
            System.out.println("No reports folder found.");
            return;
        }

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            System.out.println("No reports available.");
            return;
        }

        System.out.println("\nAvailable Reports:");
        for (int i = 0; i < files.length; i++) {
            System.out.println((i + 1) + ". " + files[i].getName());
        }

        System.out.print("\nEnter report number to view: ");
        Scanner sc = new Scanner(System.in);
        int choice = sc.nextInt();
        sc.nextLine();

        if (choice < 1 || choice > files.length) {
            System.out.println("Invalid choice.");
            return;
        }

        File selected = files[choice - 1];
        System.out.println("\n--- Viewing: " + selected.getName() + " ---");
        System.out.println("--------------------------------------------");

        try (Scanner reader = new Scanner(selected)) {
            while (reader.hasNextLine()) {
                System.out.println(reader.nextLine());
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        System.out.println("--------------------------------------------");
        System.out.println("End of Report.");
    }


    public void askDataTeam() {
        Scanner sc = new Scanner(System.in);

        System.out.println("\n===== REQUEST DATA TEAM REPORT =====");
        System.out.print("Enter what you need a report on: ");
        String request = sc.nextLine().trim();

        if (request.isEmpty()) {
            System.out.println("Invalid request. Try again.");
            return;
        }
        DataPanel.addRequest("Executive " + getName() + " requested: " + request);
    }

    @Override
    public void viewPrices() {
        Hotel hotel = new Hotel(Main.HOTEL_PATH);
        hotel.loadPricesFromFile();
        hotel.viewPrices();
    }

    @Override
    public void setPrices() {
        Hotel hotel = new Hotel(Main.HOTEL_PATH);
        hotel.loadPricesFromFile();
        hotel.setPrices();
        hotel.savePricesToFile();
    }

    public void viewNotifications() {
        Scanner sc = new Scanner(System.in);

        while (true) {
            System.out.println("\n===== EXECUTIVE NOTIFICATIONS =====");

            if (notifications.isEmpty()) {
                System.out.println("No new notifications.");
            } else {
                for (int i = 0; i < notifications.size(); i++) {
                    System.out.println((i + 1) + ". " + notifications.get(i));
                }
            }

            System.out.println("===================================");
            System.out.println("Options:");
            System.out.println("1. Clear ALL notifications");
            System.out.println("2. Clear a specific notification");
            System.out.println("3. Exit");
            System.out.print("Choose: ");

            int choice = sc.nextInt();
            sc.nextLine();

            if (choice == 1) {
                notifications.clear();
                System.out.println("All notifications cleared.");

            } else if (choice == 2) {

                if (notifications.isEmpty()) {
                    System.out.println("There are no notifications to clear.");
                    continue;
                }

                System.out.print("Enter notification number to clear: ");
                int index = sc.nextInt();
                sc.nextLine();

                if (index < 1 || index > notifications.size()) {
                    System.out.println("Invalid number.");
                    continue;
                }

                notifications.remove(index - 1);
                System.out.println("Notification removed.");

            } else if (choice == 3) {
                System.out.println("Exiting Notification Panel.");
                break;

            } else {
                System.out.println("Invalid option.");
            }
        }
    }
}