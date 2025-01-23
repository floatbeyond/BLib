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

public class ExtendWindowController {

    @FXML private DatePicker datePicker;
    @FXML private Button btnSubmit;
    @FXML private Button closeButton;
    @FXML private Label messageLabel;

    private int sub_id;
    private BorrowRecordDTO borrowRecord;
    private Stage stage;

    @FXML
    public void initialize() {
        SharedController.setExtendWindowController(this);
    }

    public void setBorrowRecord(BorrowRecordDTO borrowRecord) {
        this.borrowRecord = borrowRecord;
        LocalDate currentDate = LocalDate.now();
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(currentDate.plusDays(1))) {
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

    @FXML
    private void handleSubmit() {
        LocalDate newDate = datePicker.getValue();
        try { 
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (newDate != null) {
                    // Check if there are any orders for this book
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "checkOrder", sub_id + ":" + borrowRecord.getBorrowId() + ":" + newDate + ":" + SharedController.getLibrarian().getLibrarian_name()); 
                } else {
                    // Handle error: no date selected
                    displayMessage("Please select a date");
                }
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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
}