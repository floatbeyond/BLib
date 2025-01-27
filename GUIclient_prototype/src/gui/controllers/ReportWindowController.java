package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.TabPane;
import common.BorrowReport;
import common.SubscriberReport;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class for the ReportWindow.fxml file.
 * This class is responsible for updating the charts in the report window.
 */
public class ReportWindowController {
    @FXML private TabPane reportTabPane;
    
    // Borrow Charts
    @FXML private BarChart<String, Number> genreBorrowChart;
    @FXML private BarChart<String, Number> lateReturnChart;
    @FXML private CategoryAxis borrowGenreAxis;
    @FXML private CategoryAxis returnGenreAxis;
    
    // Subscriber Charts
    @FXML private PieChart subscriberStatusChart;
    
    /**
     * Initializes the controller.
     * This method is called automatically and initializes the charts.
     */
    @FXML
    public void initialize() {
        setupCharts();
    }
    
    /**
     * Sets the reports for the charts.
     * @param borrowReports - the borrow reports
     * @param subscriberReports - the subscriber reports
     */
    private void setupCharts() {
        genreBorrowChart.setTitle("Monthly Borrows by Genre");
        lateReturnChart.setTitle("Late Returns and Lost Books");
        subscriberStatusChart.setTitle("Subscriber Status Distribution");
        setupAlternatingAxisLabels(borrowGenreAxis);
        setupAlternatingAxisLabels(returnGenreAxis);
    }

    /**
     * Sets the reports for the charts.
     * @param borrowReports - the borrow reports
     * @param subscriberReports - the subscriber reports
     */
    public void setReports(List<BorrowReport> borrowReports, List<SubscriberReport> subscriberReports) {
        updateCharts(borrowReports, subscriberReports);
    }

    /**
     * Sets up alternating axis labels for the given axis.
     * @param axis - the axis to set up
     */
    private void setupAlternatingAxisLabels(CategoryAxis axis) {
        axis.setTickLabelRotation(45);  // Set 45-degree rotation
        axis.setTickLabelGap(10);       // Adjust gap between labels
        
        axis.getCategories().addListener((javafx.collections.ListChangeListener.Change<? extends String> c) -> {
            List<Node> labels = new ArrayList<>(axis.lookupAll(".axis-label"));
            labels.sort((n1, n2) -> {
                String text1 = ((Label)n1).getText();
                String text2 = ((Label)n2).getText();
                return text1.compareTo(text2);
            });
        });
    }
    
        /**
     * Updates the charts with the given reports.
     * @param borrowReport - the borrow reports
     * @param subscriberReport - the subscriber reports
     */
    public void updateCharts(List<BorrowReport> borrowReport, 
                           List<SubscriberReport> subscriberReport) {
        updateBorrowCharts(borrowReport);
        updateSubscriberCharts(subscriberReport);
    }

    /**
     * Updates the borrow charts with the given reports.
     * @param reports
     */
    private void updateBorrowCharts(List<BorrowReport> reports) {
        genreBorrowChart.getData().clear();
        lateReturnChart.getData().clear();
        
        // Sort reports by genre
        List<BorrowReport> sortedReports = new ArrayList<>(reports);
        sortedReports.sort((r1, r2) -> r1.getGenre().compareTo(r2.getGenre()));
        
        // Create series
        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> avgBorrowDaysSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> onTimeSeries = new XYChart.Series<>();
        XYChart.Series<String, Number> lateNoPenaltySeries = new XYChart.Series<>();
        XYChart.Series<String, Number> lateWithPenaltySeries = new XYChart.Series<>();
        
        totalSeries.setName("Total Borrows");
        avgBorrowDaysSeries.setName("Avg Borrow Days");
        onTimeSeries.setName("On Time Returns");
        lateNoPenaltySeries.setName("Late (No Penalty)");
        lateWithPenaltySeries.setName("Late with Penalty/Lost");
        
        // Add sorted data
        for (BorrowReport report : sortedReports) {
            totalSeries.getData().add(new XYChart.Data<>(report.getGenre(), report.getTotalBorrows()));
            avgBorrowDaysSeries.getData().add(new XYChart.Data<>(report.getGenre(), report.getAvgBorrowDays()));
            onTimeSeries.getData().add(new XYChart.Data<>(report.getGenre(), report.getOnTimeReturns()));
            lateNoPenaltySeries.getData().add(new XYChart.Data<>(report.getGenre(), report.getLateNoPenalty()));
            lateWithPenaltySeries.getData().add(new XYChart.Data<>(report.getGenre(), report.getLateWithPenaltyOrLost()));
        }
        
        genreBorrowChart.getData().addAll(List.of(totalSeries, avgBorrowDaysSeries));
        lateReturnChart.getData().addAll(List.of(onTimeSeries, lateNoPenaltySeries, lateWithPenaltySeries));
    }
    
    /**
     * Updates the subscriber charts with the given reports.
     * @param reports
     */
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