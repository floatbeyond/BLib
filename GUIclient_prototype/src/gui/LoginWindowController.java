package gui;

import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;



public class LoginWindowController {

    @FXML private MenuButton menuButton;
    @FXML private TextField idField;
    @FXML private Button loginButton;
    @FXML private Button backButton;

    @FXML private Label messageLabel;

    public String userType;

    private String getId() {
        return idField.getText();
    }

    @FXML
    public void initialize() {
        setupLogin();
        // Set the controller in SharedController
        SharedController.setLoginWindowController(this);
    }

    private void setupLogin() {
        loginButton.setOnAction(e -> handleLoginAction(e));
        idField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLoginAction(null);
            }
        });
    }

    public void handleLoginAction(ActionEvent event) {
        String idText = getId();
        // Implement your search logic here
        try {
           MessageUtils.sendMessage(ClientUI.cc, "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                System.out.println("Entered ID: " + idText);
                if (idText.trim().isEmpty()) {
                    displayMessage("Please enter ID"); 
                } else if (idText.length() > 9 || !idText.matches("\\d+")) {
                    displayMessage("Please enter a valid ID number");
                } else { 
                    MessageUtils.sendMessage(ClientUI.cc, "login" , Integer.parseInt(idText));
                    if (userType == "NotFound") {
                        displayMessage("User not found");
                    } else {
                        System.out.println("User found");
                        if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                            ((Node) event.getSource()).getScene().getWindow().hide();
                            // Load and display MainFrame GUI
                            String fxmlPath = String.format("/gui/%sMainFrame.fxml", userType);
                            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                            Pane root = loader.load();
                            Stage stage = new Stage();
                            Scene scene = new Scene(root);
                            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                                try {
                                    // print message to console
                                    System.out.println("clientui.chat: " + ClientUI.chat);
                                    if (ClientUI.chat != null) {
                                        ClientUI.chat.quit();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            });
                            stage.setScene(scene);
                            String windowTitle = userType + " Main Frame";
                            stage.setTitle(windowTitle);
                            stage.setResizable(false);
                            stage.show();
                        } else {
                            displayMessage("Connection failed");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public void handleBackAction (ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            // Load and display MainFrame GUI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/LandingWindow.fxml"));
            Pane root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Landing Window");
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }


    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
