package Main.Guest;

import Main.Booking.Reservation;
import Main.Employee.Employee;
import Main.Employee.FrontDesk;

import java.time.LocalTime;
import java.util.Scanner;

public class GuestSession implements CheckingProcess {

    @Override
    public void checkin(Employee frontDesk) {
        boolean process = true;
        Scanner scnr = new Scanner(System.in);

        while(process){
            String name = scnr.nextLine();
            System.out.println("Okay, and could you provide your id please?");
            int id = scnr.nextInt();

            Guest guest = new Guest(name, id);
            //Downcast to use FrontDesk
            boolean check = ((FrontDesk) frontDesk).verifyIdentity(guest);

            if(check){
                System.out.println("Thank you. Give me a moment to verify your reservation");
                Reservation reserve = ((FrontDesk) frontDesk).verifyCheckIn(guest);
                System.out.println();
                System.out.println("Thanks for waiting, you have a reservation from " + reserve.getStartDate() + " to " + reserve.getEndDate());
                System.out.println();
                ((FrontDesk) frontDesk).provideKeyCard(reserve.getRoomNumber(), guest);
                System.out.println();
                System.out.println("Here is your keycard for room " + reserve.getRoomNumber() );
                break;
            }
            System.out.println("Sorry sir, you can't verify your identity");
            break;
        }

        scnr.close();
    }

    @Override
    public void checkout(Employee frontDesk) {
        Scanner scnr = new Scanner(System.in);
        LocalTime now = LocalTime.now();

        String greeting;
        if (now.isBefore(java.time.LocalTime.NOON)) {
            greeting = "Good morning";
        } else if (now.isBefore(java.time.LocalTime.of(17, 0))) {
            greeting = "Good afternoon";
        } else if (now.isBefore(java.time.LocalTime.of(21, 0))) {
            greeting = "Good evening";
        } else {
            greeting = "Good night";
        }

        System.out.println(greeting + "! Checking out today?");
        System.out.print("Can I get the name on the reservation? ");
        String name = scnr.nextLine();
        System.out.print("And the ID you used during check-in? ");
        int id = scnr.nextInt();
        scnr.nextLine(); // consume leftover newline

        Guest guest = new Guest(name, id);

        // Verify identity
        boolean verified = ((FrontDesk) frontDesk).verifyIdentity(guest);
        if (!verified) {
            System.out.println("Hmm, I’m not finding a match with that name and ID. Could you double-check them for me?");
            return;
        }

        // Attempt checkout
        boolean checkedOut = ((FrontDesk) frontDesk).verifyCheckOut(guest);
        if (checkedOut) {
            System.out.println("Alright, " + guest.getName() + ", I found your reservation.");
            System.out.println("Your checkout is all set. I’ll take your keycard—thank you!");
            System.out.println("We hope you had a pleasant stay. Safe travels!");
        } else {
            System.out.println("It looks like you don’t have any active reservations right now.");
            System.out.println("If you’ve already checked out earlier, you’re all good.");
        }
        scnr.close();
    }
}


