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
            System.out.println("===== WORKER UNSUCCESSFUL =====");
            return null;
        }

        System.out.println("✔ Login successful. Welcome, " + emp.getName() +
                " (" + emp.role() + ")");
        System.out.println("===== WORKER SUCCESSFUL =====");
        return emp;
    }

    // ============================================
    // NEW: REGISTER NEW WORKER
    // ============================================
    public static void registerNewWorker(Scanner sc) {

        System.out.println("\n===== REGISTER NEW WORKER =====");

        // Full name (can have spaces)
        System.out.print("Enter full name: ");
        String name = sc.nextLine().trim();

        // Role must match your existing role names
        System.out.print("Enter role (FrontDesk / Cleaner / Kitchen / Executive / DataTeam / HR): ");
        String role = sc.nextLine().trim();

        // Username
        System.out.print("Enter username: ");
        String username = sc.nextLine().trim();

        // Password
        System.out.print("Enter password: ");
        String password = sc.nextLine().trim();

        // Create employee via service
        Employee newEmp = EmployeeLoginService.createEmployee(name, role, username, password);

        if (newEmp == null) {
            System.out.println("❌ Failed to register new worker. Please try again.");
            return;
        }

        System.out.println("\n✔ Worker successfully registered!");
        System.out.println("Assigned Employee ID: " + newEmp.getId());
        System.out.println("Name: " + name);
        System.out.println("Role: " + role);
        System.out.println("Username: " + username);
    }
}