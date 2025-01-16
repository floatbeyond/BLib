package gui.controllers;

import common.DataLogs;
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

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.stream.Collectors;


import client.ClientUI;

public class SubscriberLogsController {
	
	@FXML private Button btnBack = null;

    @FXML private ListView<String> listViewLogs;
    @FXML private ComboBox<Month> monthComboBox;
    @FXML private Button filterButton;
    @FXML private Button backButton; 

    private List<DataLogs> allDataLogs;

    // Method to populate the ListView with data logs
    public void showDataLogs(List<Object> dataLogs) {
        // this.allDataLogs = dataLogs; // Store all logs
        ObservableList<String> items = FXCollections.observableArrayList();
        for (Object log : dataLogs) {
            items.add(log.toString());
        }
        listViewLogs.setItems(items);
    }
     // Method to filter logs by selected month
    @FXML
    public void filterLogsByMonth() {
        Month selectedMonth = monthComboBox.getValue();
        if (selectedMonth != null) {
            List<Object> filteredLogs = allDataLogs.stream()
                .filter(log -> {
                    LocalDate logDate = log.getTimestamp();
                    return logDate.getMonth() == selectedMonth;
                })
                .collect(Collectors.toList());
            showDataLogs(filteredLogs);
        }
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