package common;

import java.io.Serializable;


public class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private int bookId;
    private String title;
    private String author;
    private String genre;
    private String description;
    private int numOfCopies;
    private boolean isOrdered; // New property

    public Book(int bookId, String title, String author, String genre, String description, int numOfCopies) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.numOfCopies = numOfCopies;
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