package Main.Guest;

import Main.Employee.*;
import java.util.*;
import Main.Booking.*;

public class CheckSystemController {
    public static void RunCheckin(Employee frontDesk){
        System.out.println("===== HOTEL CHECK-IN SYSTEM =====");
        System.out.println("Hi, I am " + frontDesk.getName() + ", Can you provide your name?");

        GuestSession check_in = new GuestSession();
        check_in.checkin(frontDesk);
    }
}
