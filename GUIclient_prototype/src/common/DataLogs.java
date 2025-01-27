package common;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Represents a data log in the library system.
 * Implements Serializable for client-server communication.
 */
public class DataLogs implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private int log_id;
    private int sub_id; // Foreign key referencing Subscribers
    private String log_action; 
    private Timestamp timestamp;

    /**
     * Constructor for DataLogs
     * @param log_id
     * @param sub_id
     * @param log_action
     * @param timestamp
     */
    public DataLogs(int log_id, int sub_id, String log_action, Timestamp timestamp) {
        this.log_id = log_id;
        this.sub_id = sub_id;
        this.log_action = log_action;
        this.timestamp = timestamp;
    }

    public int getLog_id() { return log_id; }
    public int getSub_id() { return sub_id; }
    public String getLog_action() { return log_action; }   
    public Timestamp getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "DataLogs {"+
        "log_id=" + log_id +
         ", sub_id=" + sub_id + 
         ", log_action=" + log_action + 
         ", timestamp="+ timestamp +
          "}";
    }
}