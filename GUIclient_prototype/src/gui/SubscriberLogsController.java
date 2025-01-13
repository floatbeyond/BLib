package gui;

import common.DataLogs;
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

import java.util.ArrayList;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;

import java.util.List;
import java.util.stream.Collectors;


import client.ClientUI;

public class SubscriberLogsController {
	
	@FXML private Button btnBack = null;

    @FXML private ListView<String> listViewLogs;
    @FXML private ComboBox<Month> monthComboBox;
    @FXML private Button filterButton;

    private List<DataLogs> allDataLogs;

    // Method to populate the ListView with data logs
    public void showDataLogs(List<DataLogs> dataLogs) {
        this.allDataLogs = dataLogs; // Store all logs
        ObservableList<String> items = FXCollections.observableArrayList();
        for (DataLogs log : dataLogs) {
            items.add(log.toString());
        }
        listViewLogs.setItems(items);
    }
     // Method to filter logs by selected month
    @FXML
    public void filterLogsByMonth() {
        Month selectedMonth = monthComboBox.getValue();
        if (selectedMonth != null) {
            List<DataLogs> filteredLogs = allDataLogs.stream()
                .filter(log -> {
                    LocalDate logDate = log.getTimestamp().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    return logDate.getMonth() == selectedMonth;
                })
                .collect(Collectors.toList());
            showDataLogs(filteredLogs);
        }
    }

     public void goBackBtn(ActionEvent event) throws Exception {
        System.out.println("goBackBtn clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/MainFrame.css").toExternalForm());
		primaryStage.setTitle("Library Tool");
		primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    ClientUI.cc.accept("disconnect");
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