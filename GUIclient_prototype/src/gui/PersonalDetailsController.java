package gui;

import client.ClientUI;
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
import java.sql.Date;


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

    // ObservableList<String> list;
	
	public void loadSubscriber(Subscriber subscriber) {
		
		this.s = subscriber;
		this.txtID.setText(String.valueOf(s.getSub_id()));
		this.txtID.setEditable(false);
		this.txtName.setText(s.getSub_name());
		this.txtName.setEditable(false);
		this.txtPNumber.setText(s.getSub_phone_num());
		this.txtEmail.setText(s.getSub_email());
        this.txtJoinDate.setText(s.getSub_join_date());
        this.txtExDate.setText(s.getSub_ex_date());
        this.txtNumBookBorrowed.setText(String.valueOf(s.getSub_num_books_borrowed()));
        this.txtNumBookOrdered.setText(String.valueOf(s.getSub_num_books_ordered()));
	}


    public void goBackBtn(ActionEvent event) throws Exception {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberMainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/SubscriberMainFrame.css").toExternalForm());
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
			ClientUI.cc.accept("Connect");
			if (ClientUI.cc.getConnectionStatusFlag() == 1) {
				int id = Integer.valueOf(txtID.getText());
				String name = txtName.getText();
				String phoneNumber = txtPNumber.getText();
				String email = txtEmail.getText();
                Date joinDate = Date.valueOf(txtJoinDate.getText());
                Date exDate = Date.valueOf(txtExDate.getText());
                int numBookBorrowed = Integer.valueOf(txtNumBookBorrowed.getText());
                int numBookOrdered = Integer.valueOf(txtNumBookOrdered.getText());


				if (!isValidPhoneNumber(phoneNumber)) {
					displayMessage("Invalid phone number");
					return;
				}

				if (!isValidEmail(email)) {
					displayMessage("Invalid email");
					return;
				}

				if (!phoneNumber.equals(s.getSub_phone_num()) || !email.equals(s.getSub_email())) {
					s = new Subscriber(id, name, phoneNumber, email, joinDate, exDate);//לא תואם לקונסטרקטור של מנוי צריך להחליט איזה ממבר יש למנוי
					ClientUI.cc.accept("change "+ s.toString());
					System.out.println("ID: "+ id);
					displayMessage("Subscriber updated!");
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
