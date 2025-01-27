package common;

import java.io.Serializable;

/**
 * Represents an order response in the library system.
 * Implements Serializable for client-server communication.
 */
public class OrderResponse implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;
    
    private String status;
    private Book book;

    /**
     * Constructor for OrderResponse
     * @param status
     * @param book
     */
    public OrderResponse(String status, Book book) {
        this.status = status;
        this.book = book;
    }

    // Getters
    public String getStatus() { return status; }
    public Book getBook() { return book; }
}