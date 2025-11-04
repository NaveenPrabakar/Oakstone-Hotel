package Main.Employee;

import Main.Guest.*;
import Main.Booking.*;
import java.time.LocalDate;


import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;


public class frontdeskteam extends Employee implements FrontDesk{

    //access to the Guest Database
    private static String GUESTS = "src/Main/Guest.txt";

    //access to reservation Database
    private static final String RESERVATION = "src/Main/Reservation.txt";

    //access to keycard Database
    private static final String KEYCARD = "src/Main/Keycard.txt";

    public frontdeskteam(int id, String name) {
        super(id, name, "FrontDesk");
    }

    @Override
    public Reservation verifyCheckIn(Guest guest) {

        File file = new File(RESERVATION);

        try{
            Scanner scnr = new Scanner(file);
            scnr.nextLine();

            while(scnr.hasNextLine()){
                String[] reserve = scnr.nextLine().split(",");

                String revid = reserve[0];
                String name = reserve[1];
                String roomnumber = reserve[2];
                String startDate = reserve[3];
                String endDate = reserve[4];

                if(name.equals(guest.getName())){
                    return new Reservation(
                            revid,
                            name,
                            Integer.parseInt(roomnumber),
                            LocalDate.parse(startDate),
                            LocalDate.parse(endDate)
                    );

                }
            }
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public boolean verifyIdentity(Guest guest) {

        File file = new File(GUESTS);

        try {
            Scanner scnr = new Scanner(file);

            while(scnr.hasNextLine()){
                String[] profile = scnr.nextLine().split(" ");
                String name = profile[0] + " " + profile[1];
                int id = Integer.parseInt(profile[2]);

                if(guest.getName().equals(name) && guest.getid() == id){
                    return true;
                }
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return false;
    }

    @Override
    public void provideKeyCard(int roomnumber, Guest owner) {
        //Create they keycard
        KeyCard keycard = new KeyCard(roomnumber, true, owner);
        File log = new File(KEYCARD);

        //Record the card has been issued
        try{
            PrintWriter write = new PrintWriter(log);
            write.write(keycard.toString());
            write.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean verifyCheckOut() {
        return false;
    }

    @Override
    public void revokeKeyCard() {

    }
}
