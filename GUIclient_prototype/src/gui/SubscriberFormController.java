package gui;

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


public class SubscriberFormController {
	private Subscriber s;
	
	@FXML private Button btnBack = null;
	@FXML private Button btnSave = null;

	@FXML private Label messageLabel;
	@FXML private Label lblID;
	@FXML private Label lblName;
	@FXML private Label lblStatus;
	@FXML private Label lblPNumber;
	@FXML private Label lblEmail;
	
	@FXML private TextField txtID;
	@FXML private TextField txtName;
	@FXML private TextField txtStatus;
	@FXML private TextField txtPNumber;
	@FXML private TextField txtEmail;
	
	// ObservableList<String> list;
	
	public void loadSubscriber(Subscriber subscriber) {
		
		this.s = subscriber;
		this.txtID.setText(String.valueOf(s.getSub_id()));
		this.txtID.setEditable(false);
		this.txtName.setText(s.getSub_name());
		this.txtName.setEditable(false);
		this.txtStatus.setText(s.getSub_status());	
		this.txtStatus.setEditable(false);
		this.txtPNumber.setText(s.getSub_phone_num());
		this.txtEmail.setText(s.getSub_email());
	}

	public void goBackBtn(ActionEvent event) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/MainFrame.css").toExternalForm());
		primaryStage.setTitle("Library Tool");
		primaryStage.setScene(scene);
		primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    ClientUI.cc.accept("disconnect");
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
	
	public void getSaveBtn(ActionEvent event) throws Exception {
		try {
			// ClientUI.cc.accept("connect");
			MessageUtils.sendMessage(ClientUI.cc, "connect", null);
			if (ClientUI.cc.getConnectionStatusFlag() == 1) {
				int id = Integer.valueOf(txtID.getText());
				String name = txtName.getText();
				String status = txtStatus.getText();
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
					s = new Subscriber(id, name, status, phoneNumber, email, s.getSub_penalties(), s.getSub_freeze(), s.getSub_joined(), s.getSub_expiration());
					// ClientUI.cc.accept("updateSubscriber " + s);
					MessageUtils.sendMessage(ClientUI.cc, "updateSubscriber", s);
					System.out.println("ID: "+ id);
					// displayMessage("Subscriber updated!");
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
