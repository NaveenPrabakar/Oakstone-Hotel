package Main.Guest;

import Main.Employee.Employee;
import Main.Employee.frontdeskteam;

public class CheckSystemController {
    public static void RunCheckin(Employee frontDesk){

        System.out.println("Waiting for FrontDesk Employee");

        frontdeskteam.addToQueue(() -> {
            System.out.println("===== HOTEL CHECK-IN SYSTEM =====");
            System.out.println();
            System.out.println("Hi, I am " + frontDesk.getName() + ", Can you provide your name?");

            GuestSession check_in = new GuestSession();
            check_in.checkin(frontDesk);
        });
    }

    public static void RunCheckOut(Employee frontDesk){

        System.out.println("Waiting for FrontDesk Employee");

        frontdeskteam.addToQueue(() -> {
            System.out.println();
            System.out.println("===== HOTEL CHECK-OUT SYSTEM =====");
            System.out.println();

            GuestSession check_out = new GuestSession();
            check_out.checkout(frontDesk);
        });
    }
}
