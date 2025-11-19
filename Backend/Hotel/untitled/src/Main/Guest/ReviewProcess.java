package Main.Guest;

import Main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class ReviewProcess {
    private static final String RESERVATION = Main.HOTEL_PATH + "/Reservation.txt";
    private static final String PAST_RESERVATIONS = Main.HOTEL_PATH + "/Past_Reservation.txt";
    private static final String REVIEWS = Main.HOTEL_PATH + "/reviews.txt";
    private final Scanner input;

    public ReviewProcess(Scanner input) {
        this.input = input;
    }

    public void execute() {
        System.out.println("Thank you for leaving a review!");
        System.out.println("What's your name?");
        String name = input.nextLine().trim();
        System.out.println("What's your reservation ID?");
        String resId = input.nextLine().trim();

        String[] customerInformation = findReservation(name, resId);
        
        if (customerInformation == null) {
            System.out.println("Sorry, I can't find your reservation.");
            return;
        }

        System.out.println("I have located your booking! You stayed with us until " + customerInformation[4]);

        int stars = getStarRating();
        System.out.print("Please enter your review (optional): ");
        String review = input.nextLine();
        addReview(REVIEWS, stars, review, customerInformation);
        System.out.println("Thank you for submitting a review! A team member will be in touch shortly.");
    }

    private String[] findReservation(String name, String resId) {
        String[] customerInfo = checkReservations(RESERVATION, name, resId);
        if (customerInfo == null) {
            customerInfo = checkReservations(PAST_RESERVATIONS, name, resId);
        }
        return customerInfo;
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

    private void addReview(String path, int stars, String review, String[] customerInformation) {
        File reviewFile = new File(path);
        try (PrintWriter writer = new PrintWriter(new FileWriter(reviewFile, true))) {
            if (reviewFile.length() == 0) {
                writer.println("reservationId,guestName,roomNumber,checkoutDate,stars,review,reviewDate,hrResponse,hrRespondent,responseDate,status");
            }
            
            // Get current date for reviewDate
            String reviewDate = java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ISO_LOCAL_DATE);
            
            // customerInformation[0] = reservationId
            // customerInformation[1] = guestName
            // customerInformation[2] = roomNumber
            // customerInformation[4] = checkoutDate
            writer.printf("%s,%s,%s,%s,%d,%s,%s,%s,%s,%s,%s%n",
                    customerInformation[0],  // reservationId
                    customerInformation[1],  // guestName
                    customerInformation[2],  // roomNumber
                    customerInformation[4],  // checkoutDate
                    stars,                    // stars
                    escapeCSV(review),       // review
                    reviewDate,         // reviewDate
                    "PENDING",                     // hrResponse (empty)  
                    "PENDING",                         // hrRespondent (empty)
                    "PENDING",                 // responseDate (empty));           
                    "PENDING"           // status
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

    private String[] checkReservations(String path, String nameInput, String resIdInput) {
        try (Scanner scn = new Scanner(new File(path))) {
            while (scn.hasNextLine()) {
                String line = scn.nextLine().trim();
                if (line.isEmpty() || line.startsWith("reservationId")) {
                    continue;
                }
                
                String[] parts = line.split(",");
                if (parts.length < 5) {
                    continue;
                }
                
                String resId = parts[0];
                String guestName = parts[1];
                
                if (resIdInput.equals(resId) && nameInput.equals(guestName)) {
                    return parts;
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading reservations: " + e.getMessage());
        }
        return null;
    }
}
