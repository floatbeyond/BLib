package common; 

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Represents a subscriber in the library system.
 * Implements Serializable for client-server communication.
 */
public class Subscriber implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;
    
    private int sub_id;
    private String sub_name;
    private String sub_status;
    private String sub_phone_num;
    private String sub_email;
    private int sub_penalties;
    private LocalDate sub_freeze;
    private LocalDate sub_joined;
    private LocalDate sub_expiration;
    private int currentlyBorrowed;
    private int currentlyOrdered;

    /**
     * Constructor for Subscriber
     * @param sub_id
     * @param sub_name
     * @param sub_status
     * @param sub_phone_num
     * @param sub_email
     * @param sub_penalties
     * @param sub_freeze
     * @param sub_joined
     * @param sub_expiration
     * @param currentlyBorrowed
     * @param currentlyOrdered
     */
    public Subscriber(int sub_id, String sub_name, String sub_status, String sub_phone_num, String sub_email, 
            int sub_penalties, LocalDate sub_freeze, LocalDate sub_joined, LocalDate sub_expiration, int currentlyBorrowed, int currentlyOrdered) {       
        this.sub_id = sub_id;
        this.sub_name = sub_name;
        this.sub_status = sub_status;
        this.sub_phone_num = sub_phone_num;
        this.sub_email = sub_email;
        this.sub_penalties = sub_penalties;
        this.sub_freeze = sub_freeze;
        this.sub_joined = sub_joined;
        this.sub_expiration = sub_expiration;
        this.currentlyBorrowed = currentlyBorrowed;
        this.currentlyOrdered = currentlyOrdered;
    }

    // Getters
    public int getSub_id() { return sub_id; }
    public String getSub_name() { return sub_name; }
    public String getSub_status() { return sub_status; }
    public String getSub_phone_num() { return sub_phone_num; }
    public String getSub_email() { return sub_email; }
    public int getSub_penalties() { return sub_penalties; }
    public LocalDate getSub_freeze() { return sub_freeze; }
    public LocalDate getSub_joined() { return sub_joined; }
    public LocalDate getSub_expiration() { return sub_expiration; }
    public int getCurrentlyBorrowed() { return currentlyBorrowed; }
    public int getCurrentlyOrdered() { return currentlyOrdered; }

    @Override
    public String toString() {
        return "Subscriber{" +
                "sub_id=" + sub_id +
                ", sub_name='" + sub_name + '\'' +
                ", sub_status='" + sub_status + '\'' +
                ", sub_phone_num='" + sub_phone_num + '\'' +
                ", sub_email='" + sub_email + '\'' +
                ", sub_penalties=" + sub_penalties + '\'' +
                ", sub_freeze=" + sub_freeze + '\'' +
                ", sub_joined=" + sub_joined +  '\'' +
                ", sub_expiration=" + sub_expiration + '\'' +
                ", currentlyBorrowed=" + currentlyBorrowed + '\'' +
                ", currentlyOrdered=" + currentlyOrdered +
                '}';
    }
}