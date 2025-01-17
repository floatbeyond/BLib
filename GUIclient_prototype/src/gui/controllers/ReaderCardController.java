package gui.controllers;

import common.BorrowRecordDTO;
import common.OrderRecordDTO;
import common.DataLogs;
import common.DateUtils;
import common.MessageUtils;
import common.Subscriber;
import client.ClientUI;
import client.SharedController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableCell;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ReaderCardController {

    @FXML private Label lblName;
    @FXML private Label lblID;
    @FXML private Label lblPhone;
    @FXML private Label lblEmail;
    @FXML private Label lblStatus;
    @FXML private Label lblPenalties;
    @FXML private Label lblJoined;
    @FXML private Label lblExpires;
    @FXML private Label lblBooksHeld;

    @FXML private TabPane tabPane;
    @FXML private Tab tabLogs;
    @FXML private Tab tabBorrowed;
    @FXML private Tab tabOrdered;

    @FXML private ListView<String> listViewLogs;

    @FXML private TableView<BorrowRecordDTO> tableViewBorrows;
    @FXML private TableColumn<BorrowRecordDTO, String> colBookName;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colBorrowDate;
    @FXML private TableColumn<BorrowRecordDTO, LocalDate> colExpectedReturnDate;
    @FXML private TableColumn<BorrowRecordDTO, String> colStatus;
    @FXML private TableColumn<BorrowRecordDTO, Void> colExtend;

    @FXML private TableView<OrderRecordDTO> tableViewOrders;
    @FXML private TableColumn<OrderRecordDTO, String> colBookName2;
    @FXML private TableColumn<OrderRecordDTO, LocalDate> colOrderDate;
    @FXML private TableColumn<OrderRecordDTO, LocalDate> colStatus2;
    @FXML private TableColumn<OrderRecordDTO, String> colNotify;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private Subscriber s;
    private List<Object> userLogs = new ArrayList<>();
    private List<BorrowRecordDTO> borrowRecords = new ArrayList<>();
    private List<OrderRecordDTO> orderRecords = new ArrayList<>();

    @FXML
    private void initialize() {
        Subscriber subscriber = SharedController.getSubscriber();
        if (subscriber != null) {
            loadSubscriber(subscriber);
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "userLogs", subscriber.getSub_id());
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "userBorrows", s.getSub_id());
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "userOrders", s.getSub_id());
        }

        tabPane.getSelectionModel().selectedItemProperty().addListener((observable, oldTab, newTab) -> {
            if (newTab == tabLogs) {
                showDataLogs(userLogs);
            } else if (newTab == tabBorrowed) {
                showUserBorrows();
            } else if (newTab == tabOrdered) {
                showUserOrders();
            }
        });

        initBorrowCols();
        initOrderCols();

    }

    private void initBorrowCols() {
        colBookName.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colBorrowDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colExpectedReturnDate.setCellValueFactory(new PropertyValueFactory<>("expectedReturnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colExtend.setCellFactory(param -> new TableCell<BorrowRecordDTO, Void>() {
            private final Button btn = new Button("Extend");

            {
                btn.setOnAction(event -> {
                    BorrowRecordDTO borrowRecord = getTableView().getItems().get(getIndex());
                    openExtendWindow(borrowRecord);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btn);
                }
            }
        });
    }

    private void initOrderCols() {
        colBookName2.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colOrderDate.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        colStatus2.setCellValueFactory(new PropertyValueFactory<>("status"));
        colNotify.setCellValueFactory(new PropertyValueFactory<>("notificationStamp"));
    }

    private void loadSubscriber(Subscriber subscriber) {
        this.s = subscriber;
        appendText(lblName, s.getSub_name());
        appendText(lblID, String.valueOf(s.getSub_id()));
        appendText(lblPhone, s.getSub_phone_num());
        appendText(lblEmail, s.getSub_email());
        appendText(lblStatus, s.getSub_status());
        appendText(lblPenalties, String.valueOf(s.getSub_penalties()));
        appendText(lblJoined, s.getSub_joined().format(DATE_FORMATTER));
        appendText(lblExpires, s.getSub_expiration().format(DATE_FORMATTER));
        String booksHeld = s.getCurrentlyBorrowed() + "/" + s.getCurrentlyOrdered();
        appendText(lblBooksHeld, booksHeld);
    }

    private void appendText(Label label, String text) {
        String currentText = label.getText();
        label.setText(currentText + " " + text);
    }

    public void setAllDataLogs(List<Object> logs) {
        this.userLogs = logs;
        showDataLogs(userLogs);
    }

    public void setBorrowRecords(List<BorrowRecordDTO> list) {
        this.borrowRecords = list;
    }

    public void setOrderRecords(List<OrderRecordDTO> list) {
        this.orderRecords = list;
    }

    public void showDataLogs(List<Object> logs) {
        ObservableList<String> items = FXCollections.observableArrayList();
        int logNumber = 1;
        for (Object log : logs) {
            items.add(formatLog(log, logNumber));
            logNumber++;
        }
        listViewLogs.setItems(items);
    }

    private String formatLog(Object log, int logNumber) {
        DataLogs dataLog = (DataLogs) log;
      
        // Format the date
        String formattedDate = DateUtils.formatTimestamp(dataLog.getTimestamp(), "dd-MM-yyyy HH:mm:ss");
        return logNumber + ". " + dataLog.getLog_action() + ", " + formattedDate;
    }


    public void showUserBorrows() {
        ObservableList<BorrowRecordDTO> data = FXCollections.observableArrayList(borrowRecords);
        tableViewBorrows.setItems(data);
    }



    public void showUserOrders() {
        ObservableList<OrderRecordDTO> data = FXCollections.observableArrayList(orderRecords);
        tableViewOrders.setItems(data);
    }

    private void openExtendWindow(BorrowRecordDTO borrowRecord) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ExtendWindow.fxml"));
            Pane root = loader.load();            
            ExtendWindowController controller = loader.getController();
            controller.setBorrowRecord(borrowRecord);
            controller.setSubscriberId(s.getSub_id());

            Stage stage = new Stage();
            controller.setStage(stage);

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Extend Return Date");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
