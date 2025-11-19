package Main.Guest;

import Main.Main;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLOutput;
import java.util.Scanner;

public class ReviewProcess {
    private static final String RESERVATION = Main.HOTEL_PATH + "/" + "Reservation.txt";
    private static final String PAST_RESERVATIONS = Main.HOTEL_PATH + "/" + "Past_Reservation.txt";
    private static final String REVIEWS = Main.HOTEL_PATH + "/" + "reviews.txt";
    private final Scanner input;

    public ReviewProcess(Scanner input) {
        this.input = input;
    }

    public void execute(){
        System.out.println("Thankyou for leaving a review!");
        System.out.println("Whats your name?");
        String name = input.nextLine().trim();
        System.out.println("Whats your resId?");
        String resId = input.nextLine().trim();

        String[] customerInformation_reserved = checkReservations(RESERVATION, name, resId);
        String[] customerInformation_past = checkReservations(PAST_RESERVATIONS, name, resId);

        if(customerInformation_reserved != null ){
            System.out.print("I have located your booking! ");
            System.out.println("I can see that youre going to stay with us until "+ customerInformation_reserved[4] );
        }else if(customerInformation_past != null ){
            System.out.print("I have located your booking! ");
            System.out.println("I can see that you stayed with us until "+ customerInformation_past[4]);
        }else{
            System.out.println("Sorry i cant find your reservation");
            return;
        }
        String[] customerInfo = customerInformation_reserved != null ? customerInformation_reserved: customerInformation_past;

        // --- STAR RATING VALIDATION ---
        int stars = -1;
        while (true) {
            System.out.print("How would you rate your stay? (1–5): ");

            if (input.hasNextInt()) {
                stars = input.nextInt();
                input.nextLine(); // consume leftover newline

                if (stars >= 1 && stars <= 5) {
                    break; // valid
                } else {
                    System.out.println("Invalid number. Please enter a value between 1 and 5.");
                }
            } else {
                System.out.println("Invalid input. Please enter a value between 1 and 5.");
                input.nextLine(); // discard non-integer
            }
        }

        System.out.print("Please enter your review (optional): ");
        String review = input.nextLine();   // no validation needed
        customerInfo[5] = String.valueOf(stars);
        customerInfo[6] = review;
        addReview(REVIEWS,customerInfo);
        System.out.println("Thankyou for submitting a review! A team member would be in touch shortly");
    }

    private void addReview(String path, String[] customerInformation){
        File reviewFile = new File(path);
        try (PrintWriter pastWriter = new PrintWriter(new FileWriter(reviewFile, true))) {
            if (reviewFile.length() == 0) {
                pastWriter.println("reservationId,guestName,roomNumber,review");
            }
            pastWriter.printf("%s,%s,%d,%s",
                    customerInformation[0], customerInformation[1], customerInformation[2], customerInformation[5], customerInformation[6]);
        } catch (IOException e) {
            System.out.println("Uh-oh "+ e);
        }
    }


    private String[] checkReservations(String path, String nameInput, String resIdInput){
        try {
            File file = new File(path);
            Scanner scn = new Scanner(file);

            while(scn.hasNextLine()) {
                String line = scn.nextLine().trim();
                if (line.isEmpty() || line.startsWith("reservationId")) continue; // skip header
                String[] parts = line.split(",");
                String resId = parts[0];
                String guestName = parts[1];
                int currentRoom = Integer.parseInt(parts[2]);
                String startDate = parts[3];
                String endDate = parts[4];

                if (resIdInput.equals(resId) && nameInput.equals(guestName)) {
                    parts[5] = "T";
                    return parts;
                }
            }

        }catch(Exception e){
            System.out.println("Uh-oh: "+e);
        }
        return null;
    }
}
