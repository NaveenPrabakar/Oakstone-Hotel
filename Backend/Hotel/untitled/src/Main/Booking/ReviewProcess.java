package Main.Booking;

import Main.Data.ReviewRepository;

import java.util.Scanner;

public class ReviewProcess extends Reservation implements reviewInterface{
    private Scanner input;
    private ReviewRepository reviewRepository;

    public ReviewProcess(Reservation reservation) {
        super(reservation.getReservationId(), reservation.getGuestName(), reservation.getRoomNumber(), reservation.getStartDate(), reservation.getEndDate());
        this.input = null;
        this.reviewRepository = new ReviewRepository();
    }

    public void execute(Scanner input) {
        System.out.println("Thank you for leaving a review!");
        this.input = input;
        int stars = getStarRating();
        System.out.print("Please enter your review (optional): ");
        String review = input.nextLine();
        reviewRepository.addReview(this, stars, review);
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
}
