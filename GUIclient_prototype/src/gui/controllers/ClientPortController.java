package gui.controllers;

import client.ClientController;
import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
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

/**
 * This class is the controller for the ClientPort.fxml file. It is responsible for handling the user input and
 * displaying the appropriate messages to the user. It is also responsible for starting the client and connecting to the server.
 * It is also responsible for displaying the main window of the application.
 */
public class ClientPortController  {

	public static ClientController chat; 
	
	@FXML private Button btnExit = null;
	@FXML private Button btnDone = null;

	@FXML private Label messageLabel;
	@FXML private Label lbltxt;
	
	@FXML private TextField iptxt;

	private String getip() { return iptxt.getText(); }
	
	/**
	 * This method is called when the user clicks the Done button.
	 * It is responsible for starting the client and connecting to the server.
	 * It is also responsible for displaying the main window of the application.
	 * @param event
	 * @throws Exception
	 */
	@FXML
	public void Done(ActionEvent event) throws Exception {
		String ip;
		
		ip = getip();
		if (ip.trim().isEmpty()) {
			displayMessage("Must enter ip");
		} else if (ip.matches("^([0-9]{1,3}\\.){3}[0-9]{1,3}$") || ip.equals("localhost")) {
			try {
				System.out.println("IP at Done: " + ip);
				ClientUI clientUI = ClientUI.getInstance();
				clientUI.startClient(ip);
				MessageUtils.sendMessage(ClientUI.cc, "user", "connect", null);
				if (ClientUI.cc.getConnectionStatusFlag() == 1) {
					((Node) event.getSource()).getScene().getWindow().hide();
					// Load and display MainFrame GUI
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LandingWindow.fxml"));
					Pane root = loader.load();
					LandingWindowController landingWindowController = loader.getController();
                    if (landingWindowController == null) {
                        System.out.println("landingWindowController is null after loading FXML");
                    } else {
                        System.out.println("landingWindowController initialized in Show method");
                        SharedController.setLandingWindowController(landingWindowController);
                    }
					Stage stage = new Stage();
					Scene scene = new Scene(root);
					stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            			try {
                			if (ClientUI.chat != null) {
								MessageUtils.sendMessage(ClientUI.cc, "user",  "disconnect" , null);
								ClientUI.chat.quit();
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					});
					stage.setScene(scene);
					stage.setTitle("Library Tool");
					stage.setResizable(false);
					stage.show();
				} else {
					displayMessage("Connection failed");
				}
			} catch (Exception e) {
				e.printStackTrace();
				displayMessage("Error: " + e.getMessage());
			}

		} else {
			displayMessage("Invalid ip format");
		}
	}
	
	/**
	 * This method is called when the user clicks the Exit button.
	 * It is responsible for exiting the application.
	 * @param event
	 * @throws Exception
	 */
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Exiting Subscriber Management Tool");
		System.exit(1);	
	}

	/**
	 * This method is called when the user clicks the Exit button.
	 * It is responsible for exiting the application.
	 * @param event
	 * @throws Exception
	 */
	public void displayMessage(String message) {
        messageLabel.setText(message);
    }
	
}