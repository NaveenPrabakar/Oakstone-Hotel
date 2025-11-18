package Main.Controller;

import Main.Employee.DataPanel;

import java.util.Scanner;

public class DataTeamController {
    public static void runDataAnalysis(String analystName) {
        Scanner sc = new Scanner(System.in);
        DataPanel analyst = new DataPanel(2001, analystName, "Data Team");

        System.out.println("\n===== DATA TEAM PANEL =====");
        System.out.println("Welcome, " + analystName + "!");
        System.out.println("----------------------------");

        boolean running = true;

        while (running) {
            System.out.println("\n--- Data Team Options ---");
            System.out.println("1. Pull Data");
            System.out.println("2. Analyze Data");
            System.out.println("3. Write Report");
            System.out.println("4. View Tickets");
            System.out.println("5. Mark Ticket as Completed");
            System.out.println("6. Delete Reports");
            System.out.println("7. Exit Data Panel");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                analyst.pullData();
            } else if (choice.equals("2")) {
                analyst.analyzeData();
            } else if (choice.equals("3")) {
                analyst.writeReport();
            } else if (choice.equals("4")) {
                DataPanel.viewRequests();
            } else if (choice.equals("5")) {
                DataPanel.viewRequests();
                System.out.print("Enter ticket number to mark complete: ");
                int index = Integer.parseInt(sc.nextLine());
                DataPanel.completeRequest(index);
            } else if (choice.equals("7")) {
                running = false;
            } else if (choice.equals("6")){
                analyst.deleteReport();
            }
            else {
                System.out.println("Invalid choice. Please enter 1–6.");
            }
        }
    }
}