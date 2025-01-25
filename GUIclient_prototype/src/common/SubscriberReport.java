package common;

import java.io.Serializable;

public class SubscriberReport implements Serializable {
    private static final long serialVersionUID = 1L;

    private String status;
    private int statusCount;
    private int withPenalties;
    private double avgPenalties;
    private int month;
    private int year;
    
    // Constructor, getters, setters
    public SubscriberReport(String status, int statusCount, int withPenalties, double avgPenalties, int month, int year) {
        this.status = status;
        this.statusCount = statusCount;
        this.withPenalties = withPenalties;
        this.avgPenalties = avgPenalties;
        this.month = month;
        this.year = year;
    }

    // Getters
    public String getStatus() { return status; }
    public int getStatusCount() { return statusCount; }
    public int getWithPenalties() { return withPenalties; }
    public double getAvgPenalties() { return avgPenalties; }
    public int getMonth() { return month; }
    public int getYear() { return year; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setStatusCount(int statusCount) { this.statusCount = statusCount; }
    public void setWithPenalties(int withPenalties) { this.withPenalties = withPenalties; }
    public void setAvgPenalties(double avgPenalties) { this.avgPenalties = avgPenalties; }
    public void setMonth(int month) { this.month = month; }
    public void setYear(int year) { this.year = year; }

    // toString
    @Override
    public String toString() {
        return "SubscriberReport{" +
                "status='" + status + '\'' +
                ", statusCount=" + statusCount +
                ", withPenalties=" + withPenalties +
                ", avgPenalties=" + avgPenalties +
                ", month=" + month +
                ", year=" + year +
                '}';
    }
}
