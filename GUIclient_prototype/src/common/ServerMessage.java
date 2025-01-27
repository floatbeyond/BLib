package common;

import java.io.Serializable;

/**
 * Represents a message from the server to the client.
 * Implements Serializable for client-server communication.
 */
public class ServerMessage implements Serializable {
    /** Ensures consistent serialization across different versions */
    private static final long serialVersionUID = 1L;

    private String user;
    private String type;
    private Object data;

    /**
     * Constructor for ServerMessage
     * @param user
     * @param type
     * @param data
     */
    public ServerMessage(String user, String type, Object data)  {
        this.user = user;
        this.type = type;
        this.data = data;
    }

    // Getters
    public String getUser() { return user; }
    public String getType() { return type; }
    public Object getData() { return data; }
}
