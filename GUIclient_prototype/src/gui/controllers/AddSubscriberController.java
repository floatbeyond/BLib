package gui.controllers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import common.MessageUtils;
import client.ClientUI;
import client.SharedController;
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


/**
 * Controller for the Add Subscriber window.
 */
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

    /**
     * Initializes the controller class.
     * This method is automatically called after the fxml file has been loaded.
     * It sets the join date and expiration date fields to the current date and the date one year from now, respectively.
     * It clears the text fields.
     */
    @FXML
    private void initialize() {
        txtJoinDate.setText(LocalDate.now().format(USER_DATE_FORMATTER));
        txtExDate.setText(LocalDate.now().plusYears(1).format(USER_DATE_FORMATTER));
        txtJoinDate.setEditable(false);
        txtExDate.setEditable(false);
        clearFields();
    }

    /**
     * Clears the text fields.
     */
    private void clearFields() {
        txtFName.clear();
        txtEmail.clear();
        txtPNumber.clear();
    }

    /**
     * Handles the register button click event.
     * It sends a message to the server to add a new subscriber with the data from the text fields.
     * @param event
     */
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

    /**
     * Handles the back button click event.
     * It loads the Librarian Main Frame window.
     * @param event
     */
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
            } else {
                    displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a message to the user.
     * @param message The message to be displayed.
     */
    public void newSubAdded(String message) {
        displayMessage(message);
    }

    /**
     * Checks if the given name is a valid full name.
     * A valid full name contains only letters, spaces, hyphens, and apostrophes.
     * @param name
     * @return
     */
    private boolean isValidFullName(String name) {
        String pattern = "^[a-zA-Z]+([ '-][a-zA-Z]+)*$";
        return name.matches(pattern);
    }

    /**
     * Checks if the given phone number is a valid phone number.
     * A valid phone number is in the format 05X-XXXXXXX, where X is a digit.
     * @param phoneNumber
     * @return
     */
    private boolean isValidPhoneNumber(String phoneNumber) {
        String pattern = "^05\\d-\\d{7}$";
        return phoneNumber.matches(pattern);
    }

    /**
     * Checks if the given email is a valid email.
     * A valid email is in the format local-part@domain, where local-part and domain are strings of letters, digits, and special characters.
     * @param email
     * @return
     */
	private boolean isValidEmail(String email) {
		String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.matches(pattern);
	}

    /**
     * Displays a message to the user.
     * @param message The message to be displayed.
     */
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
