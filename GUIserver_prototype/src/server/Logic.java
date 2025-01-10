package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.Date;
import java.util.List;

import common.BorrowingRecord;
import common.Subscriber;
import common.DataLogs; 
import ocsf.server.ConnectionToClient;
import gui.ClientConnectedController;


public class Logic {
    private static Connection conn = InstanceManager.getDbConnection();
    private static ClientConnectedController ccc = InstanceManager.getClientConnectedController();
    private static Subscriber s;
    private static BorrowingRecord br;


    // Login

    public static void userLogin(String user, int userId, ConnectionToClient client) {
        Object userInfo = mysqlConnection.userLogin(conn, userId);
        MessageUtils.sendResponseToClient(user, "LoginStatus", userInfo, client);
    }

    // Subscriber

    public static void specificSubscriber(String user, int subscriberId, ConnectionToClient client) {
        Subscriber subscriber = mysqlConnection.findSubscriber(conn, subscriberId);
        if (subscriber == null) {
            MessageUtils.sendResponseToClient(user, "Error", "Subscriber ID Not Found", client);
        } else {
            MessageUtils.sendResponseToClient(user, "Subscriber", subscriber, client);
        }
    }

    public static void updateSubscriberDetails(String user, ConnectionToClient client) {
        int subscriberId = s.getSub_id();
        String phoneNumber = s.getSub_phone_num();
        String email = s.getSub_email();
            
        boolean success = mysqlConnection.updateSubscriber(conn, subscriberId, phoneNumber, email);
        MessageUtils.sendResponseToClient(user, "UpdateStatus", success ? "Subscriber updated!" : "ERROR: Couldn't update subscriber", client);
	}

    public static void showSubscribersTable(String user, ConnectionToClient client) {
        ArrayList<Subscriber> table = mysqlConnection.getSubscribers(conn);
        MessageUtils.sendResponseToClient(user, "SubscriberList", table, client);
    }
    
     public static void showDataLogs(ConnectionToClient client) {
        ArrayList<DataLogs> dataLogs = mysqlConnection.getDataLogs(conn);
        MessageUtils.sendResponseToClient("DataLogsList", dataLogs, client);
    }


    // Books

    public static void sendSearchedBooks(String user, String searchType, String searchText, ConnectionToClient client) {
        List<Object> results = mysqlConnection.searchBooks(conn, searchType, searchText);
        // can you print the results list
        System.out.println(results);
        MessageUtils.sendResponseToClient(user, "BookList", results, client);
    }

    public static void addBorrow(String user, ConnectionToClient client) {
        int bookCopyId = br.getCopyId();
        int subscriberId = br.getSubId();
        Date borrowDate = br.getBorrowDate();
        Date expectedReturnDate = br.getExpectedReturnDate();
        boolean success = mysqlConnection.addBorrowingRecord(conn, subscriberId, bookCopyId, borrowDate, expectedReturnDate);
        MessageUtils.sendResponseToClient(user, "BorrowStatus", success ? "Borrow added successfully." : "ERROR: Couldn't add borrow.", client);
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




}
