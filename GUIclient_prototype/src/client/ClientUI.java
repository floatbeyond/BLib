package client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import common.MessageUtils;
import gui.controllers.ClientPortController;

/**
 * ClientUI class is responsible for starting the client side of the application.
 * This class is responsible for setting up the client connection and starting the client.
 */

public class ClientUI extends Application {
	public static ClientController cc; 
    public static ChatClient chat;
    private static ClientUI instance;

    /**
     * Main method to start the client side of the application.
     * 
     * @param args
     * @throws Exception
     */
	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   }
	 
    /**
     * The start method initializes the primary stage and starts the client port controller.
     *
     * @param primaryStage the initial stage for this application
     * @throws Exception if an error occurs during initialization
     */
	@Override
	public void start(Stage primaryStage) throws Exception {
        instance = this;
		try {
            // Load FXML and get controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ClientPort.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            ClientPortController clientPortController = loader.getController();
            SharedController.setClientPortController(clientPortController);
            primaryStage.setOnCloseRequest(new javafx.event.EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    try {
                        // print cc and chat
                        System.out.println("cc: " + cc);
                        System.out.println("chat: " + chat);
                        if (chat != null) {
                            // print cc
                            System.out.println("cc: " + cc);
                            MessageUtils.sendMessage(cc, "user", "disconnect", null);
                            chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            primaryStage.setTitle("BLib");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

    /**
     * Starts the client with the specified IP address.
     * 
     * @param ip the IP address of the client
     */
    public void startClient(String ip) {
        try {
            // Initialize chat client
            cc = new ClientController(ip, 5555);
            chat = cc.getClient();
            // print ip
            System.out.println("IP: " + ip);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Returns the instance of the client UI.
     * 
     * @return the instance of the client UI
     */
    public static ClientUI getInstance() {
        if (instance == null) {
            instance = new ClientUI();
        }
        return instance;
    }
}
