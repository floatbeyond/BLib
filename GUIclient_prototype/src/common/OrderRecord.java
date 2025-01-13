package common;

import java.io.Serializable;
import java.time.LocalDate;

public class OrderRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private int orderId;
    private int bookId; // Foreign key referencing Book
    private int subId; // Foreign key referencing Subscribers
    private LocalDate orderDate;
    private String status;
    private LocalDate notificationStamp;

    public OrderRecord(int orderId, int bookId, int subId, LocalDate orderDate, String status, LocalDate notificationStamp) {
        this.orderId = orderId;
        this.bookId = bookId;
        this.subId = subId;
        this.orderDate = orderDate;
        this.status = status;
        this.notificationStamp = notificationStamp;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getBookId() {
        return bookId;
    }

    public int getSubId() {
        return subId;
    }

    public LocalDate getOrderDate() {
        return orderDate;
    }

    public String getStatus() {
        return status;
    }

    public LocalDate getNotificationStamp() {
        return notificationStamp;
    }

    @Override
    public String toString() {
        return "OrderRecord{" +
                "orderId=" + orderId +
                ", bookId=" + bookId +
                ", subId=" + subId +
                ", orderDate=" + orderDate +
                ", status='" + status + '\'' +
                ", notificationStamp=" + notificationStamp +
                '}';
    }
}