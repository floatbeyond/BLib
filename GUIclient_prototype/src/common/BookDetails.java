package common;

public class BookDetails {
    private String bookName;
    private String author;
    private String genre;
    private String description;
    private String location;
    private String availability;

    public BookDetails(String bookName, String author, String genre, String description, String location, String availability) {
        this.bookName = bookName;
        this.author = author;
        this.genre = genre;
        this.description = description;
        this.location = location;
        this.availability = availability;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}