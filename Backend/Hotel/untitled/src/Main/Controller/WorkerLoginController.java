package Main.Controller;

import java.io.FileNotFoundException;
import java.util.Random;
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
        String username = generateUsername(name);
        System.out.println(username);

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

            System.out.println("Password accepted.");
            break;
        }

        Employee emp = EmployeeLoginService.createEmployee(
                name, role, username, password
        );

        if (emp != null) {
            System.out.println("Worker created successfully: ID " + emp.getId());
        } else {
            System.out.println("Failed to save worker.");
        }
        System.out.println("\n===== NEW WORKER CREATED =====");
    }

    public static String generateUsername(String fullName) {
        String[] parts = fullName.trim().toLowerCase().split("\\s+");
        Random r = new Random();

        StringBuilder base = new StringBuilder();

        if (parts.length == 1) {
            String name = parts[0];

            int pattern = r.nextInt(4); // 4 patterns for single-word
            switch (pattern) {
                case 0:
                    base = new StringBuilder(slice(name, 5) + (100 + r.nextInt(900)));       // rihanna341
                    break;
                case 1:
                    base = new StringBuilder(slice(name, 3) + slice(name, 2) + r.nextInt(99)); // adele23
                    break;
                case 2:
                    base = new StringBuilder(slice(name, 4) + "x" + r.nextInt(9999));        // platox889
                    break;
                case 3:
                    base = new StringBuilder(slice(name, 2) + slice(name, 3) + r.nextInt(1000)); // rihanna -> rirhan314
                    break;
            }

            // ensure minimum length 8
            while (base.length() < 8) {
                base.append(r.nextInt(10));
            }

            return base.toString();
        }

        String first = parts[0];
        String middle = parts.length > 2 ? parts[1] : "";
        String last = parts[parts.length - 1];

        int pattern = r.nextInt(7); // 7 patterns
        switch (pattern) {
            case 0:
                base = new StringBuilder(slice(first, 4) + slice(last, 3));
                break;
            case 1:
                base = new StringBuilder(slice(first, 3) + slice(last, 4));
                break;
            case 2:
                base = new StringBuilder(slice(first, 3) + slice(middle, 1) + slice(last, 3));
                break;
            case 3:
                base = new StringBuilder(slice(first, 1) + slice(last, 3 + r.nextInt(3))); // 3–5 letters
                break;
            case 4:
                base = new StringBuilder(slice(last, 3) + slice(first, 3));
                break;
            case 5:
                base = new StringBuilder(slice(first, 4) + slice(last, 4));
                break;
            case 6:
                base = new StringBuilder(slice(first, 2) + slice(last, 4));
                break;
        }

        if (r.nextInt(100) < 40) {
            base.append(r.nextInt(100));
        }

        while (base.length() < 8) {
            base.append(r.nextInt(10));
        }

        return base.toString();
    }

    // ONLY helper allowed
    private static String slice(String s, int n) {
        if (s == null || s.isEmpty()) return "";
        return s.substring(0, Math.min(s.length(), n));
    }
}