package gui.controllers;

import client.ClientUI;
import client.SharedController;
import common.BorrowRecordDTO;
import common.MessageUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.event.ActionEvent;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ExtendWindowController {

    @FXML private DatePicker datePicker;
    @FXML private Button btnSubmit;
    @FXML private Button closeButton;
    @FXML private Label messageLabel;

    private int sub_id;
    private BorrowRecordDTO borrowRecord;
    private Stage stage;
    private String user;

    /**
     * This method is called when the ExtendWindow.fxml file is loaded.
     * It is responsible for setting the controller instance in the SharedController class.
     */
    @FXML
    public void initialize() {
        SharedController.setExtendWindowController(this);
    }

    /**
     * This method is called when the ExtendWindow.fxml file is loaded.
     * It is responsible for setting the borrow record for the current window.
     * It is also responsible for setting the date picker to disable dates before the current date and after 14 days after the expected return date.
     * @param borrowRecord
     */
    public void setBorrowRecord(BorrowRecordDTO borrowRecord) {
        this.borrowRecord = borrowRecord;
        LocalDate currentDate = LocalDate.now();
        LocalDate expectedReturnDate = borrowRecord.getExpectedReturnDate();
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date.isBefore(currentDate.plusDays(1)) || date.isAfter(expectedReturnDate.plusDays(14))) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc0cb;");
                }
            }
        });
    }

    /**
     * Sets the subscriber ID.
     *
     * @param sub_id the subscriber ID to set
     */
    public void setSubscriberId(int sub_id) { this.sub_id = sub_id; }

    /**
     * Sets the stage.
     *
     * @param stage the stage to set
     */
    public void setStage(Stage stage) { this.stage = stage; }

    /**
     * Sets the user.
     *
     * @param user the user to set
     */
    public void setUser(String user) { this.user = user; }

    /**
     * This method is called when the user clicks the Submit button.
     * It is responsible for validating the new date.
     * It is also responsible for sending the extension request to the server.
     */
    @FXML
    private void handleSubmit() {
        if (!connectToServer()) {
            return;
        }
    
        LocalDate newDate = datePicker.getValue();
        if (!validateDate(newDate)) {
            return;
        }
    
        sendExtensionRequest(newDate);
    }

    /**
     * Validates the new date its being extended to.
     * @param newDate
     * @return
     */
    private boolean validateDate(LocalDate newDate) {
        if (newDate == null) {
            displayMessage("Please select a date");
            return false;
        }
    
        if (newDate.isBefore(borrowRecord.getExpectedReturnDate())) {
            displayMessage("New date must be after expected return date");
            return false;
        }
    
        if (user.equals("subscriber")) {
            long daysDifference = ChronoUnit.DAYS.between(LocalDate.now(), 
                borrowRecord.getExpectedReturnDate());
            if (daysDifference > 7) {
                displayMessage("Can only extend within 7 days of return date");
                return false;
            }
            if (borrowRecord.getStatus().equals("Late") || borrowRecord.getStatus().equals("Lost")) {
                displayMessage("Cannot extend late or lost borrowings");
                return false;
            }
        }
    
        return true;
    }

    /**
     * Sends the extension request to the server.
     * @param newDate
     */
    private void sendExtensionRequest(LocalDate newDate) {
        StringBuilder msgBuilder = new StringBuilder()
            .append(sub_id)
            .append(":")
            .append(borrowRecord.getBorrowId())
            .append(":")
            .append(newDate);
    
        if (user.equals("librarian")) {
            msgBuilder.append(":")
                     .append(SharedController.getLibrarian().getLibrarian_name());
        }
    
        MessageUtils.sendMessage(ClientUI.cc, user, "checkOrder", msgBuilder.toString());
    }

    /**
     * Displays a message to the user of the extension status.
     * @param message
     */
    public void successfulExtend(String message) {
        displayMessage(message);
        if (message.equals("Borrowing extended")) {
            borrowRecord.setExpectedReturnDate(datePicker.getValue());
        }
    }
    
    /**
     * This method is called when the user clicks the Close button.
     * It is responsible for closing the current window.
     */
    @FXML
    private void handleClose(ActionEvent event) {
        stage.close();
    }

    /**
     * Displays a message to the user.
     * @param message
     */
    private void displayMessage(String message) {
        messageLabel.setText(message);
    }

    /**
     * Connects to the server.
     * @return
     */
    private boolean connectToServer() {
        MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
        if (ClientUI.cc.getConnectionStatusFlag() != 1) {
            displayMessage("No server connection");
            return false;
        }
        return true;
    }
}