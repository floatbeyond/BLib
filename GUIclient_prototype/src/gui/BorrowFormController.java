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
                ClientUI.cc.accept("connect");
                if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                    subscriberID = Integer.valueOf(txtID.getText());
                    copyID = Integer.valueOf(txtCopyId.getText());

                    // Add: תקינות קלט לפני ששולחים לסרבר גם לספר וגם למנוי
        
                    // Send subscriberID to the server
                    ClientUI.cc.accept("sendSubscriber" + subscriberID);
        
                    // Assuming the server response is stored in a static variable in ClientUI
                    String serverResponse = ClientUI.cc.getStatus(); //אלון תחזיר לי חזרה מהשרת אם המנוי קפוא או לא. במידה וקפוא תחזיר כן אחרת לא
                    ALON COMMENT: אני מחזיר לכם את הסטודנט עם כל המידע, אתם יכולים לעשות 
                    ALON COMMENT: s.getSub_status() ולראות אם הוא קפוא או לא
                    if ("YES".equals(serverResponse)) {
                        messageLabel.setText("Subscriber Frozen");
                    } else if ("NO".equals(serverResponse)) {
                        // Add: אם הוא לא קפוא לשלוח לסרבר את הספר
                        // Add: keyword to send "Borrow add" + subscriberID + copyID + borrowDate + INSERTED return date (by the librarian)
                        
                        messageLabel.setText("Borrow successfully");
                         // Calculate return date
                    LocalDate returnDate = LocalDate.now().plusDays(14);
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    txtReturnDate.setText("Return Date: " + returnDate.format(formatter));

                    } else {
                        messageLabel.setText("Unexpected server response");
                    }
                } else {
                    displayMessage("No server connection");
                    return;
                }
            } catch (NumberFormatException e) {
                displayMessage("Please check your input values!");
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
