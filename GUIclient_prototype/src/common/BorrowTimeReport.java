package common;

import java.io.Serializable;

public class BorrowTimeReport implements Serializable {
    private static final long serialVersionUID = 1L;
    private String bookTitle;
    private String status;
    private double value; // Can be average days held, average days late, or borrow count

    public BorrowTimeReport(String bookTitle, String status, double value) {
        this.bookTitle = bookTitle;
        this.status = status;
        this.value = value;
    }

    public String getBookTitle() { return bookTitle; }
    public String getStatus() { return status; }
    public double getValue() { return value; }

    @Override
    public String toString() {
        return "BorrowTimeReport{" +
                "bookTitle='" + bookTitle + '\'' +
                ", status='" + status + '\'' +
                ", value=" + value +
                '}';
    }
}