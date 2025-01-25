package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.scene.control.Button;


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
    private ItemLoader itemLoader;

    public String getScanText() {
        return txtScan.getText();
    }

    @FXML
    public void initialize() {
        txtScan.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleScanAction();
            }
        });
    }

    public void handleScanAction() {
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
                itemLoader.loadBookCopy(foundBookCopy);
                closeScanWindow();
            } else if (foundSubscriber != null) {
                itemLoader.loadSubscriber(foundSubscriber);
                closeScanWindow();
            } else {
                displayMessage("Couldnt find a matching item");
            }
        }
    }

    private void closeScanWindow() {
        Stage stage = (Stage) txtScan.getScene().getWindow();
        stage.close();
    }

    // Keep ActionEvent version for button
    @FXML
    public void handleScanButtonAction(ActionEvent event) {
        handleScanAction();
    }

    public void setCommand(String msg) {
        command = msg;
    }

    public void setItemLoader(ItemLoader loader) {
        itemLoader = loader;
    }

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
    
}
