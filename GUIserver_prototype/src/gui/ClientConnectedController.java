package gui;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import server.InstanceManager;
import javafx.scene.control.Button;

/**
 * Controller for the ClientConnected GUI.
 * This class is responsible for displaying the connected client (debugging purposes).
 */
public class ClientConnectedController {

    @FXML private Button backBtn;
    @FXML private Label infoLabel;
    @FXML private Label statusLabel;

    private Stage stage;

    /**
     * Initializes the ClientConnected GUI.
     * Sets the infoLabel and statusLabel to "null" and "Disconnected" respectively.
     */
    @FXML
    public void initialize() {
        infoLabel.setText("null");
        statusLabel.setText("Disconnected");
        

    }

    /**
     * Sets the stage for the ClientConnected GUI.
     * Forces the stage to close the connection and stop listening when the window is closed.
     * @param stage
     */
    public void setStage(Stage stage) {
        this.stage = stage;
        this.stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                InstanceManager.getInstance().close();
                InstanceManager.getInstance().stopListening();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Loads the client details onto the GUI.
     * @param info
     * @param status
     */
    public void loadClientDetails(String info, String status) {
        // print client info and connection status
        System.out.println("LOADCLIENTDETAILS: Client info: " + info);
        System.out.println("LOADCLIENTDETAILS: Connection status: " + status);
        Platform.runLater(() -> {
            infoLabel.setText(info);
            statusLabel.setText(status);

            if ("Connected".equals(status)) {
                statusLabel.setStyle("-fx-text-fill: green;");
            } else if ("Disconnected".equals(status)) {
                statusLabel.setStyle("-fx-text-fill: red;");
            } else {
                statusLabel.setStyle("-fx-text-fill: black;");
            }
        });        
    }

    /**
     * Handles the back button event.
     * Closes the connection, stops listening, and returns to the ServerPort GUI.
     * @param event
     * @throws Exception
     */
    public void disconnectBtn(ActionEvent event) throws Exception {
        InstanceManager.getInstance().close();
        InstanceManager.getInstance().stopListening();
        InstanceManager.getReportScheduler().stopScheduler();
        ((Node)event.getSource()).getScene().getWindow().hide();
        // Load and display ServerPort GUI
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ServerPort.fxml"));
        Pane root = loader.load();
        Stage stage = new Stage();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Server");
        stage.setResizable(false);
        stage.show();
    }

    

    
}
