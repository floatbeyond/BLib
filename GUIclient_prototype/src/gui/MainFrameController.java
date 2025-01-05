package gui;

import client.ChatClient;
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

public class MainFrameController {
    
    @FXML private Button btnDisconnect = null;
    @FXML private Button btnDetails = null;
    @FXML private Button btnShow = null;
	@FXML private Button btnSend = null;

    @FXML private Label messageLabel;

	@FXML private TextField idtxt;

    private String getID() {
		return idtxt.getText();
	}

	public void Send(ActionEvent event) throws Exception {
		try {
            ClientUI.cc.accept("details");
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                String id = getID();
                System.out.println("ID entered: " + id);
                if (id.trim().isEmpty()) {
                    System.out.println("Must enter an id number");
                    displayMessage("Must enter an id number");
                } else if (!id.matches("\\d+")) {
                    displayMessage("ID must be numbers");
                } else if (id.length() > 9) {
                    displayMessage("ID number too long");
                } else {
                    System.out.println("Calling ClientUI.cc.accept");
                    ClientUI.cc.accept("send " + id);
                    Subscriber foundSubscriber = ChatClient.s1;
                    if(foundSubscriber == null) {
                        System.out.println("Subscriber ID Not Found");
                        displayMessage("Subscriber ID Not Found");
                    } else {
                        System.out.println("Subscriber ID Found");
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscriberForm.fxml"));
                        if (loader.getLocation() == null) {
                            throw new IllegalArgumentException("FXML file not found");
                        }
                        System.out.println("Updating UI");
                        ((Node) event.getSource()).getScene().getWindow().hide(); 
                        Stage primaryStage = new Stage();
                        Pane root = loader.load();
                        SubscriberFormController subscriberFormController = loader.getController();
                        if (subscriberFormController == null) {
                            System.out.println("subscriberFormController is null after loading FXML");
                        } else {
                            System.out.println("subscriberFormController initialized in Send method");
                            subscriberFormController.loadSubscriber(foundSubscriber);
                        }
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/gui/SubscriberForm.css").toExternalForm());
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
                        primaryStage.setTitle("Subscriber Management Tool");
                        primaryStage.setScene(scene);
                        primaryStage.setResizable(false);
                        primaryStage.show();
                    }
                }
	        } else {
                System.out.println("No server connection");
                displayMessage("No server connection");
                return;
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

    public void Show(ActionEvent event) throws Exception {
		try {
            ClientUI.cc.accept("details");
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/SubscribersTableForm.fxml"));
                if (loader.getLocation() == null) {
                    throw new IllegalArgumentException("FXML file not found");
                }
                System.out.println("FXML file found");
                System.out.println("Updating UI: Switching to subscriber table");
                ((Node) event.getSource()).getScene().getWindow().hide(); 
                Stage primaryStage = new Stage();
                Pane root = loader.load();
                SubscribersTableController subscribersTableController = loader.getController();
                if (subscribersTableController == null) {
                    System.out.println("subscribersTableController is null after loading FXML");
                } else {
                    System.out.println("subscribersTableController initialized in Show method");
                    ChatClient.setSubscribersTableController(subscribersTableController);
                }
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/gui/SubscribersTableForm.css").toExternalForm());
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
                primaryStage.setTitle("Subscribers Table");
                primaryStage.setScene(scene);
                primaryStage.setResizable(false);
                primaryStage.show();
            } else {
                System.out.println("No server connection");
                displayMessage("No server connection");
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void disconnectBtn(ActionEvent event) throws Exception {
		System.out.println("Exiting Mentormatch");
        ClientUI.cc.accept("disconnect");	
		//System.exit(1);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientPort.fxml"));
		Pane root = loader.load();
		ClientPortController clientPortController = loader.getController();
        ChatClient.setClientPortController(clientPortController);
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		scene.getStylesheets().add(getClass().getResource("/gui/ClientPort.css").toExternalForm());
		primaryStage.setTitle("Client");
		primaryStage.setScene(scene);
		
		((Node)event.getSource()).getScene().getWindow().hide();
		primaryStage.setResizable(false);
		primaryStage.show();
	}

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }

}