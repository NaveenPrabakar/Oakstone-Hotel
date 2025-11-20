package Main.Booking;

import Main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ReviewProcess extends Reservation implements reviewInterface{
    private static final String RESERVATION = Main.HOTEL_PATH + "/Reservation.txt";
    private static final String PAST_RESERVATIONS = Main.HOTEL_PATH + "/Past_Reservation.txt";
    private static final String REVIEWS = Main.HOTEL_PATH + "/reviews.txt";
    private Scanner input;

    public ReviewProcess(Reservation reservation) {
        super(reservation.getReservationId(), reservation.getGuestName(), reservation.getRoomNumber(), reservation.getStartDate(), reservation.getEndDate());
        this.input = null;
    }

    public void execute(Scanner input) {
        System.out.println("Thank you for leaving a review!");
        this.input = input;
        int stars = getStarRating();
        System.out.print("Please enter your review (optional): ");
        String review = input.nextLine();
        addReview(REVIEWS, stars, review, this);
        System.out.println("Thank you for submitting a review! A team member will be in touch shortly.");
    }

    private int getStarRating() {
        while (true) {
            System.out.print("How would you rate your stay? (1–5): ");
            
            if (input.hasNextInt()) {
                int stars = input.nextInt();
                input.nextLine();
                
                if (stars >= 1 && stars <= 5) {
                    return stars;
                }
                System.out.println("Invalid number. Please enter a value between 1 and 5.");
            } else {
                System.out.println("Invalid input. Please enter a number between 1 and 5.");
                input.nextLine();
            }
        }
    }

    private void addReview(String path, int stars, String review, Reservation reservation) {
        File reviewFile = new File(path);
        try (PrintWriter writer = new PrintWriter(new FileWriter(reviewFile, true))) {
            if (reviewFile.length() == 0) {
                writer.println("reservationId,guestName,roomNumber,checkoutDate,stars,review,reviewDate,hrResponse,hrRespondent,responseDate,status");
            }
            
            // Get current date for reviewDate
            String reviewDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);

            writer.printf("%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s%n",
                    reservation.getReservationId(),  // reservationId
                    reservation.getGuestName(),  // guestName
                    String.valueOf(reservation.getRoomNumber()),  // roomNumber
                    reservation.getEndDate().toString(),  // checkoutDate
                    stars,                    // stars
                    escapeCSV(review),       // review
                    reviewDate,         // reviewDate
                    "PENDING",                     // hrResponse (empty)  
                    "PENDING",                         // hrRespondent (empty)
                    "PENDING",                 // responseDate (empty));           
                    "PENDING" );          // status
        } catch (IOException e) {
            System.out.println("Error writing review: " + e.getMessage());
        }
    }

    private String escapeCSV(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        // Escape commas and quotes in CSV
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
