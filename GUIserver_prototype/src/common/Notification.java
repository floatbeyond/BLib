package common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Notification implements Serializable {
    private static final long serialVersionUID = 1L;
    private int notificationId;
    private int userId;
    private String message;
    private Timestamp timestamp;

    public Notification(int notificationId, int userId, String message, Timestamp timestamp) {
        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", userId=" + userId +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}