package Main.Employee;

import java.util.Scanner;

public class ExecutiveController {
    public static void runExecutivePanel(String execName) {
        Scanner sc = new Scanner(System.in);
        ExecutivePanel executive = new ExecutivePanel(3001, execName, "Executive");

        System.out.println("\n===== EXECUTIVE PANEL =====");
        System.out.println("Welcome, " + execName + "!");
        System.out.println("---------------------------");

        boolean running = true;

        while (running) {
            System.out.println("\n--- Executive Options ---");
            System.out.println("1. View Reports");
            System.out.println("2. Ask Data Team for New Report");
            System.out.println("3. View Current Room Prices");
            System.out.println("4. Set / Update Room Prices");
            System.out.println("5. Exit Executive Panel");
            System.out.println("6. Notifications");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                executive.viewReports();
            } else if (choice.equals("2")) {
                executive.askDataTeam();
            } else if (choice.equals("3")) {
                executive.viewPrices();
            } else if (choice.equals("4")) {
                executive.setPrices();
            } else if (choice.equals("5")) {
                System.out.println("Logging out of Executive Panel...");
                running = false;
            }else if(choice.equals("6")){
                executive.viewNotifications();
            } else {
                System.out.println("Invalid choice. Please enter 1–5.");
            }
        }
    }
}