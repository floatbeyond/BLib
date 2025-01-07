package common;

import java.io.Serializable;
import java.sql.Date;

public class BorrowingRecord implements Serializable {
    private static final long serialVersionUID = 1L;
    private int borrowId;
    private int copyId; // Foreign key referencing BookCopies
    private int subId; // Foreign key referencing Subscribers
    private Date borrowDate;
    private Date expectedReturnDate;
    private Date actualReturnDate;
    private String status;

    public BorrowingRecord(int borrowId, int copyId, int subId, Date borrowDate, Date expectedReturnDate, Date actualReturnDate, String status) {
        this.borrowId = borrowId;
        this.copyId = copyId;
        this.subId = subId;
        this.borrowDate = borrowDate;
        this.expectedReturnDate = expectedReturnDate;
        this.actualReturnDate = actualReturnDate;
        this.status = status;
    }

    public int getBorrowId() {
        return borrowId;
    }

    public int getCopyId() {
        return copyId;
    }

    public int getSubId() {
        return subId;
    }

    public Date getBorrowDate() {
        return borrowDate;
    }

    public Date getExpectedReturnDate() {
        return expectedReturnDate;
    }

    public Date getActualReturnDate() {
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