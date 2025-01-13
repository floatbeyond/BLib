package common;

import java.io.Serializable;
import java.time.LocalDate;

public class BorrowingRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private int borrowId;
    private int copyId; // Foreign key referencing BookCopies
    private int subId; // Foreign key referencing Subscribers
    private LocalDate borrowDate;
    private LocalDate expectedReturnDate;
    private LocalDate actualReturnDate;
    private String status;

    public BorrowingRecord(int borrowId, int copyId, int subId, LocalDate borrowDate, LocalDate expectedReturnDate, LocalDate actualReturnDate, String status) {
        this.borrowId = borrowId;
        this.copyId = copyId;
        this.subId = subId;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
    }

    public int getCopyId() {
        return copyId;
    }

    public int getSubId() {
        return subId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "BorrowingRecord{" +
                "borrowId=" + borrowId +
                ", copyId=" + copyId +
                ", subId=" + subId +
                ", borrowDate=" + borrowDate +
                ", expectedReturnDate=" + expectedReturnDate +
                ", actualReturnDate=" + actualReturnDate +
                ", status='" + status + '\'' +
                '}';
    }
}