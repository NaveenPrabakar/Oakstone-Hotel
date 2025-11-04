package Main;

import java.io.FileNotFoundException;
import java.util.*;
import Main.Booking.Booking;
import java.util.Scanner;
import java.io.File;
import Main.Employee.*;
import Main.Guest.CheckSystemController;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        File file = new File("Backend/Hotel/untitled/src/Main/Employee.txt");


        Scanner scnr = new Scanner(file);

        ArrayList<Employee> employees = new ArrayList<>();

        //Get List of Employees Working
        while(scnr.hasNextLine()){
            String line = scnr.nextLine();
            String[] employee = line.split(" ");

            int id = Integer.parseInt(employee[0]);
            String name = employee[1] + employee[2];
            String position = employee[3];

            if(position.equals("FrontDesk")){
                employees.add(new frontdeskteam(id, name));
            }
            else if (position.equals("Cleaner")){
                employees.add(new cleaningstaffteam(id, name));
            }

        }

        while (running) {
            printMenu();

            System.out.print("\nSelect an option (1–5) or 0 to exit: ");
            String choice = sc.nextLine().trim();

            if(choice.equals("1")){
                Booking.handleBooking();
                break;
            }
            else if(choice.equals("2")) {
                System.out.println("Check In option selected.");
                Employee frontDesk = null;

                //Find available FrontDesk Employee
                for (Employee e : employees) {
                    if (e.role().equals("FrontDesk")) {
                        frontDesk = e;
                        break;
                    }
                }

                CheckSystemController.RunCheckin(frontDesk);
                break;
            }

            if (running) {
                System.out.println("\nPress Enter to return to main menu...");
                sc.nextLine();
            }

        }
        scnr.close();
    }

    private static void printMenu() {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║             HOTEL ERP SYSTEM           ║");
        System.out.println("╠════════════════════════════════════════╣");
        System.out.println("║ 1. Book                                ║");
        System.out.println("║ 2. Check In                            ║");
        System.out.println("║ 3. Check Out                           ║");
        System.out.println("║ 4. Front Desk                          ║");
        System.out.println("║ 5. Cleaning Staff                      ║");
        System.out.println("║ 0. Exit                                ║");
        System.out.println("╚════════════════════════════════════════╝");
    }
}

