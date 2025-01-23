package gui.controllers;

import client.ClientUI;
import common.DateUtils;
import common.MessageUtils;
import common.OrderRecordDTO;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.scene.control.TableCell;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ActiveOrdersController {

    @FXML private TableView<OrderRecordDTO> activeOrdersTable;
    @FXML private TableColumn<OrderRecordDTO, String> bookTitleCol;
    @FXML private TableColumn<OrderRecordDTO, LocalDate> orderDateCol;
    @FXML private TableColumn<OrderRecordDTO, String> statusCol;
    @FXML private TableColumn<OrderRecordDTO, Timestamp> notificationStampCol;
    @FXML private TableColumn<OrderRecordDTO, Void> actionCol;

    @FXML private Button closeButton;

    @FXML
    public void initialize() {

        // Initialize the columns
        bookTitleCol.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        orderDateCol.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        notificationStampCol.setCellValueFactory(new PropertyValueFactory<>("notificationTimestamp"));

        // Custom cell factory for orderDateCol
        orderDateCol.setCellFactory(column -> new TableCell<OrderRecordDTO, LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        // Custom cell factory for notificationStampCol
        notificationStampCol.setCellFactory(column -> new TableCell<OrderRecordDTO, Timestamp>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

            @Override
            protected void updateItem(Timestamp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(DateUtils.toLocalDateTime(item).format(formatter));
                }
            }
        });

        // Initialize the button column
        actionCol.setCellFactory(new Callback<TableColumn<OrderRecordDTO, Void>, TableCell<OrderRecordDTO, Void>>() {
            @Override
            public TableCell<OrderRecordDTO, Void> call(final TableColumn<OrderRecordDTO, Void> param) {
                final TableCell<OrderRecordDTO, Void> cell = new TableCell<OrderRecordDTO, Void>() {
                    private final Button btn = new Button("Cancel");

                    {
                        btn.setOnAction(event -> {
                            OrderRecordDTO order = getTableView().getItems().get(getIndex());
                            cancelOrder(order);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        });
    }

    public void setOrdersData(ObservableList<OrderRecordDTO> orders) {
        activeOrdersTable.setItems(orders);
    }

    public void alertMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Cancel");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    private void cancelOrder(OrderRecordDTO order) {
        // Implement the logic to cancel the order
        System.out.println("Cancelling order for book: " + order.getBookTitle());

        // Send a request to the server to cancel the order
        MessageUtils.sendMessage(ClientUI.cc, "user", "cancelOrder", order.getOrderId());
    }

    @FXML
    private void handleClose(ActionEvent event) {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        SubscriberMainFrameController.setActiveOrdersStage(null);
        stage.close();
    }
}