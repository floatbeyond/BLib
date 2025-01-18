package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.chart.*;
import java.util.List;
import common.BorrowTimeReport;
import common.SubscriberStatusReport;
import java.util.HashMap;
import java.util.Map;

public class ReportWindowController {

    @FXML private BarChart<String, Number> subscriberStatusChart;
    @FXML private BarChart<String, Number> returnedBooksChart;
    @FXML private BarChart<String, Number> notReturnedBooksChart;

    public void setReports(List<BorrowTimeReport> borrowReports, List<SubscriberStatusReport> subscriberReports) {
        updateSubscriberStatusChart(subscriberReports);
        updateReturnedBooksChart(borrowReports);
        updateNotReturnedBooksChart(borrowReports);
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

    private void updateNotReturnedBooksChart(List<BorrowTimeReport> reportData) {
        notReturnedBooksChart.getData().clear();
        CategoryAxis xAxis = (CategoryAxis) notReturnedBooksChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) notReturnedBooksChart.getYAxis();

        xAxis.setLabel("Book");
        yAxis.setLabel("Average Days Late");

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Late Books");

        for (BorrowTimeReport data : reportData) {
            if (data.getStatus().equals("Late")) {
                series.getData().add(new XYChart.Data<>(data.getBookTitle(), data.getValue()));
            }
        }

        notReturnedBooksChart.getData().add(series);
    }
}