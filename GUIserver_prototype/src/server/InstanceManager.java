package server;

import java.sql.Connection;
import gui.ClientConnectedController;

public class InstanceManager {
    private static EchoServer instance;
    private static ClientConnectedController ccc;
    private static Connection conn;

    // Setters and getters
    
    public static void setInstance(EchoServer serverInstance) { instance = serverInstance; }
    public static EchoServer getInstance() { return instance; }

    public static void setClientConnectedController(ClientConnectedController controller) { ccc = controller; }
    public static ClientConnectedController getClientConnectedController() { return ccc; }

    public static void setDbConnection(Connection connection) { conn = connection; }
    public static Connection getDbConnection() { return conn; }
}
