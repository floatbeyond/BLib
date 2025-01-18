package common;

import client.ClientController;

public class MessageUtils {
    public static synchronized void sendMessage(ClientController cc, String user, String type, Object data) {
        ServerMessage msg = new ServerMessage(user, type, data);
        try {
            cc.accept(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
