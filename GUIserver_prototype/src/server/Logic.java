package server;

import java.sql.Connection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import common.BorrowingRecord;
import common.MessageUtils;
import common.Subscriber;
import common.BookCopy;
import common.BorrowingRecord;
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

    public static void returnSubscriber(String user, int bookId, int subscriberId, ConnectionToClient client){
        BorrowingRecord book= mysqlConnection.returnSubscriber(conn, bookId, subscriberId);
        if (book == null) {
            MessageUtils.sendResponseToClient(user, "Error", "Not Found", client);
        } else {
            MessageUtils.sendResponseToClient(user, "Return", book, client);
        }
    }

    public static void updateActualReturnDate(String user, int bookId, int subscriberId, Date actualReturnDate, ConnectionToClient client){
        boolean success = mysqlConnection.updateActualReturnDate(conn, subscriberId, bookId, actualReturnDate);
        MessageUtils.sendResponseToClient(user, "UpdateStatus", success ? "actualReturnDate updated successfully." : "ERROR: Couldn't update actualReturnDate.", client);
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

    public static void freezeSubscriber(String user, int subscriberId, ConnectionToClient client) {
        boolean success = mysqlConnection.freezeSubscriber(conn, subscriberId);
        MessageUtils.sendResponseToClient(user, "FreezeStatus", success ? "Subscriber frozen!" : "ERROR: Couldn't freeze subscriber", client);
    }
    public static void returnBook(String user, int copyId, ConnectionToClient client) {
        boolean success = mysqlConnection.returnBook(conn, copyId);
        MessageUtils.sendResponseToClient(user, "ReturnStatus", success ? "Book returned!" : "ERROR: Couldn't return book", client);
    }


    // Books

    public static void sendSearchedBooks(String user, String searchType, String searchText, ConnectionToClient client) {
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
