package gui.controllers;

import client.ClientUI;
import common.Subscriber;
import common.Librarian;
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


/**
 * LoginWindowController class is the controller for the LoginWindow.fxml file.
 * It is responsible for handling user login actions and displaying the appropriate
 * main frame based on the user type.
 */
public class LoginWindowController {

    @FXML private MenuButton menuButton;
    @FXML private TextField idField;
    @FXML private Button loginButton;
    @FXML private Button backButton;

    @FXML private Label messageLabel;

    private String userType;
    private Object userConnected;

    private String getId() { return idField.getText(); }

    /**
     * Initializes the controller class.
     * This method is called automatically when the FXML file is loaded.
     * It is used to initialize the controller and set up the login button.
     * It also sets the controller in the SharedController class.
     * It sets the subscriber, librarian, and book copy to null.
     * It also sets the userType to an empty string.
     */
    @FXML
    public void initialize() {
        setupLogin();
        userType = "";
        // Set the controller in SharedController
        SharedController.setLoginWindowController(this);
        SharedController.setSubscriber(null);
        SharedController.setLibrarian(null);
        SharedController.setBookCopy(null);
    }

    /**
     * Sets up the login button.
     * It sets the action for the login button to handleLoginAction.
     * It also sets the action for the idField to handleLoginAction when the Enter key is pressed.
     */
    private void setupLogin() {
        loginButton.setOnAction(e -> handleLoginAction(e));
        idField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleLoginAction(new ActionEvent(e.getSource(), e.getTarget()));
            }
        });
    }

    /**
     * Handles the login action.
     * It gets the ID from the idField.
     * It sends a message to the server to connect.
     * If the connection is successful, it sends a message to the server to login with the ID.
     * If the user is not found, it displays a message that the user is not found.
     * If the user is a librarian, it sets the userType to "Librarian".
     * If the user is a subscriber, it checks if the subscriber is eligible to login
     * If so it sets the userType to "Subscriber".
     * It then hides the current window and loads and displays the main frame GUI based on the userType.
     * If the connection fails, it displays a message that the connection failed.
     * If an exception occurs, it prints the stack trace.
     * @param event The ActionEvent to handle.
     */
    public void handleLoginAction(ActionEvent event) {
        String idText = getId();
        // Implement your search logic here
        try {
           MessageUtils.sendMessage(ClientUI.cc, "user",  "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                System.out.println("Entered ID: " + idText);
                if (idText.trim().isEmpty()) {
                    displayMessage("Please enter ID"); 
                } else if (idText.length() > 9 || !idText.matches("\\d+")) {
                    displayMessage("Please enter a valid ID number");
                } else {
                    MessageUtils.sendMessage(ClientUI.cc, "user", "login" , Integer.parseInt(idText));
                    if (userConnected == null) {
                        displayMessage("User not found");
                        return;
                    }
                    if (userConnected.equals("User already logged in")) {
                        displayMessage("User already logged in");
                        return;
                    }
                    if ((userConnected instanceof Librarian)) {
                        userType = "Librarian";
                    }
                    if ((userConnected instanceof Subscriber)) {
                        userType = "Subscriber";
                    }
                    System.out.println("User found");
                    ((Node) event.getSource()).getScene().getWindow().hide();
                    // Load and display MainFrame GUI
                    String fxmlPath = String.format("/gui/fxml/%sMainFrame.fxml", userType);
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                    Pane root = loader.load();
                    Stage stage = new Stage();
                    Scene scene = new Scene(root);
                    stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                        try {
                            // print message to console
                            System.out.println("clientui.chat: " + ClientUI.chat);
                            if (ClientUI.chat != null) {
                                MessageUtils.sendMessage(ClientUI.cc, userType,  "disconnect" , Integer.parseInt(idText));
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
                }
            } else {
                displayMessage("Connection failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    /**
     * Handles the back action.
     * It hides the current window and loads and displays the landing window GUI.
     * If an exception occurs, it prints the stack trace.
     * @param event The ActionEvent to handle.
     */
    public void handleBackAction (ActionEvent event) {
        try {
            ((Node) event.getSource()).getScene().getWindow().hide();
            // Load and display MainFrame GUI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LandingWindow.fxml"));
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

    /**
     * Sets the user status.
     * @param user The user status to set.
     */
    public void setUserStatus(Object user) {
        userConnected = user;
    }

    /**
     * Displays a message.
     * @param message The message to display.
     */
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
