package Main;

import java.io.FileNotFoundException;
import java.util.*;
import Main.Booking.Booking;
import java.util.Scanner;
import java.io.File;
import Main.Employee.*;
import Main.Guest.CheckSystemController;
import Main.Room.room;


public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sc = new Scanner(System.in);
        boolean running = true;

        File file = new File("Employee.txt");


        Scanner scnr = new Scanner(file);

        ArrayList<Employee> employees = new ArrayList<>();

        ArrayList<Housekeeping> housekeepings = new ArrayList<>();

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
                employees.add(new Housekeeping(id, name));
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
            else if (choice.equals("3")) {
                System.out.println("Check Out option selected.");
                Employee frontDesk = null;

                // Find available FrontDesk Employee
                for (Employee e : employees) {
                    if (e.role().equals("FrontDesk")) {
                        frontDesk = e;
                        break;
                    }
                }

                // Call the checkout system
                CheckSystemController.RunCheckOut(frontDesk);
                break;
            }

            else if(choice.equals("9")){
                int RoomNumber = 10;
                int i = 0;
                while( i < 3){
                    housekeepings.add(new Housekeeping(i, "alpha"));
                    System.out.println("added new housekeeping "+ i);
                    i++;
                }
                while(true){
                    housekeepings.get(0).addToCleanQueue(new room(RoomNumber, null, null, null, null));
                    RoomNumber+=1;
                }
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

