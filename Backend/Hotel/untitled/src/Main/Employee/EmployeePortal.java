package Main.Employee;

import java.util.Scanner;

public class EmployeePortal {
    private ClockSystem clockSystem;

    public EmployeePortal(String dataFilePath) {
        clockSystem = new ClockSystem(dataFilePath);
    }

    public void runPortal(Scanner sc, Employee employee) {
        boolean running = true;
        int empId = employee.getId();

        while (running) {
            System.out.println("\n╔════════════════════════╗");
            System.out.println("║ EMPLOYEE PORTAL        ║");
            System.out.println("╠════════════════════════╣");
            System.out.println("║ 1. Clock In            ║");
            System.out.println("║ 2. Clock Out           ║");
            System.out.println("║ 3. Show Hours Worked   ║");
            System.out.println("║ 0. Back to Worker Portal║");
            System.out.println("╚════════════════════════╝");
            System.out.print("Select an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    clockSystem.clockIn(empId);
                    break;
                case "2":
                    clockSystem.clockOut(empId);
                    break;
                case "3":
                    clockSystem.showHoursWorked(empId);
                    break;
                case "0":
                    running = false; // go back to Worker Portal
                    break;
                default:
                    System.out.println("Invalid option. Choose 0-3.");
            }
        }
    }
}