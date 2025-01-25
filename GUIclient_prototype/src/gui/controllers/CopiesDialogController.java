package gui.controllers;

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

    private boolean isLibrarian = false;

    public void initialize() {
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));

        statusCol.setCellValueFactory(data -> {
            BookCopy copy = data.getValue();
            String status = copy.getStatus();
            
            // For guests/subscribers, show simplified status
            if (status.startsWith("Ordered by")) {
                if (isLibrarian) {
                    String subId = status.substring("Ordered by ".length());
                    return new SimpleStringProperty("Ordered (" + subId + ")");
                }
                return new SimpleStringProperty("Ordered");
            }
            
            // For librarian, show full status with ID
            BorrowingRecord record = copy.getBorrowingRecord();
            if (isLibrarian && record != null && 
                (status.equals("Borrowed") || status.equals("Late") || status.equals("Lost"))) {
                return new SimpleStringProperty(status + " (" + record.getSubId() + ")");
            }
            
            return new SimpleStringProperty(status);
        });
        
        expectedReturnDateCol.setCellValueFactory(data -> {
            BookCopy copy = data.getValue();
            String status = copy.getStatus();
            BorrowingRecord record = copy.getBorrowingRecord();
            
            // Hide order details for non-librarians
            if (status != null && status.startsWith("Ordered by")) {
                return new SimpleStringProperty("N/A");
            }
            
            return new SimpleStringProperty(record != null ? record.getExpectedReturnDate().toString() : "N/A");
        });
    }

    public void setCopiesData(ObservableList<BookCopy> copies) {
        copyTable.setItems(copies);
    }

    public void setLibrarianView(boolean isLibrarian) {
        this.isLibrarian = isLibrarian;
    }
}