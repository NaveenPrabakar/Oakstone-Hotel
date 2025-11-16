package Main.Employee;

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
}