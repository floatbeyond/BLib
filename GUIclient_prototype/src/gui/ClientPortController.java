package gui;

import client.ClientController;
import client.ClientUI;
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


public class ClientPortController  {

	public static ClientController chat; 
	
	@FXML private Button btnExit = null;
	@FXML private Button btnDone = null;

	@FXML private Label messageLabel;
	@FXML private Label lbltxt;
	
	@FXML private TextField iptxt;

	private String getip() {
		return iptxt.getText();
	}
	
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
				ClientUI.cc.accept("connect");
				if (ClientUI.cc.getConnectionStatusFlag() == 1) {
					((Node) event.getSource()).getScene().getWindow().hide();
					// Load and display MainFrame GUI
					FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/MainFrame.fxml"));
					Pane root = loader.load();
					Stage stage = new Stage();
					Scene scene = new Scene(root);
					stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            			try {
							// print message to console
							System.out.println("clientui.chat: " + ClientUI.chat);
                			if (ClientUI.chat != null) {
								ClientUI.cc.accept("disconnect");
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
	
	public void getExitBtn(ActionEvent event) throws Exception {
		System.out.println("Exiting Subscriber Management Tool");
		System.exit(1);	
	}

	public void displayMessage(String message) {
        messageLabel.setText(message);
    }
	
}