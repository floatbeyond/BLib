package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.Node;

import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import common.Subscriber;
import common.BookCopy;

public class ScanWindowController {

    @FXML private TextField txtScan;
    @FXML private Button btnScan;
    @FXML private Label messageLabel;

    private String command;

    public String getScanText() {
        return txtScan.getText();
    }

    public void handleScanAction(ActionEvent event) {
        String scanText = getScanText();
        if (scanText.isEmpty()) {
            displayMessage("No scan text found");
        } else if (scanText.length() > 10) {
            displayMessage("Scan text too long");
        } else if (!scanText.matches("\\d+")) {
            displayMessage("Scan text must contains numbers");
        } else {
            MessageUtils.sendMessage(ClientUI.cc, "librarian", command, scanText);
            Subscriber foundSubscriber = SharedController.getSubscriber();
            BookCopy foundBookCopy = SharedController.getBookCopy();
            if (foundBookCopy != null) {
                SharedController.getBorrowFormController().loadBookCopy(foundBookCopy);
                closeScanWindow(event);
            } else if (foundSubscriber != null) {
                SharedController.getBorrowFormController().loadSubscriber(foundSubscriber);
                closeScanWindow(event);
            } else {
                displayMessage("Couldnt find a matching item");
            }
        }
    }

    private void closeScanWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void setCommand(String msg) {
        command = msg;
    }

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
    
}
