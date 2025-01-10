package common;

import java.io.Serializable;

public class ServerMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String user;
    private String type;
    private Object data;

    public ServerMessage(String user, String type, Object data)  {
        this.user = user;
        this.type = type;
        this.data = data;
    }

    public String getUser() { return user; }
    public String getType() { return type; }
    public Object getData() { return data; }
}
