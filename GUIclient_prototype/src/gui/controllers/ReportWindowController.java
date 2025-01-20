package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import java.util.List;
import common.BorrowTimeReport;
import common.SubscriberStatusReport;
import java.util.HashMap;
import java.util.Map;

public class ReportWindowController {

    @FXML private StackedBarChart<String, Number> borrowTimesChart;
    @FXML private BarChart<String, Number> subscriberStatusChart;
    @FXML private BarChart<String, Number> returnedBooksChart;
    @FXML private BarChart<String, Number> UnreturnedBooksChart;

    public void setReports(List<BorrowTimeReport> borrowReports, List<SubscriberStatusReport> subscriberReports) {
        updateSubscriberStatusChart(subscriberReports);
        updateReturnedBooksChart(borrowReports);
        updateUnreturnedBooksChart(borrowReports);
        updateBorrowTimesChart(borrowReports);
    }

    private void updateSubscriberStatusChart(List<SubscriberStatusReport> reportData) {
        subscriberStatusChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) subscriberStatusChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) subscriberStatusChart.getYAxis();

        xAxis.setLabel("Status");
        yAxis.setLabel("Number of Subscribers");

        XYChart.Series<String, Number> activeSeries = new XYChart.Series<>();
        activeSeries.setName("Active");
        XYChart.Series<String, Number> inactiveSeries = new XYChart.Series<>();
        inactiveSeries.setName("In-Active");
        XYChart.Series<String, Number> frozenSeries = new XYChart.Series<>();
        frozenSeries.setName("Frozen");

        Map<String, Integer> statusCounts = new HashMap<>();
        for (SubscriberStatusReport data : reportData) {
            statusCounts.put(data.getStatus(), statusCounts.getOrDefault(data.getStatus(), 0) + data.getCount());
        }

        activeSeries.getData().add(new XYChart.Data<>("Active", statusCounts.getOrDefault("Active", 0)));
        inactiveSeries.getData().add(new XYChart.Data<>("In-Active", statusCounts.getOrDefault("In-Active", 0)));
        frozenSeries.getData().add(new XYChart.Data<>("Frozen", statusCounts.getOrDefault("Frozen", 0)));

        subscriberStatusChart.getData().addAll(activeSeries, inactiveSeries, frozenSeries);
    }

    private void updateReturnedBooksChart(List<BorrowTimeReport> reportData) {
        returnedBooksChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) returnedBooksChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) returnedBooksChart.getYAxis();

        xAxis.setLabel("Book");
        yAxis.setLabel("Average Days Held");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Returned Books");

        for (BorrowTimeReport data : reportData) {
            if (data.getStatus().equals("Returned")) {
                series.getData().add(new XYChart.Data<>(data.getBookTitle(), data.getValue()));
            }
        }

        returnedBooksChart.getData().add(series);
    }

    private void updateUnreturnedBooksChart(List<BorrowTimeReport> reportData) {
        UnreturnedBooksChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) UnreturnedBooksChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) UnreturnedBooksChart.getYAxis();

        xAxis.setLabel("Book");
        yAxis.setLabel("Average Days Late");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Late Books");

        for (BorrowTimeReport data : reportData) {
            if (data.getStatus().equals("Late")) {
                series.getData().add(new XYChart.Data<>(data.getBookTitle(), data.getValue()));
            }
        }

        UnreturnedBooksChart.getData().add(series);
    }

    private void updateBorrowTimesChart(List<BorrowTimeReport> reportData) {
        borrowTimesChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) borrowTimesChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) borrowTimesChart.getYAxis();
    
        xAxis.setLabel("Book");
        yAxis.setLabel("Count");
    
        XYChart.Series<String, Number> borrowedSeries = new XYChart.Series<>();
        borrowedSeries.setName("Borrowed");
        XYChart.Series<String, Number> returnedSeries = new XYChart.Series<>();
        returnedSeries.setName("Returned");
        XYChart.Series<String, Number> lateSeries = new XYChart.Series<>();
        lateSeries.setName("Late");
        XYChart.Series<String, Number> returnedLateSeries = new XYChart.Series<>();
        returnedLateSeries.setName("Returned Late");
    
        Map<String, Integer> borrowedCounts = new HashMap<>();
        Map<String, Integer> returnedCounts = new HashMap<>();
        Map<String, Integer> lateCounts = new HashMap<>();
        Map<String, Integer> returnedLateCounts = new HashMap<>();
    
        for (BorrowTimeReport data : reportData) {
            String bookTitle = data.getBookTitle();
            String status = data.getStatus();
    
            switch (status) {
                case "BorrowedThisMonth":
                    borrowedCounts.put(bookTitle, borrowedCounts.getOrDefault(bookTitle, 0) + 1);
                    break;
                case "Returned":
                    returnedCounts.put(bookTitle, returnedCounts.getOrDefault(bookTitle, 0) + 1);
                    break;
                case "Late":
                    lateCounts.put(bookTitle, lateCounts.getOrDefault(bookTitle, 0) + 1);
                    break;
                case "ReturnedLate":
                    returnedLateCounts.put(bookTitle, returnedLateCounts.getOrDefault(bookTitle, 0) + 1);
                    break;
            }
        }
    
        for (String bookTitle : borrowedCounts.keySet()) {
            borrowedSeries.getData().add(new XYChart.Data<>(bookTitle, borrowedCounts.get(bookTitle)));
        }
        for (String bookTitle : returnedCounts.keySet()) {
            returnedSeries.getData().add(new XYChart.Data<>(bookTitle, returnedCounts.get(bookTitle)));
        }
        for (String bookTitle : lateCounts.keySet()) {
            lateSeries.getData().add(new XYChart.Data<>(bookTitle, lateCounts.get(bookTitle)));
        }
        for (String bookTitle : returnedLateCounts.keySet()) {
            returnedLateSeries.getData().add(new XYChart.Data<>(bookTitle, returnedLateCounts.get(bookTitle)));
        }
    
        borrowTimesChart.getData().addAll(borrowedSeries, returnedSeries, lateSeries, returnedLateSeries);
    }
}