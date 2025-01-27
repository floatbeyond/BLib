package gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.StringConverter;
import java.time.Month;
import java.time.Year;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import client.ClientUI;
import common.BorrowReport;
import common.MessageUtils;
import common.SubscriberReport;

/**
 * This class is the controller for the DataReports.fxml file. It is responsible for handling the user input and
 * displaying the reports for a specific month and year. It is also responsible for displaying the main window of the application.
 */
public class DataReportsController {

    @FXML private ComboBox<Month> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private Button showButton;
    @FXML private Button backButton;

    private Map<String, List<BorrowReport>> allBorrowReports = new HashMap<>();
    private Map<String, List<SubscriberReport>> allSubscriberReports = new HashMap<>();

    /**
     * This method is called when the DataReports.fxml file is loaded.
     * It is responsible for initializing the ComboBoxes with months and years.
     * It is also responsible for setting the cell value factories for the columns.
     * It is also responsible for setting the reports for the BorrowReports and SubscriberReports.
     * It is also responsible for displaying the reports for a specific month and year.
     */
    @FXML
    public void initialize() {
        // Populate the ComboBox with months
        monthComboBox.setConverter(new StringConverter<Month>() {
            @Override
            public String toString(Month month) {
                return month != null ? month.toString().substring(0, 1) + month.toString().substring(1).toLowerCase() : "";
            }

            @Override
            public Month fromString(String string) {
                return string != null && !string.isEmpty() ? Month.valueOf(string.toUpperCase()) : null;
            }
        });
        monthComboBox.setItems(FXCollections.observableArrayList(Month.values()));

        // Populate the ComboBox with years
        int currentYear = Year.now().getValue();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int year = 2024; year <= currentYear; year++) {
            years.add(year);
        }
        yearComboBox.setItems(years);
    }

    /**
     * This method is called when the user clicks the Show button.
     * It is responsible for displaying the reports for a specific month and year.
     * @param event
     */
    @FXML
    public void showReports(ActionEvent event) {
        Month selectedMonth = monthComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();
        if (selectedMonth != null && selectedYear != null) {
            String monthYear = String.format("%d-%02d", selectedYear, selectedMonth.getValue());
            List<BorrowReport> filteredBorrowTimesReports = allBorrowReports.getOrDefault(monthYear, List.of());
            List<SubscriberReport> filteredSubscriberStatusReports = allSubscriberReports.getOrDefault(monthYear, List.of());
            // print size of reports and all reports too
            System.out.println("Borrow Reports: " + filteredBorrowTimesReports.size());
            System.out.println("Subscriber Reports: " + filteredSubscriberStatusReports.size());
            openReportWindow(monthYear, filteredBorrowTimesReports, filteredSubscriberStatusReports);
        }
    }

    /**
     * This method is called when the user clicks the Show button
     * It is responsible for displaying the reports for a specific month and year.
     * @param monthYear
     * @param borrowReports
     * @param subscriberReports
     */
    private void openReportWindow(String monthYear, List<BorrowReport> borrowReports, List<SubscriberReport> subscriberReports) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/ReportWindow.fxml"));
            Parent root = fxmlLoader.load();
            ReportWindowController reportWindowController = fxmlLoader.getController();
            reportWindowController.setReports(borrowReports, subscriberReports);

            Stage stage = new Stage();
            stage.setTitle("Reports for " + monthYear);
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * It is responsible for setting the reports for the BorrowReports.
     * @param reports
     */
    public void setAllBorrowReports(Map<String, List<BorrowReport>> reports) {
        System.out.println("Setting all borrow times reports: " + reports);
        this.allBorrowReports = reports;
    }

    /**
     * It is responsible for setting the reports for the SubscriberReports.
     * @param reports
     */
    public void setAllSubscriberReports(Map<String, List<SubscriberReport>> reports) {
        System.out.println("Setting all subscriber status reports: " + reports);
        this.allSubscriberReports = reports;
    }

    /**
     * This method is called when the user clicks the Back button.
     * It is responsible for displaying the main window of the application.
     * @param event
     * @throws Exception
     */
    public void goBackBtn(ActionEvent event) throws Exception {

        // Load the LibrarianMainFrame
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LibrarianMainFrame.fxml"));
        Pane root = loader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setTitle("Library Tool");
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    MessageUtils.sendMessage(ClientUI.cc, "user", "disconnect", null);
                    ClientUI.chat.quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ((Node) event.getSource()).getScene().getWindow().hide();
        stage.setResizable(false);
        stage.show();
    }
}