package gui.controllers;
import java.time.LocalDate;

import client.ClientUI;
import client.SharedController;
import common.BookCopy;
import common.MessageUtils;
import common.Subscriber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ReturnBookController implements ItemLoader {
    private BookCopy bc;

    @FXML private Button btnback;
    @FXML private Button btnscanbook;
    @FXML private TextField txtID;
    @FXML private TextField txtCopyId;
    @FXML private DatePicker actualReturnDate;
    
    @FXML private Label messageLabel;
    private static Stage scanWindowStage; // Track the ScanWindow stage

    @FXML
    public void initialize() {
        txtID.setEditable(false);
        txtCopyId.setEditable(false);
        actualReturnDate.setValue(LocalDate.now());
        actualReturnDate.setEditable(false);
        actualReturnDate.setDisable(true);
    }

    private String getSubId() { return txtID.getText(); }
    private void setID(String sub_id) { txtID.setText(sub_id); }
    private String getCopyId() { return txtCopyId.getText(); }
    private void setCopyID(String copy_id) { txtCopyId.setText(copy_id); }
    private LocalDate getActualReturnDate() { return actualReturnDate.getValue(); }

    @FXML
    private void showScanWindow(ActionEvent event) throws Exception {
        if (scanWindowStage != null && scanWindowStage.isShowing()) {
            scanWindowStage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ScanWindow.fxml"));
            Pane root = loader.load();

            ScanWindowController scanWindowController = loader.getController();
            scanWindowController.setCommand("scanBookCopy");
            scanWindowController.setItemLoader(this);

            scanWindowStage = new Stage();
            Scene scene = new Scene(root);
            scanWindowStage.setTitle("Scan Window");
            scanWindowStage.setScene(scene);
            scanWindowStage.setResizable(false);
            scanWindowStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //handle when librarian clicks submit return date button with actual return date
      public void handleReturnDateSubmit(ActionEvent event) { 
        try {
            LocalDate returnDate = getActualReturnDate();
            String subId = getSubId();
            String copyId = getCopyId();
            // Input validation
            if (subId.isEmpty() || copyId.isEmpty()) {
                displayMessage("Please scan first");
                return;
            }

            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (bc.getStatus().equals("Returned")) {
                    displayMessage("Book has been returned already");
                } else if (bc.getStatus().equals("Lost")) {
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "returnLostBook", subId + ":" + copyId + ":" + returnDate);
                } else {
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "returnBook", subId + ":" + copyId + ":" + returnDate);
                }
            } else {
                displayMessage("No server connection");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadBookCopy(BookCopy bookCopy) {
        setCopyID(String.valueOf(bookCopy.getCopyId()));
        bc = bookCopy;
        SharedController.setBookCopy(null);
        System.out.println("Book status: " + bookCopy.getStatus());
        if (bookCopy.getStatus().equals("Returned")) {
            displayMessage("Book has been returned already");
        } else {
            displayMessage("Book found");
        }
    }

    @Override
    public void loadSubscriber(Subscriber subscriber) {
        setID(String.valueOf(subscriber.getSub_id()));
        SharedController.setSubscriber(null);
        displayMessage("Subscriber found");
    }

    public void returnMessage(String msg) {
        displayMessage(msg);
        if (msg.equals("Book has been returned successfully")) {
            txtCopyId.clear();
            txtID.clear();
        } else {
            displayMessage(msg);
        }

    }  

    public void displayMessage(String message) { //display message to user in label
        messageLabel.setText(message);
    }

    public void goBackBtn(ActionEvent event) throws Exception {
        if (scanWindowStage != null && scanWindowStage.isShowing()) {
            scanWindowStage.close();
        }

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
