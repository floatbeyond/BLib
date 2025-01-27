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

/**
 * Controller class for the ScanWindow.fxml file.
 * This class is responsible for handling the scan functionality.
 */
public class ScanWindowController {

    @FXML private TextField txtScan;
    @FXML private Button btnScan;
    @FXML private Label messageLabel;

    private String command;
    private ItemLoader itemLoader;

    public String getScanText() {
        return txtScan.getText();
    }

    /**
     * Initializes the controller.
     * This method is called automatically.
     * It listens for the Enter key to be pressed for scanning.
     */
    @FXML
    public void initialize() {
        txtScan.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleScanAction();
            }
        });
    }

    /**
     * Handles the scan action.
     * This method is called when the scan button is pressed.
     * It sends a message to the server with the scan text.
     * If a subscriber or book copy is found, it loads the item into the controller.
     * If no item is found, it displays a message.
     */
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

    /**
     * Closes the scan window.
     * This method closes the scan window.
     */
    private void closeScanWindow() {
        Stage stage = (Stage) txtScan.getScene().getWindow();
        stage.close();
    }

    /**
     * Handles the scan button action.
     * This method is called when the scan button is pressed.
     */
    @FXML
    public void handleScanButtonAction(ActionEvent event) {
        handleScanAction();
    }

    /**
     * Sets the command (type of scan).
     * @param msg
     */
    public void setCommand(String msg) {
        command = msg;
    }

    /**
     * Sets the item loader.
     * @param loader
     */
    public void setItemLoader(ItemLoader loader) {
        itemLoader = loader;
    }

    /**
     * Displays a message.
     * @param message
     */
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
    
}
