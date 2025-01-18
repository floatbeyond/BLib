package gui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import common.MessageUtils;
import client.ClientUI;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import common.Subscriber;
import javafx.scene.control.Label;


public class AddSubscriberController {

    @FXML private Button btnRegister;
    @FXML private Button btnBack;

    @FXML private Label messageLabel;

    @FXML private TextField txtFName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPNumber;
    @FXML private TextField txtJoinDate;
    @FXML private TextField txtExDate;

    private static final DateTimeFormatter USER_DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DB_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @FXML
    private void initialize() {
        txtJoinDate.setText(LocalDate.now().format(USER_DATE_FORMATTER));
        txtExDate.setText(LocalDate.now().plusYears(1).format(USER_DATE_FORMATTER));
        txtJoinDate.setEditable(false);
        txtExDate.setEditable(false);
        clearFields();
    }

    private void clearFields() {
        txtFName.clear();
        txtEmail.clear();
        txtPNumber.clear();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                String name = txtFName.getText();
                String email = txtEmail.getText();
                String phone = txtPNumber.getText();

                if (name.isEmpty() || email.isEmpty() || phone.isEmpty()) {
                    displayMessage("Fill all the fields");
                    return;
                }

                if (!isValidFullName(name)) {
                    displayMessage("Invalid Name");
                    return;
                }

                if (!isValidPhoneNumber(phone)) {
					displayMessage("Invalid phone number");
					return;
				}

				if (!isValidEmail(email)) {
					displayMessage("Invalid email");
					return;
				}
                LocalDate joinDate = LocalDate.now();
                LocalDate exDate = LocalDate.now().plusYears(1);
                Subscriber newSub = new Subscriber(0, name, "Active", phone, email, 0, null, joinDate, exDate, 0, 0);
                MessageUtils.sendMessage(ClientUI.cc, "librarian", "newSubscriber", newSub);
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc, "librarian", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
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
            } else {
                    displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void newSubAdded(String message) {
        displayMessage(message);
    }

    private boolean isValidFullName(String name) {
        String pattern = "^[a-zA-Z]+([ '-][a-zA-Z]+)*$";
        return name.matches(pattern);
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String pattern = "^05\\d-\\d{7}$";
        return phoneNumber.matches(pattern);
    }

	private boolean isValidEmail(String email) {
		String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.matches(pattern);
	}

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
