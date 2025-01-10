package common;

import client.ClientController;

public class MessageUtils {
    public static void sendMessage(ClientController cc, String type, Object data) {
        ServerMessage msg = new ServerMessage(type, data);
        try {
            cc.accept(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
