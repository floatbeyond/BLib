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

import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import common.Subscriber;
import common.BookCopy;

    
public class BorrowFormController {
    private Subscriber s;
    private BookCopy bc;

    @FXML private Button scanButton;

    @FXML private TextField txtID;
    @FXML private TextField txtCopyId;
    @FXML private TextField txtReturnDate;

    @FXML private Label lblReturnDate;
    @FXML private int subscriberID;
    @FXML private int copyID;

    @FXML private Label messageLabel;

    private static Stage scanWindowStage; // Track the ScanWindow stage

    @FXML
    private void initialize() {
        txtID.setEditable(false);
        txtCopyId.setEditable(false);
        txtReturnDate.setEditable(true);
    }

    @FXML
    private void showScanWindow(ActionEvent event) throws Exception {
        if (scanWindowStage != null && scanWindowStage.isShowing()) {
            scanWindowStage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ScanWindow.fxml"));
            Pane root = loader.load();

            SharedController.setScanWindowController(loader.getController());
            scanWindowStage = new Stage();
            Scene scene = new Scene(root);
            SharedController.getScanWindowController().setCommand("scanBookCopy");
            scanWindowStage.setTitle("Scan Window");
            scanWindowStage.setScene(scene);
            scanWindowStage.setResizable(false);
            scanWindowStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleBorrowAction(ActionEvent event) throws Exception {
        try {
            // Input validation
            if (txtID.getText().isEmpty() || txtCopyId.getText().isEmpty()) {
                displayMessage("Please scan first");
                return;
            }
    
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (s.getSub_status().equals("Frozen")) {
                    displayMessage("Subscriber is frozen");
                    return;
                } else if (bc.getStatus().equals("Borrowed")) {
                    displayMessage("Book is already borrowed");
                    return;
                } else {
                    MessageUtils.sendMessage(ClientUI.cc, "librarian", "borrow", null);
                    displayMessage("Book borrowed successfully");
                }
            } else {
                displayMessage("No server connection");
                return;
            }
        } catch (Exception e) {
            displayMessage("An error occurred: " + e.getMessage());
        }
    }

    public void loadBookCopy(BookCopy bookCopy) {
        txtCopyId.setText(String.valueOf(bookCopy.getCopyId()));
        // print
        System.out.println("Book status: " + bookCopy.getStatus());
        if (bookCopy.getStatus().equals("Borrowed")) {
            // print book status
            System.out.println("Book status: " + bookCopy.getStatus());
            displayMessage("Book is already borrowed");
        }
        txtCopyId.setEditable(false);
    }

    public void loadSubscriber(Subscriber subscriber) {
        txtID.setText(String.valueOf(subscriber.getSub_id()));
        txtID.setEditable(false);
    }

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

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
    
}
