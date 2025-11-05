package Main.Guest;

public enum RoomAccessResult {
    ACCESS_GRANTED("Access granted. Door unlocked."),
    CARD_INACTIVE("Access denied: your key card is inactive."),
    INVALID_GUEST("Access denied: the card is not assigned to you."),
    ROOM_MISMATCH("Access denied: the card does not unlock this room."),
    ROOM_ALREADY_UNLOCKED("Door already unlocked. Please enter."),
    SYSTEM_ERROR("System error: maintenance has been notified. Please contact the front desk.");

    private final String message;

    RoomAccessResult(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return this == ACCESS_GRANTED;
    }

    public boolean isSystemIssue() {
        return this == SYSTEM_ERROR;
    }
}
