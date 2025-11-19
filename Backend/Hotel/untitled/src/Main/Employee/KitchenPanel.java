package Main.Employee;

import Main.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;

public class KitchenPanel extends Employee {

    private static final LinkedBlockingQueue<KitchenOrder> kitchenQueue = new LinkedBlockingQueue<>();

    public KitchenPanel(int staffID, String name) {
        super(staffID, name, "Kitchen");
    }

    public static void submitOrder(KitchenOrder order) {
        if (order == null) {
            return;
        }
        kitchenQueue.add(order);
        System.out.println("🍽️ Room service order queued for Room " + order.getRoomNumber());
        ExecutivePanel.notifications.add("Room " + order.getRoomNumber() + " placed a room service order.");
    }

    public static void processOrders(String staffName) {
        if (Main.HOTEL_PATH == null || Main.HOTEL_PATH.isEmpty()) {
            System.out.println("Hotel location not set. Please log in through the worker portal first.");
            return;
        }

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n===== KITCHEN PANEL =====");
            System.out.println("Logged in as " + staffName + " at " + Main.HOTEL_PATH);
            System.out.println("1. View pending orders");
            System.out.println("2. Complete an order");
            System.out.println("3. Exit Kitchen Panel");
            System.out.print("Choose an option: ");

            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    viewOrders();
                    break;
                case "2":
                    completeOrder(sc, staffName);
                    break;
                case "3":
                    running = false;
                    break;
                default:
                    System.out.println("Invalid option. Choose 1-3.");
            }
        }
    }

    private static void viewOrders() {
        List<KitchenOrder> orders = collectOrdersForHotel();
        if (orders.isEmpty()) {
            System.out.println("No pending kitchen orders for this hotel.");
            return;
        }

        System.out.println("\n--- Pending Kitchen Orders ---");
        for (int i = 0; i < orders.size(); i++) {
            System.out.println((i + 1) + ". " + orders.get(i).formatLineItem());
        }
    }

    private static void completeOrder(Scanner sc, String staffName) {
        List<KitchenOrder> orders = collectOrdersForHotel();
        if (orders.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }

        System.out.println("Select the order to complete: ");
        for (int i = 0; i < orders.size(); i++) {
            System.out.println((i + 1) + ". " + orders.get(i).formatLineItem());
        }
        System.out.print("Enter number (or 0 to cancel): ");

        String input = sc.nextLine().trim();
        int idx;
        try {
            idx = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid number.");
            return;
        }

        if (idx == 0) {
            return;
        }

        if (idx < 1 || idx > orders.size()) {
            System.out.println("Selection out of range.");
            return;
        }

        KitchenOrder selected = orders.get(idx - 1);
        kitchenQueue.remove(selected);
        System.out.println("Order for Room " + selected.getRoomNumber() + " completed by " + staffName + ".");
    }

    private static List<KitchenOrder> collectOrdersForHotel() {
        List<KitchenOrder> result = new ArrayList<>();
        for (KitchenOrder order : kitchenQueue) {
            if (order.belongsToHotel(Main.HOTEL_PATH)) {
                result.add(order);
            }
        }
        return result;
    }
}
