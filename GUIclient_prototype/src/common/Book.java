package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Comparator;

/**
 * Represents a book in the library system.
 * This class implements Serializable to support transmission between client and server.
 */
public class Book implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private int bookId;
    private String title;
    private String author;
    private String genre;
    private String description;
    private int numOfCopies;
    private List<BookCopy> copies; // list of book copies
    private boolean isOrdered;

    /**
     * Constructs a new Book with the specified details.
     *
     * @param bookId unique identifier for the book
     * @param title title of the book
     * @param author author of the book
     * @param genre genre/category of the book
     * @param description brief description of the book
     * @param numOfCopies number of copies available
     */
    public Book(int bookId, String title, String author, String genre, String description, int numOfCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.numOfCopies = numOfCopies;
        this.copies = new ArrayList<>();
        this.isOrdered = false; // Initialize as false
    }

    // Getters
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }
    public int getNumOfCopies() { return numOfCopies; }
    public boolean isOrdered() { return isOrdered; } // New getter

    // Setters
    public void setOrdered(boolean isOrdered) { this.isOrdered = isOrdered; } // New setter

    /**
     * Returns a string representation of the Book object.
     * @return List of book copies containing all book details
     */
    public List<BookCopy> getAllCopies() { return copies; }
    
    /**
     * Returns a list of available book copies.
     * @return List of available book copies
     */
    public List<BookCopy> getAvailableCopies() {
        return copies.stream()
            .filter(copy -> copy.getStatus().equals("Available"))
            .collect(Collectors.toList());
    }

    // Setter for copies
    public void setCopies(List<BookCopy> copies) { this.copies = copies; }
    
    /**
     * Returns a string representation of the Book object.
     * @return string containing all book details
     */
    public String getCopyCount() {
        long available = getAvailableCopies().size();
        if (available == 0) {
            BookCopy earliestCopy = copies.stream()
                .filter(copy -> copy.getBorrowingRecord() != null)
                .min(Comparator.comparing(copy -> copy.getBorrowingRecord().getExpectedReturnDate()))
                .orElse(null);
            
            if (earliestCopy != null) {
                return "None available until " + earliestCopy.getBorrowingRecord().getExpectedReturnDate();
            }
        } else {
            return available + " available out of " + numOfCopies;
        }
        return "No copies available";
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", genre='" + genre + '\'' +
                ", description='" + description + '\'' +
                ", numOfCopies=" + numOfCopies +
                ", isOrdered=" + isOrdered + // Include new property in toString
                '}';
    }
}