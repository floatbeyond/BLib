package server;

import java.sql.Connection;
import gui.ClientConnectedController;

/**
 * The InstanceManager class provides a centralized way to manage and access
 * various instances used throughout the application, such as the server instance,
 * database connection, and controllers.
 */
public class InstanceManager {
    private static EchoServer instance;
    private static ClientConnectedController ccc;
    private static Connection conn;
    private static ReportScheduler reportScheduler;


        
    public static void setInstance(EchoServer serverInstance) { instance = serverInstance; }
    public static EchoServer getInstance() { return instance; }

    public static void setClientConnectedController(ClientConnectedController controller) { ccc = controller; }
    public static ClientConnectedController getClientConnectedController() { return ccc; }

    public static void setDbConnection(Connection connection) { conn = connection; }
    public static Connection getDbConnection() { return conn; }

    public static void setReportScheduler(ReportScheduler scheduler) { reportScheduler = scheduler; }
    public static ReportScheduler getReportScheduler() { return reportScheduler; }
}
