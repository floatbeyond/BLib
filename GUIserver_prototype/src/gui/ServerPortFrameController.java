package gui;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.InstanceManager;
import server.ServerUI;

public class ServerPortFrameController  {
	
	@FXML private Button btnExit = null;
	@FXML private Button btnDone = null;

	@FXML private Label messageLabel;
	@FXML private Label lbltxt;
	
	@FXML private TextField portxt;

	
	private String getport() {
		return portxt.getText();			
	}
	
	public void Done(ActionEvent event) throws Exception {
		String p;
		
		p = getport();
		if (p.trim().isEmpty()) {
			displayMessage("Must enter port");
		} else if (p.equals("5555")) {
			((Node)event.getSource()).getScene().getWindow().hide(); //hiding primary window
			ServerUI.runServer(p);  // Start the server
			// Load and display the SQL connection GUI
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientConnected.fxml"));
			Parent root = loader.load();
			ClientConnectedController controller = loader.getController();
            InstanceManager.setClientConnectedController(controller);
			Stage stage = new Stage();
			Scene scene = new Scene(root);
			stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
				try {
					InstanceManager.getInstance().close();
					InstanceManager.getInstance().stopListening();
					InstanceManager.getReportScheduler().stopScheduler();
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
			stage.setScene(scene);
			stage.setTitle("Client Status");
	        stage.setResizable(false);
			stage.show();
		} else {
			displayMessage("Invalid port");
		}
	}
	
	public void getExitBtn(ActionEvent event) throws Exception {
		// System.out.println("Exit server UI");
		// System.exit(1);	
		// EchoServer.getInstance().close();
        ((Node)event.getSource()).getScene().getWindow().hide();
        // Load and display ServerPort GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SqlConnection.fxml"));
        Pane root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("SQL Connector");
        stage.setResizable(false);
        stage.show();
	}

	public void displayMessage(String message) {
        messageLabel.setText(message);
    }
	
}