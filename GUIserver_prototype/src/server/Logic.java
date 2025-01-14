package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

import common.BorrowingRecord;
import common.DataLogs;
import common.MessageUtils;
import common.OrderRecord;
import common.Subscriber;
import common.BookCopy;
import ocsf.server.ConnectionToClient;
import gui.ClientConnectedController;


public class Logic {
    private static Connection conn = InstanceManager.getDbConnection();
    private static ClientConnectedController ccc = InstanceManager.getClientConnectedController();
    private static Subscriber s;
    private static BookCopy bc;
    private static BorrowingRecord br;


    // Login

    public static void userLogin(String user, int userId, ConnectionToClient client) {
        Object userInfo = mysqlConnection.userLogin(conn, userId);
        MessageUtils.sendResponseToClient(user, "LoginStatus", userInfo, client);
    }

    // Subscriber

    public static void newSubscriber(String user, Object newSubscriber, ConnectionToClient client) {
        if (newSubscriber instanceof Subscriber) {
            s = (Subscriber) newSubscriber;
            int generatedId = mysqlConnection.addSubscriber(conn, s);
            MessageUtils.sendResponseToClient(user, "NewSubscriber", generatedId, client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Not a subscriber", client);
            return;
        }
    }

    public static void specificSubscriber(String user, int subscriberId, ConnectionToClient client) {
        if ((s = mysqlConnection.findSubscriber(conn, subscriberId)) != null) {
            MessageUtils.sendResponseToClient(user, "foundSubscriber", s, client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Subscriber ID Not Found", client);
        }
    }

    public static void updateSubscriberDetails(String user, Object data, ConnectionToClient client) {
        // int subscriberId = s.getSub_id();
        // String phoneNumber = s.getSub_phone_num();
        // String email = s.getSub_email();

        String subscriberDetails = (String) data;
        String[] parts = subscriberDetails.split(":", 3);
        int subscriberId = Integer.parseInt(parts[0]);
        String phoneNumber = parts[1];
        String email = parts[2];
            
        Object responseFromDB = mysqlConnection.updateSubscriber(conn, subscriberId, phoneNumber, email);
        MessageUtils.sendResponseToClient(user, "UpdateStatus", responseFromDB, client);
	}

    public static void showSubscribersTable(String user, ConnectionToClient client) {
        ArrayList<Subscriber> table = mysqlConnection.getSubscribers(conn);
        MessageUtils.sendResponseToClient(user, "SubscriberList", table, client);
    }
    
     public static void sendDataLogs(String user, int sub_id, ConnectionToClient client) {
        ArrayList<DataLogs> dataLogs = mysqlConnection.getDataLogs(conn, sub_id);
        Platform.runLater(() -> {
            MessageUtils.sendResponseToClient(user, "DataLogsList", dataLogs, client);
        });
        
    }


    // Books

    public static void sendSearchedBooks(String user, Object data, ConnectionToClient client) {
        String searchCriteria = (String) data;
        String[] parts = searchCriteria.split(":", 2);
        String searchType = parts[0];
        String searchText = parts[1];
        List<Object> results = mysqlConnection.searchBooks(conn, searchType, searchText);
        // can you print the results list
        System.out.println(results);
        MessageUtils.sendResponseToClient(user, "BookList", results, client);
    }

    public static void newBorrow(String user, Object newborrow, ConnectionToClient client) {
        if (newborrow instanceof BorrowingRecord) {
            br = (BorrowingRecord) newborrow;
            boolean success = mysqlConnection.addBorrowingRecord(conn, br);
            MessageUtils.sendResponseToClient(user, "BorrowStatus", success ? "Borrow added successfully." : "ERROR: Couldn't add borrow.", client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Invalid borrow record", client);
            return;
        }        
    }

    public static void newOrder(String user, Object newOrder, ConnectionToClient client) {
        if (newOrder instanceof OrderRecord) {
            OrderRecord order = (OrderRecord) newOrder;
            boolean orderExists = mysqlConnection.isOrderExists(conn, order.getSubId(), order.getBookId());
            if (orderExists) {
                MessageUtils.sendResponseToClient(user, "OrderStatus", "ERROR: Order already exists for this book.", client);
            } else {
                boolean success = mysqlConnection.addOrderRecord(conn, order);
                MessageUtils.sendResponseToClient(user, "OrderStatus", success ? "Order added successfully." : "ERROR: Couldn't add order.", client);
            }
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Invalid order record", client);
            return;
        }
    }
    // Scan

    public static void scan(String user, String msg, int unparsedId, ConnectionToClient client) {
        if ((bc = mysqlConnection.findBookCopy(conn, unparsedId)) != null) {
            MessageUtils.sendResponseToClient(user, "BookCopy", bc, client);
        } else if ((s = mysqlConnection.findSubscriber(conn, unparsedId)) != null) {
            MessageUtils.sendResponseToClient(user, "foundSubscriber", s, client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Scan failed", client);
        }
    }














    // Client connection

    public static void connect(String user, ConnectionToClient client) {
        String clientInfo = client.toString();
        String connectionStatus = client.isAlive() ? "Connected" : "Disconnected";
        
        System.out.println("Client info: " + clientInfo);
        ccc.loadClientDetails(clientInfo, connectionStatus);
        MessageUtils.sendResponseToClient(user, "Print", "Client details loaded", client);
    }

    public static void disconnect(String user, ConnectionToClient client) {
        ccc.loadClientDetails("null", "Disconnected");
        MessageUtils.sendResponseToClient(user, "Print", "Server disconnected", client);
    }
   
}

