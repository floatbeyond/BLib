package gui.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import common.BorrowRecordDTO;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controller for the Active Borrows window.
 */
public class ActiveBorrowsController {
    @FXML private TableView<BorrowRecordDTO> tableViewBorrows;
    @FXML private TableColumn<BorrowRecordDTO, String> colBookName;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colBorrowDate;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colExpectedReturnDate;
    @FXML private TableColumn<BorrowRecordDTO, String> colStatus;
    @FXML private TableColumn<BorrowRecordDTO, Void> colExtend;
    @FXML private Button closeButton;

    /**
     * Initializes the controller class.
     * This method is automatically called after the fxml file has been loaded.
     * It sets up the columns of the table.
     */
    @FXML
    private void initialize() {
        setupColumns();
    }
    
    /**
     * Sets up the table and its columns.
     */
    private void setupColumns() {
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colExpectedReturnDate.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Date formatter for dates
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        colBorrowDate.setCellFactory(column -> new TableCell<BorrowRecordDTO, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });
        
        colExpectedReturnDate.setCellFactory(column -> new TableCell<BorrowRecordDTO, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.format(formatter));
            }
        });

        setupActionColumn();
    }
    
    /**
     * Sets up the action column with a button to extend the return date.
     */
    private void setupActionColumn() {
        colExtend.setCellFactory(param -> new TableCell<BorrowRecordDTO, Void>() {
            private final Button extendBtn = new Button("Extend");
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }
                
                BorrowRecordDTO borrow = getTableView().getItems().get(getIndex());
                extendBtn.setOnAction(e -> handleExtend(borrow));
                
                setGraphic(extendBtn);
            }
        });
    }
    
    /**
     * Handles the Extend button click event.
     * Opens the Extend Window for the selected BorrowRecordDTO object.
     * @param borrow The BorrowRecordDTO object to extend the return date for.
     */
    private void handleExtend(BorrowRecordDTO borrow) {
        try {
            MessageUtils.sendMessage(ClientUI.cc, "subscriber", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (SharedController.getSubscriber().getSub_status().equals("In-Active")) {
                    SharedController.smfc.displayMessage("Your account is inactive. Please contact a librarian to activate it.");
                    return;
                }

                if (SharedController.getSubscriber().getSub_status().equals("Frozen")) {
                    SharedController.smfc.displayMessage("Your account is frozen. Please wait until " + SharedController.getSubscriber().getSub_freeze());
                    return;
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ExtendWindow.fxml"));
                Pane root = loader.load();
                
                ExtendWindowController controller = loader.getController();
                controller.setBorrowRecord(borrow);
                controller.setSubscriberId(SharedController.getSubscriber().getSub_id());
                controller.setUser("subscriber");
                
                Stage stage = new Stage();
                controller.setStage(stage);
                
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Extend Return Date");
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } else {
                // Handle connection error
                System.out.println("Failed to connect to server");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }    
    }
    
    /**
     * Sets the table data with the given list of BorrowRecordDTO objects.
     * @param borrows The list of BorrowRecordDTO objects to display in the table.
     */
    public void setTableData(ObservableList<BorrowRecordDTO> borrows) {
        tableViewBorrows.setItems(borrows);
    }

    /**
     * Handles the Close button click event.
     * @param event
     */
    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        SubscriberMainFrameController.setActiveBorrowsStage(null);
        stage.close();
    }
}