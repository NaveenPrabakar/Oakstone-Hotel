package Main.Controller;

import Main.Employee.ExecutivePanel;
import Main.Employee.HRPanel;

import java.util.Scanner;

public class HRController {
    public static void runHRPanel(String hrName) {
        Scanner sc = new Scanner(System.in);
        HRPanel hrPanel = new HRPanel(3001, hrName, "HR");

        System.out.println("\n===== HR PANEL =====");
        System.out.println("Welcome, " + hrName + "!");
        System.out.println("---------------------------");

        boolean running = true;

        while (running) {
            System.out.println("\n--- HR Options ---");
            System.out.println("1. View Reviews");
            System.out.println("2. Hire Workers");
            System.out.println("3. Fire Workers");
            System.out.println("4. Massive Layoffs");
            System.out.println("5. Exit HR Panel");
            System.out.println("6. Notifications");
            System.out.print("Enter your choice: ");

            String choice = sc.nextLine().trim();

            if (choice.equals("1")) {
                hrPanel.viewReviews();
            } else if (choice.equals("2")) {
                hrPanel.hireWorkers(null);;
            } else if (choice.equals("3")) {
                hrPanel.fireWorkers(null);
            } else if (choice.equals("4")) {
                hrPanel.massiveLayoffs();
            } else if (choice.equals("5")) {
                System.out.println("Logging out of Executive Panel...");
                running = false;
            }else if(choice.equals("6")){
                hrPanel.viewNotifications();
            } else {
                System.out.println("Invalid choice. Please enter 1–5.");
            }
        }
    }

}