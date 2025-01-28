package gui.controllers;
import java.time.LocalDate;

import client.ClientUI;
import client.SharedController;
import common.BookCopy;
import common.MessageUtils;
import common.Subscriber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Controller class for the ReturnBook.fxml file.
 * This class is responsible for handling the return book functionality.
 */
public class ReturnBookController implements ItemLoader {
    private BookCopy bc;

    @FXML private Button btnback;
    @FXML private Button btnscanbook;
    @FXML private TextField txtID;
    @FXML private TextField txtCopyId;
    @FXML private DatePicker actualReturnDate;
    
    @FXML private Label messageLabel;
    private static Stage scanWindowStage; // Track the ScanWindow stage

    private String getSubId() { return txtID.getText(); }
    private String getCopyId() { return txtCopyId.getText(); }
    private LocalDate getActualReturnDate() { return actualReturnDate.getValue(); }

    /**
     * Initializes the controller.
     * This method is called automatically and initializes the text fields.
     * The actual return date is set to the current date and is disabled.
     */
    @FXML
    public void initialize() {
        txtID.setEditable(false);
        txtCopyId.setEditable(false);
        actualReturnDate.setValue(LocalDate.now());
        actualReturnDate.setEditable(false);
        actualReturnDate.setDisable(true);
    }

    /**
     * Sets the subscriber ID in the text field.
     * @param sub_id
     */
    private void setID(String sub_id) { txtID.setText(sub_id); }

    /**
     * Sets the copy ID in the text field.
     * @param copy_id
     */
    private void setCopyID(String copy_id) { txtCopyId.setText(copy_id); }

    /**
     * Handles the scan book button click event.
     * This method shows the scan window for scanning the book copy and reader card.
     * @param event
     * @throws Exception
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
     * Handles the return date submit button click event.
     * This method sends a message to the server to return the book.
     * @param event
     */
    public void handleReturnDateSubmit(ActionEvent event) { 
        try {
            LocalDate returnDate = getActualReturnDate();
            String subId = getSubId();
            String copyId = getCopyId();
            // Input validation
            if (subId.isEmpty() || copyId.isEmpty()) {
                displayMessage("Please scan first");
                return;
            }

            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (bc.getStatus().equals("Returned")) {
                    displayMessage("Book has been returned already");
                } else if (bc.getStatus().equals("Lost")) {
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "returnLostBook", subId + ":" + copyId + ":" + returnDate);
                } else {
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "returnBook", subId + ":" + copyId + ":" + returnDate);
                }
            } else {
                displayMessage("No server connection");
            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the book copy.
     * This method is called by the ScanWindowController to load the book copy.
     * @param bookCopy - the book copy
     * @see ItemLoader
     */
    @Override
    public void loadBookCopy(BookCopy bookCopy) {
        setCopyID(String.valueOf(bookCopy.getCopyId()));
        bc = bookCopy;
        SharedController.setBookCopy(null);
        System.out.println("Book status: " + bookCopy.getStatus());
        if (bookCopy.getStatus().equals("Returned")) {
            displayMessage("Book has been returned already");
        } else {
            displayMessage("Book found");
        }
    }

    /**
     * Loads the subscriber.
     * @param subscriber - the subscriber
     * @see ItemLoader
     */
    @Override
    public void loadSubscriber(Subscriber subscriber) {
        setID(String.valueOf(subscriber.getSub_id()));
        SharedController.setSubscriber(null);
        displayMessage("Subscriber found");
    }

    /**
     * Handles the return message.
     * This method displays the return message to the user.
     * @param msg - the return message
     */
    public void returnMessage(String msg) {
        displayMessage(msg);
        if (msg.equals("Book has been returned successfully")) {
            txtCopyId.clear();
            txtID.clear();
        } else {
            displayMessage(msg);
        }

    }  

    /**
     * Displays a message to the user.
     * @param message - the message to display
     */
    public void displayMessage(String message) { //display message to user in label
        messageLabel.setText(message);
    }

    /**
     * Handles the back button click event.
     * This method closes the current window and loads the LibrarianMainFrame.
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
                    MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , SharedController.getLibrarian().getLibrarian_id());
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
}
