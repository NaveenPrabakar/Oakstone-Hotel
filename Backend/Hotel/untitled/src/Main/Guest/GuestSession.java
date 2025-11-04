package Main.Guest;

import Main.Booking.Reservation;
import Main.Employee.Employee;
import Main.Employee.FrontDesk;

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
    public void checkout(Employee frontdesk) {

    }
}


