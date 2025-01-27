package common;

import client.ClientController;

/**
 * Utility class for sending messages to the server.
 */
public class MessageUtils {
    /**
     * Sends a message to the server.
     * @param cc - the client controller
     * @param user - the user sending the message (user, librarian, subscriber)
     * @param type - the type of message
     * @param data - the data to send
     */
    public static synchronized void sendMessage(ClientController cc, String user, String type, Object data) {
        ServerMessage msg = new ServerMessage(user, type, data);
        try {
            cc.accept(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
