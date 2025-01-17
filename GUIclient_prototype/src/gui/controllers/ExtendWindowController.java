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

import java.time.LocalDate;

public class ExtendWindowController {

    @FXML private DatePicker datePicker;
    @FXML private Button btnSubmit;
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
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date.isBefore(borrowRecord.getExpectedReturnDate().plusDays(1))) {
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
        if (newDate != null) {
            // Check if there are any orders for this book
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "checkOrder", sub_id + ":" + borrowRecord.getBorrowId() + ":" + newDate + ":" + SharedController.getLibrarian().getLibrarian_name()); 
        } else {
            // Handle error: no date selected
            displayMessage("Please select a date");
        }
    }

    public void successfulExtend(String message) {
        displayMessage(message);
        if (message.equals("Borrowing extended")) {
            borrowRecord.setExpectedReturnDate(datePicker.getValue());
        }
    }

    private void displayMessage(String message) {
        messageLabel.setText(message);
    }
}