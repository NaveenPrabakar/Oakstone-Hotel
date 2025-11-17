package Main.Guest;

public class DeskTask {
    private final Runnable task;
    private final String hotelPath;

    public DeskTask(Runnable task, String hotelPath) {
        this.task = task;
        this.hotelPath = hotelPath;
    }

    public Runnable getTask() {
        return task;
    }

    public String getHotelPath() {
        return hotelPath;
    }
}