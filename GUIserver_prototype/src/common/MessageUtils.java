package common;

import java.io.IOException;
import ocsf.server.ConnectionToClient;


/**
 * Utility class for sending messages to clients.
 */
public class MessageUtils {

    /**
     * Sends a response message to the client.
     * @param user the user who initiated the request
     * @param type the type of response (or keyword)
     * @param data the data to send
     * @param client the client to send the response to
     */
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