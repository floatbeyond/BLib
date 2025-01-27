package gui.controllers;
    
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import java.time.LocalDate;

import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import common.Subscriber;
import common.BookCopy;
import common.BorrowingRecord;

/**
 * Controller for the Borrow Form window.
 */
public class BorrowFormController implements ItemLoader {
    private Subscriber s;
    private BookCopy bc;

    @FXML private Button scanButton;

    @FXML private TextField txtID;
    @FXML private TextField txtCopyId;
    @FXML private DatePicker dpReturnDate;

    @FXML private Label lblReturnDate;
    @FXML private int subscriberID;
    @FXML private int copyID;

    @FXML private Label messageLabel;

    private static Stage scanWindowStage; // Track the ScanWindow stage
    private LocalDate currentDate = LocalDate.now();

    /**
     * Initializes the controller class.
     * This method is automatically called after the fxml file has been loaded.
     * Calls setFields() to set the fields to be uneditable.
     */
    @FXML
    private void initialize() {
        setFields();
    }

    private String getSubId() { return txtID.getText(); }
    private String getCopyId() { return txtCopyId.getText(); }
    private LocalDate getReturnDate() { return dpReturnDate.getValue(); }

    /**
     * Sets the fields to be uneditable.
     */
    private void setFields() {
        txtID.setEditable(false);
        txtCopyId.setEditable(false);
        dpReturnDate.setEditable(false);
        dpReturnDate.setDayCellFactory(picker -> new BorrowDateCell());
    }

    /**
     * Custom DateCell class to disable dates before the current date and after 14 days from the current date.
     */
    private class BorrowDateCell extends DateCell {
        @Override
        public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            if (date.isBefore(currentDate.plusDays(1)) || date.isAfter(currentDate.plusDays(14))) {
                setDisable(true);
                setStyle("-fx-background-color: #ffc0cb;");
            }
        }
    }

    /**
     * Shows the Scan Window.
     * If the Scan Window is already open, brings it to the front.
     */
    @FXML
    private void showScanWindow(ActionEvent event) throws Exception {
        if (scanWindowStage != null && scanWindowStage.isShowing()) {
            scanWindowStage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ScanWindow.fxml"));
            Pane root = loader.load();

            ScanWindowController scanWindowController = loader.getController();
            scanWindowController.setCommand("scanBookCopy");
            scanWindowController.setItemLoader(this);

            scanWindowStage = new Stage();
            Scene scene = new Scene(root);
            scanWindowStage.setTitle("Scan Window");
            scanWindowStage.setScene(scene);
            scanWindowStage.setResizable(false);
            scanWindowStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Handles the Borrow button click event.
     * It validates the input fields and sends a new BorrowingRecord to the server.
     * If the server connection is lost, it displays a message to the user.
     * According to the validation process, it displays a message to the user.
     * @param event
     * @throws Exception
     */
    @FXML
    private void handleBorrowAction(ActionEvent event) throws Exception {
        try {
            LocalDate returnDate = getReturnDate();
            String subId = getSubId();
            String copyId = getCopyId();
            // Input validation
            if (subId.isEmpty() || copyId.isEmpty()) {
                displayMessage("Please scan first");
                return;
            }
    
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (bc.getStatus().startsWith("Ordered")) {
                    // get the 
                    String status = bc.getStatus();
                    String orderedSubId = status.substring(status.lastIndexOf(' ') + 1);
                    if (!orderedSubId.equals(subId)) {
                        displayMessage("Book is already ordered, scan another");
                        return;
                    }
                }
                if (s.getSub_status().equals("Frozen")) {
                    displayMessage("Subscriber is frozen");
                    return;
                } else if (s.getSub_status().equals("In-Active")) {
                    displayMessage("Ask subscriber to renew subscription");
                    return;
                } else if (bc.getStatus().equals("Borrowed")) {
                    displayMessage("Book is already borrowed, scan another");
                    return;
                } else if (bc.getStatus().equals("Lost")) {
                    displayMessage("Book is lost, scan another");
                    return;
                } else if (bc.getStatus().equals("Ordered")) {
                    displayMessage("Book is ordered, scan another");
                    return;
                } else if (returnDate == null || returnDate.isAfter(currentDate.plusDays(14))) {
                    displayMessage("Please select a valid return date");
                    return;
                } else {
                    // print return date
                    System.out.println("Return date: " + returnDate);
                    BorrowingRecord newBR = new BorrowingRecord(0, Integer.parseInt(copyId), Integer.parseInt(subId), 
                                                            currentDate, returnDate, null, "Borrowed");
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "newBorrow", newBR);
                }
            } else {
                displayMessage("No server connection");
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the book copy into the form.
     * @param bookCopy The book copy to be loaded.
     */
    @Override
    public void loadBookCopy(BookCopy bookCopy) {
        txtCopyId.setText(String.valueOf(bookCopy.getCopyId()));
        bc = bookCopy;
        SharedController.setBookCopy(null);
        System.out.println("Book status: " + bookCopy.getStatus());
        displayMessage("Book found");
    }

    /**
     * Loads the subscriber into the form.
     * @param subscriber The subscriber to be loaded.
     */
    @Override
    public void loadSubscriber(Subscriber subscriber) {
        txtID.setText(String.valueOf(subscriber.getSub_id()));
        s = subscriber;
        SharedController.setSubscriber(null);
        displayMessage("Subscriber found");
    }

    /**
     * Displays a message to the user.
     * @param message The message to be displayed.
     */
    public void successfulBorrow(String msg) {
        displayMessage(msg);
        txtCopyId.clear();
        txtID.clear();
        dpReturnDate.setValue(null);
    }

    /**
     * Handles the back button click event.
     * It loads the Librarian Main Frame window.
     * @param event
     * @throws Exception
     */
    public void goBackBtn(ActionEvent event) throws Exception {
        if (scanWindowStage != null && scanWindowStage.isShowing()) {
            scanWindowStage.close();
        }

        // Load the LibrarianMainFrame
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LibrarianMainFrame.fxml"));
        Pane root = loader.load();

        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setTitle("Library Tool");
        stage.setScene(scene);
        stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    MessageUtils.sendMessage(ClientUI.cc, "user", "disconnect", null);
                    ClientUI.chat.quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        ((Node) event.getSource()).getScene().getWindow().hide();
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Displays a message to the user.
     * @param message The message to be displayed.
     */
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
    
}
