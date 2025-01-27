package common;

import java.io.Serializable;
import java.time.LocalDate;
import java.sql.Timestamp;

/**
 * Represents an order record in the library system.
 * Implements Serializable for client-server communication.
 */
public class OrderRecordDTO implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private int orderId;
    private String bookTitle;
    private LocalDate orderDate;
    private Timestamp notificationTimestamp;
    private String status;

    /**
     * Constructor for OrderRecordDTO
     * @param orderId
     * @param bookTitle
     * @param orderDate
     * @param status
     * @param notificationTimestamp
     */
    public OrderRecordDTO(int orderId, String bookTitle, LocalDate orderDate, String status, Timestamp notificationTimestamp) {
        this.orderId = orderId;
        this.bookTitle = bookTitle;
        this.orderDate = orderDate;
        this.status = status;
        this.notificationTimestamp = notificationTimestamp;
    }

    // Getters and setters for all fields
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getNotificationTimestamp() { return notificationTimestamp; }
    public void setNotificationTimestamp(Timestamp notificationStamp) { this.notificationTimestamp = notificationStamp; }

    @Override
    public String toString() {
        return "OrderRecordDTO{" +
                "orderId=" + orderId + '\'' +
                "bookTitle='" + bookTitle + '\'' +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", notificationTimestamp=" + notificationTimestamp +
                '}';
    }
}