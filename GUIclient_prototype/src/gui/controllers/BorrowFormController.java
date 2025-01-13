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

import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import common.Subscriber;
import common.BookCopy;
import common.BorrowingRecord;

    
public class BorrowFormController {
    private Subscriber s;
    private BookCopy bc;

    @FXML private Button scanButton;

    @FXML private TextField txtID;
    @FXML private TextField txtCopyId;
    @FXML private DatePicker dpReturnDate;

    @FXML private Label lblReturnDate;
    @FXML private int subscriberID;
    @FXML private int copyID;

    @FXML private Label messageLabel;

    private static Stage scanWindowStage; // Track the ScanWindow stage

    @FXML
    private void initialize() {
        txtID.setEditable(false);
        txtCopyId.setEditable(false);
        dpReturnDate.setEditable(false);
    }

    private String getSubId() { return txtID.getText(); }
    private String getCopyId() { return txtCopyId.getText(); }
    private LocalDate getReturnDate() { return dpReturnDate.getValue(); }

    @FXML
    private void showScanWindow(ActionEvent event) throws Exception {
        if (scanWindowStage != null && scanWindowStage.isShowing()) {
            scanWindowStage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ScanWindow.fxml"));
            Pane root = loader.load();

            SharedController.setScanWindowController(loader.getController());
            scanWindowStage = new Stage();
            Scene scene = new Scene(root);
            SharedController.getScanWindowController().setCommand("scanBookCopy");
            scanWindowStage.setTitle("Scan Window");
            scanWindowStage.setScene(scene);
            scanWindowStage.setResizable(false);
            scanWindowStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBorrowAction(ActionEvent event) throws Exception {
        try {
            LocalDate returnDate = getReturnDate();
            String subId = getSubId();
            String copyId = getCopyId();
            LocalDate currentDate = LocalDate.now();
            // Input validation
            if (subId.isEmpty() || copyId.isEmpty()) {
                displayMessage("Please scan first");
                return;
            }
    
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (s.getSub_status().equals("Frozen")) {
                    displayMessage("Subscriber is frozen");
                    return;
                } else if (bc.getStatus().equals("Borrowed")) {
                    displayMessage("Book is already borrowed, scan another");
                    return;
                } else if (returnDate == null || returnDate.isBefore(currentDate.plusDays(1)) || returnDate.isAfter(currentDate.plusDays(14))) {
                    displayMessage("Please select a valid return date");
                    return;
                } else {
                    // print return date
                    System.out.println("Return date: " + returnDate);
                    BorrowingRecord newBR = new BorrowingRecord(0, Integer.parseInt(copyId), Integer.parseInt(subId), 
                                                            currentDate, returnDate, null, "Borrowed");
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "newBorrow", newBR);
                }
            } else {
                displayMessage("No server connection");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void loadBookCopy(BookCopy bookCopy) {
        txtCopyId.setText(String.valueOf(bookCopy.getCopyId()));
        bc = bookCopy;
        SharedController.setBookCopy(null);
        // print
        System.out.println("Book status: " + bookCopy.getStatus());
        if (bookCopy.getStatus().equals("Borrowed")) {
            // print book status
            System.out.println("Book status: " + bookCopy.getStatus());
            displayMessage("Book is already borrowed, scan another");
        } else {
            displayMessage("Book found");
        }
        txtCopyId.setEditable(false);
    }

    public void loadSubscriber(Subscriber subscriber) {
        txtID.setText(String.valueOf(subscriber.getSub_id()));
        s = subscriber;
        SharedController.setSubscriber(null);
        if (subscriber.getSub_status().equals("Frozen")) {
            displayMessage("Subscriber is frozen");
        } else {
            displayMessage("Subscriber found");
        }
        txtID.setEditable(false);
    }

    public void successfulBorrow(String msg) {
        displayMessage(msg);
        txtCopyId.clear();
        txtID.clear();
        dpReturnDate.setValue(null);
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

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
    
}
