package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import java.time.LocalDate;

import common.Notification;
import common.BorrowingRecord;
import common.DataLogs;
import common.MessageUtils;
import common.Subscriber;
import common.Book;
import common.BookCopy;
import common.BorrowRecordDTO;
import common.OrderRecordDTO;
import ocsf.server.ConnectionToClient;
import gui.ClientConnectedController;
import common.OrderResponse;


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

    // Notifications

    public static void fetchNotifications(String user, Object data, ConnectionToClient client) {
        int subId = (int) data;
        List<Notification> notifications = mysqlConnection.getNewNotifications(conn, subId);
        MessageUtils.sendResponseToClient(user, "NewNotifications", notifications, client);
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

    public static void sendUserBorrows(String user, Object data, ConnectionToClient client) {
        int subId = (int) data;
        List<BorrowRecordDTO> borrows = mysqlConnection.getUserBorrows(conn, subId);
        MessageUtils.sendResponseToClient(user, "UserBorrowsList", borrows, client);
    }

    public static void sendUserOrders(String user, Object data, ConnectionToClient client) {
        int subId = (int) data;
        List<OrderRecordDTO> orders = mysqlConnection.getUserOrders(conn, user, subId);
        // print list of orders being sent
        System.out.println(orders);
        MessageUtils.sendResponseToClient(user, "UserOrdersList", orders, client);
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

    public static void returnBook(String user, Object data, ConnectionToClient client) {
        String[] parts = ((String) data).split(":", 3);
        int subId = Integer.parseInt(parts[0]);
        int copyId = Integer.parseInt(parts[1]);
        LocalDate returnDate = LocalDate.parse(parts[2]);
        boolean success = mysqlConnection.returnBook(conn, subId, copyId, returnDate);
        if (success) {
            mysqlConnection.sendOrderNotification(conn, copyId);
        }
        MessageUtils.sendResponseToClient(user, "ReturnStatus", success ? "Book has been returned successfully" : "ERROR: Book does not match subscriber", client);
    }

    public static void newOrder(String user, Object newOrder, ConnectionToClient client) {
        String[] parts = ((String) newOrder).split(":", 2);
        int bookId = Integer.parseInt(parts[0]);
        int subId = Integer.parseInt(parts[1]);
        String orderStatus = "Order already exists";
        Book orderedBook = null;

        boolean orderExists = mysqlConnection.isOrderExists(conn, bookId, subId);
        if (!orderExists) {
            boolean areOrdersCapped = mysqlConnection.areOrdersCapped(conn, bookId);
            if (!areOrdersCapped) {
                boolean success = mysqlConnection.addOrderRecord(conn, bookId, subId);
                if (success == true) {
                    orderStatus = "Order added successfuly";
                    orderedBook = mysqlConnection.getBookById(conn, bookId);
                    orderedBook.setOrdered(true);
                } else orderStatus = "Failed to add order";
            } else orderStatus = "Orders are at their cap";
        }
        OrderResponse response = new OrderResponse(orderStatus, orderedBook);
        MessageUtils.sendResponseToClient(user, "OrderStatus", response, client);
    }

    public static void cancelOrder(String user, Object order, ConnectionToClient client) {
        int orderId = (int) order;
        // print order id
        System.out.println("Order ID: " + orderId);
        boolean success = mysqlConnection.cancelOrder(conn, orderId);
        MessageUtils.sendResponseToClient(user, "CancelStatus", success ? "Order has been cancelled" : "ERROR: Order not found", client);
    }

    public static void checkOrder(String user, Object data, ConnectionToClient client) {
        String[] parts = ((String) data).split(":", 4);
        int subId = Integer.parseInt(parts[0]);
        int borrowId = Integer.parseInt(parts[1]);
        LocalDate extensionDate = LocalDate.parse(parts[2]);
        int bookId = mysqlConnection.getBookIdByBorrowId(conn, borrowId);
        String libName = parts[3];
        boolean success = false;
        boolean orderExists = mysqlConnection.anyOrderExists(conn, bookId);
        if (orderExists == false) {
            success = mysqlConnection.extendBorrow(conn, subId, borrowId, extensionDate);
            if (success == true) {
                mysqlConnection.logExtensionByLibrarian(conn, subId, bookId, libName);
            }
        }
        MessageUtils.sendResponseToClient(user, "ExtendStatus" , success ? "Borrowing extended" : "Cant extend, someone ordered the book", client);
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

