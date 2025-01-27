package common;

import java.io.Serializable;

/**
 * Represents a borrowing report for a genre in the library system.
 * This class implements Serializable to support transmission between client and server.
 */
public class BorrowReport implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private String genre;
    private int totalBorrows;
    private double avgBorrowDays;
    private int onTimeReturns;
    private int lateNoPenalty;
    private int lateWithPenaltyOrLost;

    /**
     * Constructor for BorrowReport
     * @param genre
     * @param totalBorrows
     * @param avgBorrowDays
     * @param onTimeReturns
     * @param lateNoPenalty
     * @param lateWithPenaltyOrLost
     */
    public BorrowReport(String genre, int totalBorrows, double avgBorrowDays, 
                       int onTimeReturns, int lateNoPenalty, int lateWithPenaltyOrLost) {
        this.genre = genre;
        this.totalBorrows = totalBorrows;
        this.avgBorrowDays = avgBorrowDays;
        this.onTimeReturns = onTimeReturns;
        this.lateNoPenalty = lateNoPenalty;
        this.lateWithPenaltyOrLost = lateWithPenaltyOrLost;
    }

    // Getters
    public String getGenre() { return genre; }
    public int getTotalBorrows() { return totalBorrows; }
    public double getAvgBorrowDays() { return avgBorrowDays; }
    public int getOnTimeReturns() { return onTimeReturns; }
    public int getLateNoPenalty() { return lateNoPenalty; }
    public int getLateWithPenaltyOrLost() { return lateWithPenaltyOrLost; }

    // Setters
    public void setGenre(String genre) { this.genre = genre; }
    public void setTotalBorrows(int totalBorrows) { this.totalBorrows = totalBorrows; }
    public void setAvgBorrowDays(double avgBorrowDays) { this.avgBorrowDays = avgBorrowDays; }
    public void setOnTimeReturns(int onTimeReturns) { this.onTimeReturns = onTimeReturns; }
    public void setLateNoPenalty(int lateNoPenalty) { this.lateNoPenalty = lateNoPenalty; }
    public void setLateWithPenaltyOrLost(int lateWithPenaltyOrLost) { this.lateWithPenaltyOrLost = lateWithPenaltyOrLost; }

    @Override
    public String toString() {
        return "BorrowReport{" +
                "genre='" + genre + '\'' +
                ", totalBorrows=" + totalBorrows +
                ", avgBorrowDays=" + avgBorrowDays +
                ", onTimeReturns=" + onTimeReturns +
                ", lateNoPenalty=" + lateNoPenalty +
                ", lateWithPenaltyOrLost=" + lateWithPenaltyOrLost +
                '}';
    }

}
