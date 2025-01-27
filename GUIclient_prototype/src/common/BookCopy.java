package common;

import java.io.Serializable;

/**
 * Represents a physical copy of a book in the library system.
 * Implements Serializable for client-server communication.
 */
public class BookCopy implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private int copyId;
    private int bookId; // Foreign key referencing Book
    private String location;
    private String status;
    private BorrowingRecord borrowingRecord; // Add BorrowingRecord

    /**
     * Constructor for BookCopy
     * @param copyId
     * @param bookId
     * @param location
     * @param status
     */
    public BookCopy(int copyId, int bookId, String location, String status) {
        this.copyId = copyId;
        this.bookId = bookId;
        this.location = location;
        this.status = status;
    }

    // Getters and setters
    public int getCopyId() { return copyId; }
    public int getBookId() { return bookId; }
    public String getLocation() { return location; }
    public String getStatus() { return status; }
    public BorrowingRecord getBorrowingRecord() { return borrowingRecord; }

    public void setBorrowingRecord(BorrowingRecord borrowingRecord) { this.borrowingRecord = borrowingRecord; }

    @Override
    public String toString() {
        return "BookCopy{" +
                "copyId=" + copyId +
                ", bookId=" + bookId +
                ", location='" + location + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}