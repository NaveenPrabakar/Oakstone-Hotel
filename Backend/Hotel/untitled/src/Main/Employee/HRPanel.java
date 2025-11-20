package Main.Employee;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import Main.*;
import Main.Booking.Reservation;
import Main.Data.ReviewRepository;

public class HRPanel extends Employee implements HRTeam {

    private ArrayList<String[]> reviews = new ArrayList<>();
    private ReviewRepository reviewRepository;

    public HRPanel(int StaffID, String name, String role) {
        super(StaffID, name, role);
        this.reviewRepository = new ReviewRepository();
    }

    @Override
    public void viewReviews() {
        // Implementation for viewing reviews
        System.out.println("\n--- Viewing: " + this.getName() + " ---");
        System.out.println("--------------------------------------------");

        List<String[]> allReviews = reviewRepository.loadAllReviews();
        int reviewCount = 0;
        
        for(String[] line : allReviews) {
            if (line.length == 0) {
                continue; // Skip empty lines
            }
            if (line[0].startsWith("reservationId")) {
                System.out.println("HEADER || " + String.join(" | ", line));
                continue; // Skip adding header to reviews list
            }
            reviews.add(line);
            reviewCount++;
            System.out.println(reviewCount + " || " + String.join(" | ", line));
        }
        if (reviews.size() == 0) {
            System.out.println("No reviews available.");
            return;
        }
        replytoReviews();
        reviews.clear();
    }

    private void replytoReviews() {
        System.out.println("\nTotal Reviews: " + reviews.size());
        System.out.println("--------------------------------------------");
        System.out.println("Select which one to review (1-" + (reviews.size()) + "): ");
        System.out.println("Enter 0 to exit.");

        Scanner sc = new Scanner(System.in);
        int choice;
        if (sc.hasNextInt()) {
            choice = sc.nextInt();
        } else {
            System.out.println("Invalid input. Please enter a number.");
            sc.next(); // consume invalid input
            return;
        }
        sc.nextLine();
        if (choice == 0) {
            System.out.println("Exiting...");
            return;
        }
        if (choice < 1 || choice > reviews.size()) {
            System.out.println("Invalid choice.");
            return;
        }
        System.out.println("Enter your reply: ");
        String reply = sc.nextLine();

        // Get the selected review
        String[] selectedReview = reviews.get(choice - 1);
        // Create a simple reservation object with just the ID
        Reservation reservationId = new Reservation(selectedReview[0],selectedReview[1],Integer.parseInt(selectedReview[2]), LocalDate.parse(selectedReview[3]),LocalDate.parse(selectedReview[3]));
        
        System.out.println("------------------");
        System.out.println("Replying to: \n" + selectedReview[1] + ": " +  selectedReview[5]);
        System.out.println("Your Reply: " + reply);
        System.out.println("------------------");
        
        // Use repository to save the reply
        reviewRepository.addHRResponse(reservationId, this.getName(), reply);
    }

    @Override
    public void hireWorkers(JobApplication application) {
        // Implementation for hiring workers
        System.out.println("Hiring new workers...");
    }

    @Override
    public void fireWorkers(Employee employee) {
        // Implementation for firing workers
        System.out.println("Firing workers...");
    }

    @Override
    public void massiveLayoffs() {
        // Implementation for massive layoffs
        System.out.println("Conducting massive layoffs...");
    }

    @Override
    public void viewNotifications() {
        // Implementation for viewing notifications
        System.out.println("Viewing notifications...");
    }
    
}
