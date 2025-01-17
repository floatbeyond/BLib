package common;

import java.io.Serializable;

public class OrderResponse implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private Book book;

    public OrderResponse(String status, Book book) {
        this.status = status;
        this.book = book;
    }

    public String getStatus() { return status; }
    public Book getBook() { return book; }
}