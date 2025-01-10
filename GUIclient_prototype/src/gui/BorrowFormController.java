package gui;
    
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
import client.ClientUI;
import common.Book;
import common.Subscriber;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
    
    public class BorrowFormController {
        private Subscriber s;
        private Book b;
    
        @FXML
        private TextField txtID;
    
        @FXML
        private TextField txtCopyId;
        private TextField txtReturnDate;
    
        @FXML
        private Label messageLabel;
        private Label lblReturnDate;
    
        private int subscriberID;
        private int copyID;
    

        
        @FXML
        private void handleBorrowAction(ActionEvent event) throws Exception {
            try {
                // Input validation
                if (txtID.getText().isEmpty() || txtCopyId.getText().isEmpty()) {
                    messageLabel.setText("Please fill in all fields.");
                    return;
                }
        
                try {
                    subscriberID = Integer.valueOf(txtID.getText());
                    copyID = Integer.valueOf(txtCopyId.getText());
                } catch (NumberFormatException e) {
                    messageLabel.setText("Please enter valid numeric values.");
                    return;
                }
        
                ClientUI.cc.accept("connect");
                if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                    // Send subscriberID to the server
                    ClientUI.cc.accept("sendSubscriber" + subscriberID);
        
                    // Assuming the server response is stored in a static variable in ClientUI
                    String serverResponse = ClientUI.cc.getStatus(); //אלון תחזיר לי חזרה מהשרת אם המנוי קפוא או לא. במידה וקפוא תחזיר כן אחרת לא
                    if ("YES".equals(serverResponse)) {
                        messageLabel.setText("Subscriber Frozen");
                    } else if ("NO".equals(serverResponse)) {
                        // Add: keyword to send "Borrow add" + subscriberID + copyID + borrowDate + INSERTED return date (by the librarian)
                        LocalDate borrowDate = LocalDate.now();
                        LocalDate returnDate = borrowDate.plusDays(14);
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                        String formattedBorrowDate = borrowDate.format(formatter);
                        String formattedReturnDate = returnDate.format(formatter);
                    
                        String borrowInfo = "Borrow add " + subscriberID + " " + copyID + " " + formattedBorrowDate + " " + formattedReturnDate;
                        ClientUI.cc.accept(borrowInfo);
                    
                        // Display return date
                        lblReturnDate.setText("Return Date: " + formattedReturnDate);
                    } else {
                        messageLabel.setText("Unexpected server response");
                    }
                } else {
                    displayMessage("No server connection");
                    return;
                }
            } catch (Exception e) {
                displayMessage("An error occurred: " + e.getMessage());
            }
        }



    public void displayMessage(String message) {
        messageLabel.setText(message);
    }



    public void goBackBtn(ActionEvent event) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LibrarianMainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/LibrarianMainFrame.css").toExternalForm());
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
