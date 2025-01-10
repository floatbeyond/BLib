package gui;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import common.BookCopy;
import common.BorrowingRecord;

public class CopiesDialogController {

    @FXML private TableView<BookCopy> copyTable;
    @FXML private TableColumn<BookCopy, String> locationCol;
    @FXML private TableColumn<BookCopy, String> statusCol;
    @FXML private TableColumn<BookCopy, String> expectedReturnDateCol;

    public void initialize() {
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        expectedReturnDateCol.setCellValueFactory(data -> {
            BookCopy copy = data.getValue();
            BorrowingRecord record = copy.getBorrowingRecord();
            return new SimpleStringProperty(record != null ? record.getExpectedReturnDate().toString() : "N/A");
        });
    }

    public void setCopiesData(ObservableList<BookCopy> copies) {
        copyTable.setItems(copies);
    }
}