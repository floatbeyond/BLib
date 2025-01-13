package gui.controllers;
    
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;

import java.time.LocalDate;

import client.ChatClient;
import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import common.ServerMessage;
import common.Subscriber;
import common.BookCopy;
import common.BorrowingRecord;

public class ReaderCardController {
    private Subscriber s;
    @FXML
    private TextField txtSubscriberID;
    @FXML
    private TextField txtname;
    @FXML
    private TextField txtStatus;
    @FXML
    private TextField txtPhoneNumber;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPenalties;
    @FXML
    private TextField txtFreeze;
    @FXML
    private TextField txtJoinDate;
    @FXML
    private TextField txtExpDate;

    
    private String getSubId() { return txtSubscriberID.getText(); }
    
    @FXML
    private void fetchSubscriberData() {
        String subId = getSubId();        // Assuming you have a method to get subscriber data by ID
        ServerMessage response = MessageUtils.sendMessage(ClientUI.cc, "Librarian", "Subscriber", subId);
        Subscriber sub = null;
        if (response.getType().equals("Subscriber") && response.getData() instanceof Subscriber) {
            sub = (Subscriber) response.getData();
}       
        if (sub != null) {
            txtname.setText(sub.getSub_name());
            txtStatus.setText(sub.getSub_status());
            txtPhoneNumber.setText(sub.getSub_phone_num());
            txtEmail.setText(sub.getSub_email());
            txtPenalties.setText(String.valueOf(sub.getSub_penalties())); 
            txtFreeze.setText(sub.getSub_freeze().toString());
            txtJoinDate.setText(sub.getSub_joined().toString());
            txtExpDate.setText(sub.getSub_expiration().toString());
        } else {
            // Handle case where no data is found
            System.out.println("No data found for Subscriber ID: " + subscriberID);
        }
    }



 
    


    public void goBackBtn(ActionEvent event) throws Exception {
        System.out.println("goBackBtn clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LibrarianMainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		primaryStage.setTitle("Library Tool");
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