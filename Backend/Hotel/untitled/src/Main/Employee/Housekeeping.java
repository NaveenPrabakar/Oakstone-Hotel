package Main.Employee;
import Main.Room.room;
import java.util.concurrent.LinkedBlockingQueue;

public class Housekeeping extends Employee implements CleaningStaff {
    private static final LinkedBlockingQueue<room> toCleanQueue = new LinkedBlockingQueue<>();
    private static String currentCleaner = null;
    private static boolean active = true;

    public Housekeeping(int id, String name) {
        super(id, name, "CleaningStaff");
    }

    public void addToCleanQueue(room roomToClean) {
        try {
            toCleanQueue.put(roomToClean);
            System.out.println("A new cleaning request has been added to the housekeeping queue.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void processCleaning(String cleanerName) {
        if (!active) {
            System.out.println("Housekeeping is currently inactive.");
            return;
        }

        currentCleaner = cleanerName;
        System.out.println(cleanerName + " logged in to housekeeping.");

        while (!toCleanQueue.isEmpty()) {
            try {
                room roomToClean = toCleanQueue.take();
                System.out.println(cleanerName + " is now cleaning room " + roomToClean.getRoomNumber());
                clean(roomToClean);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println("All pending cleaning tasks completed.");
        System.out.println(cleanerName + " logged out of housekeeping.");
        currentCleaner = null;
    }

    private static void clean(room roomToClean) {
        synchronized (roomToClean) {
            System.out.println("Cleaning Room: " + roomToClean.getRoomNumber());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("Cleaning Room Done: " + roomToClean.getRoomNumber());
        }
    }

    private static void fireAllWorkers() {
        active = false;
        System.out.println("Everyone is fired.");
    }
}
