package server;

import java.io.IOException;
import ocsf.server.ConnectionToClient;
import common.ServerMessage;

public class MessageUtils {
    public static void sendResponseToClient(String messageType, Object data, ConnectionToClient client) {
        ServerMessage response = new ServerMessage(messageType, data);
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