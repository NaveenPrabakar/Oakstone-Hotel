package Main.Employee;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Scanner;
import Main.*;

public class HRPanel extends Employee implements HRTeam {

    ArrayList<String[]> reviews = new ArrayList<>();

    public HRPanel(int StaffID, String name, String role) {
        super(StaffID, name, role);
    }

    @Override
    public void viewReviews() {
        File selected = new File(Main.HOTEL_PATH + "/reviews.txt");
        // Implementation for viewing reviews
        System.out.println("\n--- Viewing: " + selected.getName() + " ---");
        System.out.println("--------------------------------------------");

        try (Scanner reader = new Scanner(selected)) {
            while (reader.hasNextLine()) {
                String[] line = reader.nextLine().split(",");
                
                if (line.length == 0 || line[0].startsWith("reservationId")) {
                    System.out.print("   || ");
                    for (int i = 0; i < line.length; i++) {
                        System.out.print(line[i].trim() + " | ");
                    }
                    System.out.println();
                    continue;
                }
                System.out.print(reviews.size()+1 + "|| ");
                for (int i = 0; i < line.length; i++) {
                    System.out.print(line[i].trim() + " | ");
    
                }
                reviews.add(line);
                System.out.println();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }

        if (reviews.size() == 0) {
            System.out.println("No reviews available.");
            return;
        }
        replytoReviews();
        refreshReviews();
        reviews = new ArrayList<>();
        
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

        // Update the reply in the reviews array
        String[] selectedReview = reviews.get(choice - 1);
        selectedReview[7] = reply;
        selectedReview[8] = this.getName();
        selectedReview[9] = java.time.LocalDate.now().toString();
        selectedReview[10] = "REPLIED";
        System.out.println("------------------");
        System.out.println("Replying to: \n" + selectedReview[1] + ": " +  selectedReview[5]);
        System.out.println("Your Reply: " + reply);
        System.out.println("------------------");

        //TODO: Save reply somewhere
    }

    private void refreshReviews(){
        // Write updated reviews back to file
        try (java.io.PrintWriter writer = new java.io.PrintWriter(Main.HOTEL_PATH + "/reviews.txt")) {
                writer.println("reservationId,guestName,roomNumber,checkoutDate,stars,review,reviewDate,hrResponse,hrRespondent,responseDate,status");
            for (String[] review : reviews) {
                writer.println(String.join(",", review));
            }
            System.out.println("Reply saved successfully!");
        } catch (FileNotFoundException e) {
            System.out.println("Error saving reply: " + e.getMessage());
        }
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
