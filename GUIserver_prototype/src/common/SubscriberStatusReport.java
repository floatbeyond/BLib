package common;

import java.io.Serializable;

public class SubscriberStatusReport implements Serializable {
    private static final long serialVersionUID = 1L;
    private String status;
    private int count;

    public SubscriberStatusReport(String status, int count) {
        this.status = status;
        this.count = count;
    }

    public String getStatus() { return status; }
    public int getCount() { return count; }

    @Override
    public String toString() {
        return "SubscriberStatusReport{" +
                "status='" + status + '\'' +
                ", count=" + count +
                '}';
    }
}