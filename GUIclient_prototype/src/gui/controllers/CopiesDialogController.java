package gui.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleStringProperty;
import common.BookCopy;
import common.BorrowingRecord;

/**
 * This class is the controller for the CopiesDialog.fxml file. It is responsible for displaying the copies of a book
 * in a table view. It is also responsible for displaying the status of each copy and the expected return date.
 */
public class CopiesDialogController {

    @FXML private TableView<BookCopy> copyTable;
    @FXML private TableColumn<BookCopy, String> locationCol;
    @FXML private TableColumn<BookCopy, String> statusCol;
    @FXML private TableColumn<BookCopy, String> expectedReturnDateCol;

    private boolean isLibrarian = false;

    /**
     * This method is called when the CopiesDialog.fxml file is loaded. 
     * It is responsible for initializing the table view and setting the cell value factories for the columns.
     */
    @FXML
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

    /**
     * This method is called when the copies data is set. 
     * It is responsible for setting the copies data in the table view.
     * @param copies
     */
    public void setCopiesData(ObservableList<BookCopy> copies) {
        copyTable.setItems(copies);
    }

    /**
     * This method is called when the window is opened through the librarian view. 
     * It is responsible for setting the librarian view.
     * @param isLibrarian
     */
    public void setLibrarianView(boolean isLibrarian) {
        this.isLibrarian = isLibrarian;
    }
}