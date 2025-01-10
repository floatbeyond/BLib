package gui;

import common.DataLogs;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import java.util.List;


import client.ClientUI;

public class SubscriberLogsContriller {
	
	@FXML private Button btnBack = null;
    @FXML private ListView<String> listViewLogs; // Add this line


    // Method to populate the ListView with data logs
    public void showDataLogs(List<DataLogs> dataLogs) {
        ObservableList<String> items = FXCollections.observableArrayList();
        for (DataLogs log : dataLogs) {
            items.add(log.toString());
        }
        listViewLogs.setItems(items);
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