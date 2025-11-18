package Main.Controller;

import Main.Employee.Employee;
import Main.Employee.frontdeskteam;
import Main.*;
import Main.Guest.GuestSession;

import java.util.Scanner;

public class CheckSystemController {
    public static void RunCheckin(Employee frontDesk){
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the hotel location you're checking into (e.g., Chicago, DesMoines): ");
        String hotel = sc.nextLine().trim();
        Main.HOTEL_PATH = hotel;

        System.out.println("Waiting for FrontDesk Employee at " + hotel + "...");

        frontdeskteam.addToQueue(() -> {
            System.out.println("===== HOTEL CHECK-IN SYSTEM =====");
            System.out.println();
            System.out.println("Hi, Can you provide your name?");

            Employee desk = new frontdeskteam(1, "FrontDesk");
            GuestSession check_in = new GuestSession();
            check_in.checkin(desk);
        }, Main.HOTEL_PATH);
    }

    public static void RunCheckOut(Employee frontDesk){
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter the hotel location you're checking out of (e.g., Chicago, DesMoines): ");
        String hotel = sc.nextLine().trim();
        Main.HOTEL_PATH = hotel;

        System.out.println("Waiting for FrontDesk Employee at " + hotel + "...");

        frontdeskteam.addToQueue(() -> {
            System.out.println();
            System.out.println("===== HOTEL CHECK-OUT SYSTEM =====");
            System.out.println();

            Employee desk = new frontdeskteam(1, "FrontDesk");
            GuestSession check_in = new GuestSession();
            check_in.checkout(desk);
        }, Main.HOTEL_PATH);
    }
}