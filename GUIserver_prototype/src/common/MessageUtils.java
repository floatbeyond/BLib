package common;

import java.io.IOException;
import ocsf.server.ConnectionToClient;

public class MessageUtils {
    public static void sendResponseToClient(String user, String messageType, Object data, ConnectionToClient client) {
        ServerMessage response = new ServerMessage(user, messageType, data);
        try {
            // print message being sent and data
            System.out.println("Sending message type: " + messageType);
            System.out.println("Sending data: " + data);
            client.sendToClient(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}