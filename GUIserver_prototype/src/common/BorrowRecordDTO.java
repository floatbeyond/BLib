package common;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a borrowing record in the library system.
 * Implements Serializable for client-server communication.
 */
public class BorrowRecordDTO implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private String bookTitle;
    private int borrowId;
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private String status;

    /**
     * Constructor for BorrowRecordDTO
     * @param bookTitle
     * @param borrowId
     * @param borrowDate
     * @param expectedReturnDate
     * @param status
     */
    public BorrowRecordDTO(String bookTitle, int borrowId, LocalDate borrowDate, LocalDate expectedReturnDate, String status) {
        this.bookTitle = bookTitle;
        this.borrowId = borrowId;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.status = status;
    }

    // Getters and setters for all fields
    public String getBookTitle() { return bookTitle; }
    public void setBookTitle(String bookTitle) { this.bookTitle = bookTitle; }

    public int getBorrowId() { return borrowId; }
    public void setBorrowId(int borrowId) { this.borrowId = borrowId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getExpectedReturnDate() { return expectedReturnDate; }
    public void setExpectedReturnDate(LocalDate expectedReturnDate) { this.expectedReturnDate = expectedReturnDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}