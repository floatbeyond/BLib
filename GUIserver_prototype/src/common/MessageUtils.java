package common;

import java.io.IOException;
import ocsf.server.ConnectionToClient;

public class MessageUtils {
    public static synchronized void sendResponseToClient(String user, String type, Object data, ConnectionToClient client) {
        try {
            if (client != null && client.isAlive()) {
                ServerMessage message = new ServerMessage(user, type, data);
                client.sendToClient(message);
            } else {
                System.out.println("Client connection is not alive or null");
            }
        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
            try {
                client.close();
            } catch (IOException ex) {
                System.out.println("Error closing client connection: " + ex.getMessage());
            }
        }
    }
}