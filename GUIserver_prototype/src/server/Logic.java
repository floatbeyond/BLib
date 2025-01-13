package server;

import java.sql.Connection;
import java.util.ArrayList;
<<<<<<< HEAD
import java.sql.Date;
import java.util.List;

import common.BorrowingRecord;
import common.Subscriber;
import common.DataLogs; 
=======
import java.util.List;

import common.BorrowingRecord;
import common.MessageUtils;
import common.Subscriber;
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
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

<<<<<<< HEAD
    public static void specificSubscriber(String user, int subscriberId, ConnectionToClient client) {
        if ((s = mysqlConnection.findSubscriber(conn, subscriberId)) != null) {
            MessageUtils.sendResponseToClient(user, "Subscriber", s, client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Subscriber ID Not Found", client);
        }
    }

=======
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

>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
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
<<<<<<< HEAD
    }
    
     public static void showDataLogs(String user, ConnectionToClient client) {
        ArrayList<DataLogs> dataLogs = mysqlConnection.getDataLogs(conn);
        MessageUtils.sendResponseToClient(user, "DataLogsList", dataLogs, client);
=======
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
    }


    // Books

    public static void sendSearchedBooks(String user, String searchType, String searchText, ConnectionToClient client) {
        List<Object> results = mysqlConnection.searchBooks(conn, searchType, searchText);
        // can you print the results list
        System.out.println(results);
        MessageUtils.sendResponseToClient(user, "BookList", results, client);
    }

<<<<<<< HEAD
    public static void addBorrow(String user, ConnectionToClient client) {
        int bookCopyId = br.getCopyId();
        int subscriberId = br.getSubId();
        Date borrowDate = br.getBorrowDate();
        Date expectedReturnDate = br.getExpectedReturnDate();
        boolean success = mysqlConnection.addBorrowingRecord(conn, subscriberId, bookCopyId, borrowDate, expectedReturnDate);
        MessageUtils.sendResponseToClient(user, "BorrowStatus", success ? "Borrow added successfully." : "ERROR: Couldn't add borrow.", client);
=======
    public static void newBorrow(String user, Object newborrow, ConnectionToClient client) {
        if (newborrow instanceof BorrowingRecord) {
            br = (BorrowingRecord) newborrow;
            boolean success = mysqlConnection.addBorrowingRecord(conn, br);
            MessageUtils.sendResponseToClient(user, "BorrowStatus", success ? "Borrow added successfully." : "ERROR: Couldn't add borrow.", client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Invalid borrow record", client);
            return;
        }        
>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
    }

    // Scan

    public static void scan(String user, String msg, int unparsedId, ConnectionToClient client) {
        if ((bc = mysqlConnection.findBookCopy(conn, unparsedId)) != null) {
            MessageUtils.sendResponseToClient(user, "BookCopy", bc, client);
        } else if ((s = mysqlConnection.findSubscriber(conn, unparsedId)) != null) {
            MessageUtils.sendResponseToClient(user, "Subscriber", s, client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Scan failed", client);
        }
    }


<<<<<<< HEAD
=======












>>>>>>> a68519a24bda2fd6c6f267c713be6df0ee79f84b
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

