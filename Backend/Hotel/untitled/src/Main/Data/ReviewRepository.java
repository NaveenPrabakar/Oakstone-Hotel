package Main.Data;

import Main.Booking.Reservation;
import Main.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReviewRepository {
    private static final String REVIEWS_FILE = Main.HOTEL_PATH + "/reviews.txt";

    public ReviewRepository() {
    }

    /**
     * Adds a new review to the reviews file
     */
    public void addReview(Reservation reservation, int stars, String reviewText) {
        List<String[]> allReviews = new ArrayList<>();
        String[] header = null;
        boolean found = false;

        // Read all reviews
        File reviewFile = new File(REVIEWS_FILE);
        if (reviewFile.exists()) {
            try (Scanner scanner = new Scanner(reviewFile)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();
                    if (line.isEmpty()) {
                        continue;
                    }
                    
                    String[] parts = line.split(",");
                    
                    if (parts[0].startsWith("reservationId")) {
                        header = parts;
                        continue;
                    }
                    
                    // Check if this reservation already has a review
                    if (parts[0].equals(reservation.getReservationId()) && !found) {
                        String reviewDate = java.time.LocalDate.now().toString();
                        parts[4] = String.valueOf(stars);
                        parts[5] = escapeCSV(reviewText);
                        parts[6] = reviewDate;
                        parts[7] = "PENDING";
                        parts[8] = "PENDING";
                        parts[9] = "PENDING";
                        parts[10] = "PENDING";
                        found = true;
                    }
                    
                    allReviews.add(parts);
                }
            } catch (IOException e) {
                System.out.println("Error reading reviews: " + e.getMessage());
            }
        }

        // If not found, add new review
        if (!found) {
            String reviewDate = java.time.LocalDate.now().toString();
            String[] newReview = {
                reservation.getReservationId(),
                reservation.getGuestName(),
                String.valueOf(reservation.getRoomNumber()),
                reservation.getEndDate().toString(),
                String.valueOf(stars),
                escapeCSV(reviewText),
                reviewDate,
                "PENDING",
                "PENDING",
                "PENDING",
                "PENDING"
            };
            allReviews.add(newReview);
        }

        // Write all reviews back
        writeAllReviews(header, allReviews);
        
        if (found) {
            System.out.println("Review updated successfully!");
        } else {
            System.out.println("Review added successfully!");
        }
    }

    /**
     * Loads all reviews from the file
     */
    public List<String[]> loadAllReviews() {
        List<String[]> reviews = new ArrayList<>();
        File reviewFile = new File(REVIEWS_FILE);
        
        if (!reviewFile.exists()) {
            return reviews;
        }
        
        try (Scanner scanner = new Scanner(reviewFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(",");
                reviews.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Error reading reviews: " + e.getMessage());
        }
        
        return reviews;
    }

    /**
     * Updates a review with HR response
     */
    public void addHRResponse(String reservationId, String hrName, String response) {
        List<String[]> allReviews = new ArrayList<>();
        String[] header = null;
        boolean found = false;

        // Read all reviews
        File reviewFile = new File(REVIEWS_FILE);
        try (Scanner scanner = new Scanner(reviewFile)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.isEmpty()) {
                    continue;
                }
                
                String[] parts = line.split(",");
                
                if (parts[0].startsWith("reservationId")) {
                    header = parts;
                    continue;
                }
                
                // Check if this is the review we want to reply to
                if (parts[0].equals(reservationId) && !found) {
                    parts[7] = escapeCSV(response);
                    parts[8] = hrName;
                    parts[9] = java.time.LocalDate.now().toString();
                    parts[10] = "REPLIED";
                    found = true;
                }
                
                allReviews.add(parts);
            }
        } catch (IOException e) {
            System.out.println("Error reading reviews: " + e.getMessage());
            return;
        }

        // Write all reviews back
        writeAllReviews(header, allReviews);

        if (found) {
            System.out.println("Reply saved successfully!");
        } else {
            System.out.println("Review not found for reservation ID: " + reservationId);
        }
    }

    /**
     * Writes all reviews back to the file
     */
    private void writeAllReviews(String[] header, List<String[]> reviews) {
        File reviewFile = new File(REVIEWS_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(reviewFile, false))) {
            if (header != null) {
                writer.println(String.join(",", header));
            } else {
                writer.println("reservationId,guestName,roomNumber,checkoutDate,stars,review,reviewDate,hrResponse,hrRespondent,responseDate,status");
            }
            
            for (String[] review : reviews) {
                writer.println(String.join(",", review));
            }
        } catch (IOException e) {
            System.out.println("Error writing reviews: " + e.getMessage());
        }
    }

    /**
     * Escapes CSV special characters
     */
    private String escapeCSV(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
