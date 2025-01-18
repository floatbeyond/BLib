package common;

import java.io.Serializable;
import java.sql.Timestamp;

public class Notification implements Serializable {
    private int notificationId;
    private int subId;
    private String message;
    private Timestamp timestamp;

    public Notification(int notificationId, int subId, String message, Timestamp timestamp) {
        this.notificationId = notificationId;
        this.subId = subId;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public int getNotificationId() { return notificationId; }
    public void setNotificationId(int notificationId) { this.notificationId = notificationId; }

    public int getSubId() { return subId; }
    public void setSubId(int subId) { this.subId = subId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Timestamp getTimestamp() { return timestamp; }
    public void setTimestamp(Timestamp timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "Notification{" +
                "notificationId=" + notificationId +
                ", subId=" + subId +
                ", message='" + message + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}