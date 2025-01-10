package client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import common.MessageUtils;
import gui.ClientPortController;


public class ClientUI extends Application {
	public static ClientController cc; 
    public static ChatClient chat;
    private static ClientUI instance;

	public static void main( String args[] ) throws Exception
	   { 
		    launch(args);  
	   }
	 
	@Override
	public void start(Stage primaryStage) throws Exception {
        instance = this;
		try {
            // Load FXML and get controller
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/ClientPort.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            ClientPortController clientPortController = loader.getController();
            SharedController.setClientPortController(clientPortController);
            scene.getStylesheets().add(getClass().getResource("/gui/ClientPort.css").toExternalForm());
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
                            MessageUtils.sendMessage(cc, "disconnect", null);
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
    
    public static ClientUI getInstance() {
        if (instance == null) {
            instance = new ClientUI();
        }
        return instance;
    }
}
