package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.sql.Date;

import common.BorrowingRecord;
import common.Subscriber;
import ocsf.server.ConnectionToClient;
import gui.ClientConnectedController;


public class Logic {
    private static Connection conn = InstanceManager.getDbConnection();
    private static ClientConnectedController ccc = InstanceManager.getClientConnectedController();
    private static Subscriber s;
    private static BorrowingRecord br;


    // Subscriber

    public static void specificSubscriber(String msg, int subscriberId, ConnectionToClient client) {
        Subscriber subscriber = mysqlConnection.findSubscriber(conn, subscriberId);
        switch (msg) {
            case "send": {
                if (subscriber == null) {
                    MessageUtils.sendResponseToClient("Error", "Subscriber ID Not Found", client);
                } else {
                    MessageUtils.sendResponseToClient("Subscriber", subscriber, client);
                }
                break;
            }
            case "checkStatus": {
                String status = subscriber.getSub_status();
                MessageUtils.sendResponseToClient("FreezeStatus", status, client);
                break;
            }
        }
    }

    public static void updateSubscriberDetails(ConnectionToClient client) {
        int subscriberId = s.getSub_id();
        String phoneNumber = s.getSub_phone_num();
        String email = s.getSub_email();
            
        boolean success = mysqlConnection.updateSubscriber(conn, subscriberId, phoneNumber, email);
        MessageUtils.sendResponseToClient("UpdateStatus", success ? "Subscriber updated successfully." : "ERROR: Couldn't update subscriber.", client);
	}

    public static void showSubscribersTable(ConnectionToClient client) {
        ArrayList<Subscriber> table = mysqlConnection.getSubscribers(conn);
        MessageUtils.sendResponseToClient("SubscriberList", table, client);
    }


    // Books

    public static void addBorrow(ConnectionToClient client) {
        int bookCopyId = br.getCopyId();
        int subscriberId = br.getSubId();
        Date borrowDate = br.getBorrowDate();
        Date expectedReturnDate = br.getExpectedReturnDate();
        boolean success = mysqlConnection.addBorrowingRecord(conn, subscriberId, bookCopyId, borrowDate, expectedReturnDate);
        MessageUtils.sendResponseToClient("BorrowStatus", success ? "Borrow added successfully." : "ERROR: Couldn't add borrow.", client);
    }
















    // Client connection

    public static void connect(ConnectionToClient client) {
        String clientInfo = client.toString();
        String connectionStatus = client.isAlive() ? "Connected" : "Disconnected";
        
        System.out.println("Client info: " + clientInfo);
        ccc.loadClientDetails(clientInfo, connectionStatus);
        MessageUtils.sendResponseToClient("Print", "Client details loaded", client);
    }

    public static void disconnect(ConnectionToClient client) {
        ccc.loadClientDetails("null", "Disconnected");
        MessageUtils.sendResponseToClient("Print", "Server disconnected", client);
    }




}
