package Main.Employee;
import Main.Room.room;

import java.util.LinkedList;
import java.util.Queue;

public class Housekeeping extends Employee implements CleaningStaff {
    private Thread listenerThread;
    private static final Queue<room> toCleanQueue = new LinkedList<>();
    private boolean running;
    private static boolean notLayoffs = true;

    public Housekeeping(int id, String name) {
        super(id, name, "CleaningStaff");
        running = true;
        StartCleaning();
    }

    public void addToCleanQueue(room roomToClean){
        //adds to queue
        synchronized(toCleanQueue){
            this.toCleanQueue.add(roomToClean);
            toCleanQueue.notify();
        }
    }

    private void StartCleaning(){
        listenerThread = new Thread(()->{
           while(running && notLayoffs){
               room roomToClean = null;
               synchronized(toCleanQueue){
                   while(toCleanQueue.isEmpty()){
                       try {
                           toCleanQueue.wait();
                       } catch (InterruptedException e) {
                           throw new RuntimeException(e);
                       }
                   }
                   roomToClean = toCleanQueue.remove();
               }
               clean(roomToClean);
           }
        });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void StopCleaning(){
        running = false;
        synchronized(toCleanQueue){
            toCleanQueue.notifyAll();
        }
    }

    private static void fireAllWorkers(){
        notLayoffs = false;
        System.out.println("Everyone is fired");
        synchronized (toCleanQueue){
            toCleanQueue.notifyAll();
        }
    }

    private void clean(room roomToClean){
        System.out.println("Cleaning Room: " + roomToClean.getRoomNumber());
        try{
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Cleaning Room Done: " + roomToClean.getRoomNumber());

    }

}
