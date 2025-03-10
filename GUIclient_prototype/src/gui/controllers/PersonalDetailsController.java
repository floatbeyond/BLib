package gui.controllers;

import java.time.format.DateTimeFormatter;

import client.ClientUI;
import common.MessageUtils;
import common.Subscriber;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import client.SharedController;

/**
 * PersonalDetailsController class is the controller for the PersonalDetails.fxml file.
 * It is responsible for handling the subscriber's personal details and updating them.
 */
public class PersonalDetailsController {
    private Subscriber s;
    @FXML private Button btnBack = null;
	@FXML private Button btnUpdate = null;

	@FXML private Label messageLabel;
	@FXML private Label lblID;
	@FXML private Label lblName;
	@FXML private Label lblPNumber;
	@FXML private Label lblEmail;
    @FXML private Label lblJoinDate;
    @FXML private Label lblExDate;
    @FXML private Label lblNumBookBorrowed;
    @FXML private Label lblNumBookOrdered;


	@FXML private TextField txtID;
	@FXML private TextField txtName;
	@FXML private TextField txtPNumber;
	@FXML private TextField txtEmail;
    @FXML private TextField txtJoinDate;
    @FXML private TextField txtExDate;
    @FXML private TextField txtNumBookBorrowed;
    @FXML private TextField txtNumBookOrdered;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	/**
	 * Initializes the controller class.
	 * This method is called automatically when the FXML file is loaded.
	 * Sets the text fields to be uneditable (Other than Phone and Email).
	 */
	@FXML
	private void initialize() {
		// Set Uneditable
		this.txtID.setEditable(false);
		this.txtName.setEditable(false);
		this.txtJoinDate.setEditable(false);
		this.txtExDate.setEditable(false);
		this.txtNumBookBorrowed.setEditable(false);
		this.txtNumBookOrdered.setEditable(false);
    }

	/**
	 * Sets the subscriber's details in the text fields.
	 * @param subscriber
	 */
	public void setSubscriber(Subscriber subscriber) {
        this.s = subscriber;
        populateFields();
    }

	/**
	 * Populates the text fields with the subscriber's details.
	 * If the subscriber is null, the text fields will be empty.
	 */
	private void populateFields() {
        if (s != null) {
            this.txtID.setText(String.valueOf(s.getSub_id()));
            this.txtName.setText(s.getSub_name());
            this.txtPNumber.setText(s.getSub_phone_num());
            this.txtEmail.setText(s.getSub_email());
            this.txtJoinDate.setText(s.getSub_joined().format(DATE_FORMATTER));
            this.txtExDate.setText(s.getSub_expiration().format(DATE_FORMATTER));
            this.txtNumBookBorrowed.setText(String.valueOf(s.getCurrentlyBorrowed()));
            this.txtNumBookOrdered.setText(String.valueOf(s.getCurrentlyOrdered()));
        }
    }
    
	/**
	 * Handles the update button click event.
	 * Updates the subscriber's phone number and email.
	 * @param event
	 * @throws Exception
	 * @see #isValidPhoneNumber(String)
	 * @see #isValidEmail(String)
	 * @see #displayMessage(String)
	 */
    public void getSaveBtn(ActionEvent event) throws Exception {
		try {
			MessageUtils.sendMessage(ClientUI.cc, "subscriber", "connect", null);
			if (ClientUI.cc.getConnectionStatusFlag() == 1) {
				s = SharedController.getSubscriber();
				int id = Integer.valueOf(txtID.getText());
				String phoneNumber = txtPNumber.getText();
				String email = txtEmail.getText();

				if (!isValidPhoneNumber(phoneNumber)) {
					displayMessage("Invalid phone number");
					return;
				}

				if (!isValidEmail(email)) {
					displayMessage("Invalid email");
					return;
				}

				if (!phoneNumber.equals(s.getSub_phone_num()) || !email.equals(s.getSub_email())) {
					MessageUtils.sendMessage(ClientUI.cc, "subscriber", "updateSubscriber", id + ":" + phoneNumber + ":" + email);
				} else {
					displayMessage("No changes made!");
				}


			} else {
				displayMessage("No server connection");
				return;
			}
		} catch (NumberFormatException e) {
			displayMessage("Please check your input values!");
		}
	}
    
	/**
	 * Checks if the phone number is valid.
	 * The phone number must be in the format: 05X-XXXXXXX
	 * @param phoneNumber
	 * @return true if the phone number is valid, false otherwise
	 */
    private boolean isValidPhoneNumber(String phoneNumber) {
        String pattern = "^05\\d-\\d{7}$";
		// MessageUtils.sendMessage(ClientUI.cc, "subscriber", "checkUniquePhoneNumber", phoneNumber);
        return phoneNumber.matches(pattern);
    }

	/**
	 * Checks if the email is valid.
	 * The email must be in the format:
	 * - Contains only one '@' character
	 * - Contains at least one '.' character after the '@' character
	 * - Contains at least two characters after the last '.' character
	 * @param email
	 * @return true if the email is valid, false otherwise
	 */
	private boolean isValidEmail(String email) {
		String pattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
		return email.matches(pattern);
	}

	/**
	 * Displays a message to the user of the updated subscriber status.
	 * @param status
	 */
	public void updateSubscriberStatus(String status) {
		displayMessage(status);
	}

	/**
	 * Handles the back button click event.
	 * Redirects the user to the Subscriber Main Frame.
	 * @param event
	 * @throws Exception
	 */
	public void goBackBtn(ActionEvent event) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/SubscriberMainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		primaryStage.setTitle("Subscriber Main Frame");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    MessageUtils.sendMessage(ClientUI.cc, "subscriber", "disconncet", SharedController.getSubscriber().getSub_id());
                    ClientUI.chat.quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
		
		((Node)event.getSource()).getScene().getWindow().hide();
		primaryStage.setResizable(false);
		primaryStage.show();
		
    }

	/**
	 * Displays a message to the user.
	 * @param message
	 */
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }

}
