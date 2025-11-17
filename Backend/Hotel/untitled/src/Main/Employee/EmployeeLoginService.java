package Main.Employee;

import Main.Main;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class EmployeeLoginService {
    public static Employee login(String username, String password) throws FileNotFoundException {
        File file = new File(Main.HOTEL_PATH + "/" + "Employee.txt");

        if (!file.exists()) {
            System.out.println("Employee file not found for hotel: " + Main.HOTEL_PATH);
            return null;
        }

        Scanner sc = new Scanner(file);

        while (sc.hasNextLine()) {
            String[] tokens = sc.nextLine().split("\\s+");
            if (tokens.length < 6) continue;

            int id = Integer.parseInt(tokens[0]);
            String fullName = tokens[1] + " " + tokens[2];
            String role = tokens[3];
            String storedUsername = tokens[4];
            String storedPassword = tokens[5];

            if (storedUsername.equals(username) && storedPassword.equals(password)) {
                // Build correct employee class
                switch (role) {
                    case "FrontDesk":
                        return new frontdeskteam(id, fullName);
                    case "Cleaner":
                        return new Housekeeping(id, fullName);
                    case "Executive":
                        return new ExecutivePanel(id, fullName, role);
                    case "DataTeam":
                        return new DataPanel(id, fullName, role);
                }
            }
        }

        return null;
    }
}