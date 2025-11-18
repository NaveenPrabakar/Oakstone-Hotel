package Main.Data;

import java.util.List;
import Main.Booking.*;
import Main.Guest.*;
import Main.Room.*;

public interface DataRepository {
    List<String[]> loadGuests();
    List<String[]> loadReservations();
    List<String[]> loadPastReservations();
    List<String[]> loadRooms();
}
