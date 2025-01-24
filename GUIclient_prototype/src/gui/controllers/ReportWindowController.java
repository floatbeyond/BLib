package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import javafx.scene.control.TabPane;
import common.BorrowReport;
import common.SubscriberReport;
import java.util.List;

public class ReportWindowController {
    @FXML private TabPane reportTabPane;
    
    // Borrow Charts
    @FXML private BarChart<String, Number> genreBorrowChart;
    @FXML private BarChart<String, Number> lateReturnChart;
    
    // Subscriber Charts
    @FXML private PieChart subscriberStatusChart;
    // @FXML private BarChart<String, Number> penaltyChart;
    
    @FXML
    public void initialize() {
        setupCharts();
    }
    
    private void setupCharts() {
        genreBorrowChart.setTitle("Monthly Borrows by Genre");
        lateReturnChart.setTitle("Late Returns and Lost Books");
        subscriberStatusChart.setTitle("Subscriber Status Distribution");
        // penaltyChart.setTitle("Penalties and Freezes by Status");
    }

    public void setReports(List<BorrowReport> borrowReports, List<SubscriberReport> subscriberReports) {
        updateCharts(borrowReports, subscriberReports);
    }
    
    
    public void updateCharts(List<BorrowReport> borrowReport, 
                           List<SubscriberReport> subscriberReport) {
        updateBorrowCharts(borrowReport);
        updateSubscriberCharts(subscriberReport);
    }

    private void updateBorrowCharts(List<BorrowReport> reports) {
        genreBorrowChart.getData().clear();
        lateReturnChart.getData().clear();
        
        // Total borrows by genre
        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        totalSeries.setName("Total Borrows");
        
        // Return status by genre
        XYChart.Series<String, Number> onTimeSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> lateNoPenaltySeries = new XYChart.Series<>();
        XYChart.Series<String, Number> lateWithPenaltySeries = new XYChart.Series<>();
        
        onTimeSeries.setName("On Time Returns");
        lateNoPenaltySeries.setName("Late (No Penalty)");
        lateWithPenaltySeries.setName("Late with Penalty/Lost");
        
        for (BorrowReport report : reports) {
            // Add total borrows data
            totalSeries.getData().add(
                new XYChart.Data<>(report.getGenre(), report.getTotalBorrows())
            );
            
            // Add return status data
            onTimeSeries.getData().add(
                new XYChart.Data<>(report.getGenre(), report.getOnTimeReturns())
            );
            lateNoPenaltySeries.getData().add(
                new XYChart.Data<>(report.getGenre(), report.getLateNoPenalty())
            );
            lateWithPenaltySeries.getData().add(
                new XYChart.Data<>(report.getGenre(), report.getLateWithPenaltyOrLost())
            );
        }
        
        genreBorrowChart.getData().add(totalSeries);
        lateReturnChart.getData().addAll(
            onTimeSeries, 
            lateNoPenaltySeries, 
            lateWithPenaltySeries
        );
    }
    
    private void updateSubscriberCharts(List<SubscriberReport> reports) {
        subscriberStatusChart.getData().clear();
    
        // Calculate total for percentages
        int total = reports.stream()
                .mapToInt(SubscriberReport::getStatusCount)
                .sum();
                
        for (SubscriberReport report : reports) {
            double percentage = (report.getStatusCount() * 100.0) / total;
            String label = String.format("%s: %d (%.1f%%)", 
                report.getStatus(), 
                report.getStatusCount(),
                percentage);
                
            PieChart.Data slice = new PieChart.Data(label, report.getStatusCount());
            subscriberStatusChart.getData().add(slice);
        }
        
        // Add hover effect for exact numbers
        for (PieChart.Data data : subscriberStatusChart.getData()) {
            data.getNode().setOnMouseEntered(e -> {
                data.getNode().setStyle("-fx-pie-color: derive(" + data.getNode().getStyle() + ", 20%);");
            });
            data.getNode().setOnMouseExited(e -> {
                data.getNode().setStyle("");
            });
        }
    }
}