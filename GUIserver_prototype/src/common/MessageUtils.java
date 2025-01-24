package common;

import java.io.IOException;
import ocsf.server.ConnectionToClient;

// public class MessageUtils {
//     public static synchronized void sendResponseToClient(String user, String messageType, Object data, ConnectionToClient client) {
//         ServerMessage response = new ServerMessage(user, messageType, data);
//         try {
//             // print message being sent and data
//             System.out.println("Sending message type: " + messageType);
//             System.out.println("Sending data: " + data);
//             client.sendToClient(response);
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
//     }
// }

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