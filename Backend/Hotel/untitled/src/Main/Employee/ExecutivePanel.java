package Main.Employee;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import Main.Room.*;

public class ExecutivePanel extends Employee implements ExecutiveTeam {

    public ExecutivePanel(int StaffID, String name, String role) {
        super(StaffID, name, role);
    }

    @Override
    public void viewReports() {
        System.out.println("\n===== EXECUTIVE REPORT VIEWER =====");

        File reportFile = new File("DataReport.txt");

        if (!reportFile.exists()) {
            System.out.println("No report found. The Data Team must generate one first.");
            return;
        }

        System.out.println("Displaying contents of: " + reportFile.getName());
        System.out.println("--------------------------------------------------");

        try {
            Scanner fileReader = new Scanner(reportFile);
            while (fileReader.hasNextLine()) {
                String line = fileReader.nextLine();
                System.out.println(line);
            }
            fileReader.close();

            System.out.println("--------------------------------------------------");
            System.out.println("End of Report.");

        } catch (FileNotFoundException e) {
            System.out.println("Error reading the report: " + e.getMessage());
        }
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
        Hotel hotel = new Hotel("123 Main St, Iowa");
        hotel.loadPricesFromFile();
        hotel.viewPrices();
    }

    @Override
    public void setPrices() {
        Hotel hotel = new Hotel("123 Main St, Iowa");
        hotel.loadPricesFromFile();
        hotel.setPrices();
        hotel.savePricesToFile();
    }
}