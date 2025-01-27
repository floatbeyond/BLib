package common; 

import java.io.Serializable;

/**
 * Represents a librarian in the library system.
 * Implements Serializable for client-server communication.
 */
public class Librarian implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private int librarian_id;
    private String librarian_name;

    /**
     * Constructor for Librarian
     * @param librarian_id
     * @param librarian_name
     */
    public Librarian(int librarian_id, String librarian_name) {       
        this.librarian_id = librarian_id;
        this.librarian_name = librarian_name;
    }

    public int getLibrarian_id() { return librarian_id; }
    public String getLibrarian_name() { return librarian_name; }

    @Override
    public String toString() {
        return "Librarian{" +
                "lib_id=" + librarian_id +
                ", sub_name='" + librarian_name +
                '}';
    }
}