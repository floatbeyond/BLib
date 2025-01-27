package common;

import java.io.Serializable;

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
        this.isOrdered = false;
    }

    // Getters
    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public String getDescription() { return description; }
    public int getNumOfCopies() { return numOfCopies; }
    public boolean isOrdered() { return isOrdered; }

    // Setters
    public void setOrdered(boolean isOrdered) { this.isOrdered = isOrdered; }

    /**
     * Returns a string representation of the Book object.
     * @return string containing all book details
     */
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