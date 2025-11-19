package Main.Employee;

import Main.Room.room;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class HousekeepingRequest {
    private final UUID id;
    private final room roomInfo;
    private final String serviceType;
    private final String timeWindow;
    private final List<String> amenities;
    private final String notes;
    private final boolean guestInitiated;
    private final LocalDateTime requestedAt;
    private final String requestedBy;

    public HousekeepingRequest(room roomInfo,
                               String serviceType,
                               String timeWindow,
                               List<String> amenities,
                               String notes,
                               boolean guestInitiated,
                               String requestedBy) {
        this.id = UUID.randomUUID();
        this.roomInfo = roomInfo;
        this.serviceType = serviceType;
        this.timeWindow = timeWindow;
        this.amenities = amenities == null ? List.of() : List.copyOf(amenities);
        this.notes = notes;
        this.guestInitiated = guestInitiated;
        this.requestedAt = LocalDateTime.now();
        this.requestedBy = requestedBy;
    }

    public UUID getId() {
        return id;
    }

    public room getRoomInfo() {
        return roomInfo;
    }

    public String getServiceType() {
        return serviceType;
    }

    public String getTimeWindow() {
        return timeWindow;
    }

    public List<String> getAmenities() {
        return Collections.unmodifiableList(amenities);
    }

    public String getNotes() {
        return notes;
    }

    public boolean isGuestInitiated() {
        return guestInitiated;
    }

    public LocalDateTime getRequestedAt() {
        return requestedAt;
    }

    public String getRequestedBy() {
        return requestedBy;
    }

    public String formatTicketLine() {
        StringBuilder sb = new StringBuilder();
        sb.append("Room ").append(roomInfo.getRoomNumber()).append(" | ")
          .append(serviceType).append(" @ ").append(timeWindow);
        if (!amenities.isEmpty()) {
            sb.append(" | Amenities: ").append(String.join("; ", amenities));
        }
        if (guestInitiated) {
            sb.append(" | Guest request");
        }
        return sb.toString();
    }
}
