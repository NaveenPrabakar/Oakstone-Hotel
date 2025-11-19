package Main.Controller;

import java.io.FileNotFoundException;
import java.util.Scanner;
import Main.Employee.Employee;

public class WorkerLoginController {

    public static Employee loginWorker(Scanner sc) throws FileNotFoundException {

        System.out.println("\n===== WORKER LOGIN =====");

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        Employee emp = EmployeeLoginService.login(username, password);

        if (emp == null) {
            System.out.println("Invalid username or password.");
            System.out.println("===== WORKER LOGIN UNSUCCESSFUL =====");
            return null;
        }

        System.out.println("✔ Login successful. Welcome, " + emp.getName() +
                " (" + emp.role() + ")");
        System.out.println("===== WORKER LOGIN SUCCESSFUL =====");
        return emp;
    }

    // ============================================
    // NEW: REGISTER NEW WORKER
    // ============================================
    public static void registerNewWorker(Scanner sc) {
        System.out.println("\n===== CREATE NEW WORKER =====");

        System.out.print("Full Name: ");
        String name = sc.nextLine().trim();

        System.out.print("Role (FrontDesk / Cleaner / Kitchen / Executive / DataTeam / HR): ");
        String role = sc.nextLine().trim();

        System.out.print("Username: ");
        String username = sc.nextLine().trim();

        String password = "";

        while (true) {
            System.out.print("Password: ");
            password = sc.nextLine();

            String strength = PasswordUtils.strength(password);
            System.out.println("Password Strength: " + strength);

            if (!PasswordUtils.isValid(password)) {
                System.out.println("!! Password must be at least 8 characters and contain:");
                System.out.println("   - Uppercase letter");
                System.out.println("   - Lowercase letter");
                System.out.println("   - Digit");
                System.out.println("   - Special character (!@#$%^&* etc.)");
                continue;
            }

            System.out.println("✔ Password accepted.");
            break;
        }

        Employee emp = EmployeeLoginService.createEmployee(
                name, role, username, password
        );

        if (emp != null) {
            System.out.println("✔ Worker created successfully: ID " + emp.getId());
        } else {
            System.out.println("❌ Failed to save worker.");
        }
        System.out.println("\n===== NEW WORKER CREATED =====");
    }
}