package gui.controllers;

import common.DataLogs;
import common.DateUtils;
import common.MessageUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.time.format.DateTimeFormatter;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import java.util.stream.Collectors;
import javafx.scene.control.Label;
import javafx.util.StringConverter;
import java.sql.Timestamp;



import client.ClientUI;

public class SubscriberLogsController {
	
	@FXML private Button btnBack = null;

    @FXML private ListView<String> listViewLogs;
    @FXML private ComboBox<Month> monthComboBox;
    @FXML private ComboBox<Integer> yearComboBox;
    @FXML private Button backButton; 
    @FXML private Label currentDate;

    private List<DataLogs> allDataLogs;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @FXML
    private void initialize() {
        // Populate the ComboBox with months
        ObservableList<Month> months = FXCollections.observableArrayList(Month.values());
        // Set custom StringConverter to display month names in a user-friendly format
        monthComboBox.setConverter(new StringConverter<Month>() {
            @Override
            public String toString(Month month) {
                // Capitalize the first letter and make the rest lowercase
                return month.toString().substring(0, 1) + month.toString().substring(1).toLowerCase();
            }

            @Override
            public Month fromString(String string) {
                // Convert the string back to a Month enum value
                return Month.valueOf(string.toUpperCase());
            }
        });

        monthComboBox.setItems(months);

        // Populate the ComboBox with years (for example, from 2000 to the current year)
        int currentYear = Year.now().getValue();
        ObservableList<Integer> years = FXCollections.observableArrayList();
        for (int year = 2023; year <= currentYear; year++) {
            years.add(year);
        }
        yearComboBox.setItems(years);

        // Set current date label
        currentDate.setText("Current date: " + LocalDate.now().format(DATE_FORMATTER));
    }
    // Method to set all data logs
    public void setAllDataLogs(List<Object> logs) {
        this.allDataLogs = logs.stream()
            .map(log -> (DataLogs) log)
            .collect(Collectors.toList());
    }
    // Method to filter logs by selected month
    @FXML
    public void filterLogsByMonth() {
        Month selectedMonth = monthComboBox.getValue();
        Integer selectedYear = yearComboBox.getValue();
        if (selectedMonth != null && selectedYear != null) {
            List<Object> filteredLogs = allDataLogs.stream()
                .filter(log -> {
                    Timestamp logDate = log.getTimestamp();
                    return logDate.toLocalDateTime().getMonth() == selectedMonth && logDate.toLocalDateTime().getYear() == selectedYear;
                })
                .collect(Collectors.toList());
            showDataLogs(filteredLogs);
        }
    }

    public void showDataLogs(List<Object> logs) {
        ObservableList<String> items = FXCollections.observableArrayList();
        int logNumber = 1;
        for (Object log : logs) {
            items.add(formatLog(log, logNumber));
            logNumber++;
        }
        listViewLogs.setItems(items);
    }

    private String formatLog(Object log, int logNumber) {
        DataLogs dataLog = (DataLogs) log;

        // Reformat the timestamp to a user-friendly format
        String formattedDate = DateUtils.formatTimestamp(dataLog.getTimestamp(), "dd-MM-yyyy HH:mm:ss");

        return logNumber + ". " + dataLog.getLog_action() + ", " + formattedDate;
    }

     public void goBackBtn(ActionEvent event) throws Exception {
        System.out.println("goBackBtn clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/SubscriberMainFrame.fxml"));
		Pane root = loader.load();
		
        Stage primaryStage = new Stage();
        Scene scene = new Scene(root);			
        primaryStage.setTitle("Subscriber Main Frame");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    MessageUtils.sendMessage(ClientUI.cc, "user",  "disconnect" , null);
                    ClientUI.chat.quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        ((Node)event.getSource()).getScene().getWindow().hide();
        primaryStage.setResizable(false);
        primaryStage.show();
    }
}