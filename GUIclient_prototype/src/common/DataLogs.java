package common;

import java.io.Serializable;
import java.time.LocalDate;

public class DataLogs implements Serializable {
    private static final long serialVersionUID = 1L;
    private int log_id;
    private int sub_id;
    private String log_action; 
    private LocalDate timestamp;

    public DataLogs(int log_id, int sub_id, String log_action, LocalDate timestamp) {
        this.log_id = log_id;
        this.sub_id = sub_id;
        this.log_action = log_action;
        this.timestamp = timestamp;
    }

    public int getLog_id() {
        return log_id;
    }

    public int getSub_id() {
        return sub_id;
    }

    public String getLog_action() {
        return log_action;
    }   

    public LocalDate getTimestamp() {
        return timestamp;
    }

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