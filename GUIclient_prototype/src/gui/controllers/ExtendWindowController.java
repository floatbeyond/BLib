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

    @FXML
    public void initialize() {
        SharedController.setExtendWindowController(this);
    }

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

    public void setSubscriberId(int sub_id) {
        this.sub_id = sub_id;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setUser(String user) {
        this.user = user;
    }

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
        }
    
        return true;
    }

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

    public void successfulExtend(String message) {
        displayMessage(message);
        if (message.equals("Borrowing extended")) {
            borrowRecord.setExpectedReturnDate(datePicker.getValue());
        }
    }

    @FXML
    private void handleClose(ActionEvent event) {
        stage.close();
    }

    private void displayMessage(String message) {
        messageLabel.setText(message);
    }

    private boolean connectToServer() {
        MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
        if (ClientUI.cc.getConnectionStatusFlag() != 1) {
            displayMessage("No server connection");
            return false;
        }
        return true;
    }
}