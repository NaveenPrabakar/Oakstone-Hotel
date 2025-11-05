package Main.Employee;

import Main.Guest.*;
import Main.Booking.*;
import Main.Room.room;

import java.io.*;
import java.time.LocalDate;


import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class frontdeskteam extends Employee implements FrontDesk{

    //access to the Guest Database
    private static String GUESTS = "Guest.txt";

    //access to reservation Database
    private static final String RESERVATION = "Reservation.txt";

    //access to keycard Database
    private static final String KEYCARD = "Keycard.txt";

    //access to past reservations
    private static final String PAST_RESERVATIONS = "Past_Reservation.txt";

    private static Housekeeping HousekeepingManager;
    public frontdeskteam(int id, String name) {
        super(id, name, "FrontDesk");
    }

    public void addHouseKeepingManager(Housekeeping manager){
        this.HousekeepingManager = manager;
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
                String[] profile = scnr.nextLine().split(",");
                String name = profile[0];
                int id = Integer.parseInt(profile[1]);

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
    public boolean verifyCheckOut(Guest guest) {
        File reservationFile = new File(RESERVATION);
        File pastFile = new File(PAST_RESERVATIONS);

        List<String> updatedReservations = new ArrayList<>();
        boolean found = false;
        int roomNumber = -1;

        try (Scanner reader = new Scanner(reservationFile)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty() || line.startsWith("reservationId")) continue; // skip header

                String[] parts = line.split(",");
                String resId = parts[0];
                String guestName = parts[1];
                int currentRoom = Integer.parseInt(parts[2]);
                String startDate = parts[3];
                String endDate = parts[4];

                if (guestName.equals(guest.getName())) {
                    found = true;
                    roomNumber = currentRoom;

                    // Record actual checkout date (today)
                    LocalDate actualCheckOut = LocalDate.now();

                    // Append to Past_Reservation.txt with actual checkout date
                    try (PrintWriter pastWriter = new PrintWriter(new FileWriter(pastFile, true))) {
                        if (pastFile.length() == 0) {
                            pastWriter.println("reservationId,guestName,roomNumber,startDate,endDate,actualCheckOutDate");
                        }
                        pastWriter.printf("%s,%s,%d,%s,%s,%s%n",
                                resId, guestName, currentRoom, startDate, endDate, actualCheckOut);
                    }
                } else {
                    updatedReservations.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!found) return false;

        // Rewrite the Reservation.txt with remaining active reservations
        try (PrintWriter writer = new PrintWriter(reservationFile)) {
            writer.println("reservationId,guestName,roomNumber,startDate,endDate");
            for (String record : updatedReservations) {
                writer.println(record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Revoke keycard for this guest
        revokeKeyCard(roomNumber, guest);
        room roomToClean = alertCleaningStaff(roomNumber);
        HousekeepingManager.addToCleanQueue(roomToClean);
        System.out.println(roomToClean.getRoomNumber() + " " + roomToClean.getType());

        return true;
    }

    @Override
    public void revokeKeyCard(int roomNumber, Guest guest) {
        File keycardFile = new File(KEYCARD);
        List<String> updatedRecords = new ArrayList<>();

        try (Scanner reader = new Scanner(keycardFile)) {
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) continue;

                String[] record = line.split("\\s+");
                int recordedRoom = Integer.parseInt(record[0]);
                String recordedGuest = record[2] + " " + record[3];

                // Remove only the matching keycard
                if (!(recordedRoom == roomNumber && guest.getName().equals(recordedGuest))) {
                    updatedRecords.add(line);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Rewrite keycard file without revoked card
        try (PrintWriter writer = new PrintWriter(keycardFile)) {
            for (String record : updatedRecords) {
                writer.println(record);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public room alertCleaningStaff(int roomNumber) {
        try {
            Scanner scnr = new Scanner(new File("Room.txt"));
            boolean roomMatch = false;
            String typeOfRoom = null;
            while (!roomMatch) {
                String roomData = scnr.nextLine();
                if (roomData.startsWith("roomNumber")) {
                    continue;
                }
                String[] roomInfo = roomData.split(",");
                int roomNum = Integer.parseInt(roomInfo[0]);
                if (roomNum == roomNumber) {
                    typeOfRoom = roomInfo[1];
                    roomMatch = true;
                }
            }
            room room = new room(roomNumber,typeOfRoom,null,null,null);
            System.out.println("Alerting the cleaning staff to clean room " + room.getRoomNumber());
            return room;
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
