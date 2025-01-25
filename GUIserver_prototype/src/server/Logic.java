package server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.mysql.cj.protocol.Message;

import javafx.application.Platform;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import common.OrderRecord;
import common.Notification;
import common.BorrowingRecord;
import common.DataLogs;
import common.MessageUtils;
import common.Subscriber;
import common.SubscriberReport;
import common.SubscriberStatusReport;
import common.Book;
import common.BookCopy;
import common.BorrowRecordDTO;
import common.BorrowReport;
import common.BorrowTimeReport;
import common.OrderRecordDTO;
import ocsf.server.ConnectionToClient;
import gui.ClientConnectedController;
import common.OrderResponse;


public class Logic {
    private static final Logger logger = Logger.getLogger(Logic.class.getName());
    private static Connection conn = InstanceManager.getDbConnection();
    private static ClientConnectedController ccc;
    private static Subscriber s;
    private static BookCopy bc;
    private static BorrowingRecord br;
    private static List<BorrowTimeReport> borrowTimesReport;
    private static List<SubscriberStatusReport> subscriberStatusReport;


    // Login

    public static void userLogin(String user, int userId, ConnectionToClient client) {
        Object userInfo = mysqlConnection.userLogin(conn, userId);
        MessageUtils.sendResponseToClient(user, "LoginStatus", userInfo, client);
    }

    // Notifications

    public static void fetchNotifications(String user, Object data, ConnectionToClient client) {
        int sentId = (int) data;
        List<Notification> notifications = mysqlConnection.getNewNotifications(conn, sentId, user);
        try {
            MessageUtils.sendResponseToClient(user, "NewNotifications", notifications, client);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error serializing notifications: " + e.getMessage());
        }
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
        String subscriberDetails = (String) data;
        String[] parts = subscriberDetails.split(":", 3);
        int subscriberId = Integer.parseInt(parts[0]);
        String phoneNumber = parts[1];
        String email = parts[2];
            
        Object responseFromDB = mysqlConnection.updateSubscriber(conn, subscriberId, phoneNumber, email);
        MessageUtils.sendResponseToClient(user, "UpdateStatus", responseFromDB, client);
	}

    public static void reactivateSubscriber(String user, Object data, ConnectionToClient client) {
        String[] parts = ((String) data).split(":", 2);
        int subscriberId = Integer.parseInt(parts[0]);
        String librarianName = parts[1];
        boolean success = mysqlConnection.reactivateSubscriber(conn, subscriberId, librarianName);
        MessageUtils.sendResponseToClient(user, "SubReactivated", success ? "Subscriber reactivated" : "ERROR: Subscriber not found", client);
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

    public static void returnLostBook(String user, Object data, ConnectionToClient client) {
        try {
            String[] parts = ((String) data).split(":", 3);
            int subId = Integer.parseInt(parts[0]);
            int copyId = Integer.parseInt(parts[1]);
            LocalDate returnDate = LocalDate.parse(parts[2]);
            boolean success = mysqlConnection.returnLostBook(conn, subId, copyId, returnDate);
            if (success) {
                Book returnedBook = mysqlConnection.getBookByCopyId(conn, copyId);
                mysqlConnection.notifyNextOrder(conn, returnedBook.getBookId());
                MessageUtils.sendResponseToClient(user, "ReturnStatus",
                "Lost book has been returned successfully", client);
            } else {
                MessageUtils.sendResponseToClient(user, "Error", 
                "Failed to return lost book", client);
            }
        } catch (Exception e) {
            e.printStackTrace();
            MessageUtils.sendResponseToClient(user, "Error", 
            "Error processing lost book return", client);
        }
    }

    public static void returnBook(String user, Object data, ConnectionToClient client) {
        String[] parts = ((String) data).split(":", 3);
        int subId = Integer.parseInt(parts[0]);
        int copyId = Integer.parseInt(parts[1]);
        LocalDate returnDate = LocalDate.parse(parts[2]);
        boolean success = mysqlConnection.returnBook(conn, subId, copyId, returnDate);
        if (success) {
            Book returnedBook = mysqlConnection.getBookByCopyId(conn, copyId);
            mysqlConnection.notifyNextOrder(conn, returnedBook.getBookId());
        }
        MessageUtils.sendResponseToClient(user, "ReturnStatus", success ? "Book has been returned successfully" : "ERROR: Book does not match subscriber", client);
    }

    public static void lostBook(String user, Object data, ConnectionToClient client) {
        int borrowId = (int) data;
        boolean success = mysqlConnection.markBookAsLost(conn, borrowId);

        MessageUtils.sendResponseToClient(user, "LostStatus", success ? "Book declared lost" : "ERROR: Couldn't find matching record", client);
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
        String cancelStatus = "ERROR: Order not found";
        OrderRecord cancelledOrder = mysqlConnection.cancelOrder(conn, orderId);
        if (cancelledOrder != null) {
            cancelStatus = "Order cancelled";
            if (cancelledOrder.getStatus().equals("In-Progress")) {
                Integer subId = mysqlConnection.notifyNextOrder(conn, cancelledOrder.getBookId());
                
                // print sub id
                System.out.println("Next order SubID: " + subId);
                int copyId = mysqlConnection.getCopyIdByCancelledOrder(conn, cancelledOrder.getSubId(), cancelledOrder.getBookId());

                if (subId != null) {
                    // print copy id
                    System.out.println("Cancelled order: " + cancelledOrder);
                    System.out.println("Copy ID: " + copyId);
                    mysqlConnection.setBookCopyOrdered(conn, copyId, subId);
                } else { 
                    mysqlConnection.setBookCopyAvailable(conn, copyId);
                }
            }
        } else {
            System.out.println("Order not found");
        }
        MessageUtils.sendResponseToClient(user, "CancelStatus", cancelStatus, client);
    }

    public static void checkOrder(String user, Object data, ConnectionToClient client) {
        String libName = null;
        String[] parts = ((String) data).split(":", 4);
        int subId = Integer.parseInt(parts[0]);
        int borrowId = Integer.parseInt(parts[1]);
        LocalDate extensionDate = LocalDate.parse(parts[2]);
        if (user.equals("librarian")) { 
            libName = parts[3]; 
        }

        boolean success = false;
        int bookId = mysqlConnection.getBookIdByBorrowId(conn, borrowId);
        boolean orderExists = mysqlConnection.anyOrderExists(conn, bookId);
        if (orderExists == false) {
            success = mysqlConnection.extendBorrow(conn, subId, borrowId, extensionDate);
            if (success == true && user.equals("librarian")) {
                mysqlConnection.logExtensionByLibrarian(conn, subId, bookId, libName);
            } else if (success == true && user.equals("subscriber")) {
                for (int i = 1; i < 6; i++) {
                    String msg = subId + " has extended the borrowing of book " + bookId + " to " + extensionDate;
                    mysqlConnection.sendLibNotification(conn, i, msg);
                }
            }
        }
        MessageUtils.sendResponseToClient(user, "ExtendStatus" , success ? "Borrowing extended" : "ERROR: Couldn't process extension", client);
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

    // Monthly reports

    public static void generateMonthlyReports(LocalDate reportDate) {
        try {
            mysqlConnection.generateMonthlyReports(conn, reportDate);
        } catch (Exception e) {
            logger.severe("Failed to generate monthly reports: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void fetchMonthlyReports(String user, ConnectionToClient client) {
        try {
            List<String> reportMonths = ReportSaver.getReportMonths();
            System.out.println("Processing " + reportMonths.size() + " months");
            
            // Send total count first
            MessageUtils.sendResponseToClient(user, "ReportCount", reportMonths.size(), client);
            
            // Collect all borrow reports and subscriber reports
            Map<String, List<BorrowReport>> allBorrowReports = new HashMap<>();
            Map<String, List<SubscriberReport>> allSubscriberReports = new HashMap<>();
            
            for (String monthYear : reportMonths) {
                if (!client.isAlive()) {
                    System.out.println("Client connection lost during processing");
                    return;
                }
                
                try {
                    System.out.println("Loading reports for month: " + monthYear);
                    
                    // Load reports for this month
                    List<BorrowReport> borrowReports = ReportSaver.loadBorrowReport(monthYear);
                    List<SubscriberReport> subscriberReports = ReportSaver.loadSubscriberReport(monthYear);
                    
                    // Add reports to the collections
                    allBorrowReports.put(monthYear, borrowReports);
                    allSubscriberReports.put(monthYear, subscriberReports);
                    
                    System.out.println("Loaded reports for month: " + monthYear);
                    
                } catch (Exception e) {
                    System.out.println("Error processing month " + monthYear + ": " + e.getMessage());
                }
            }
            
            // Send all borrow reports if still connected
            if (client.isAlive()) {
                System.out.println("Sending all borrow reports to client");
                MessageUtils.sendResponseToClient(user, "AllBorrowReports", allBorrowReports, client);
            }
            
            // Send all subscriber reports if still connected
            if (client.isAlive()) {
                System.out.println("Sending all subscriber reports to client");
                MessageUtils.sendResponseToClient(user, "AllSubscriberReports", allSubscriberReports, client);
            }
            
        } catch (Exception e) {
            System.out.println("Error in fetchMonthlyReports: " + e.getMessage());
        }
    }

    // public static void generateMissingReports() {
    //     LocalDate now = LocalDate.now();
    //     System.out.println("CURRENT TIME: " + now);
    //     LocalDate startDate = LocalDate.of(2024, 1, 1);
    
    //     List<String> existingReportMonths = ReportSaver.getReportMonths();
    
    //     while (startDate.isBefore(now.withDayOfMonth(1))) {
    //         String monthYear = startDate.format(DateTimeFormatter.ofPattern("MM-yyyy"));
    //         if (!existingReportMonths.contains(monthYear)) {
    //             System.out.println("Generating reports for " + monthYear);
    
    //             // Generate borrow report
    //             List<BorrowReport> borrowReport = mysqlConnection.fetchBorrowReport(conn, startDate);
    
    //             // Generate subscriber report
    //             List<SubscriberReport> subscriberReport = mysqlConnection.fetchSubscriberReport(conn, startDate);
    
    //             // Save the reports
    //             ReportSaver.saveReports(borrowReport, subscriberReport, monthYear);
    //         } else {
    //             System.out.println("Reports for " + monthYear + " already exist. Skipping generation.");
    //         }
    //         startDate = startDate.plusMonths(1);
    //     }
    // }



    // Client connection

    public static void connect(String user, ConnectionToClient client) {
        String clientInfo = client.toString();
        String connectionStatus = client.isAlive() ? "Connected" : "Disconnected";
        ccc = InstanceManager.getClientConnectedController();

        System.out.println("Client info: " + clientInfo);
        ccc.loadClientDetails(clientInfo, connectionStatus);
        MessageUtils.sendResponseToClient(user, "Print", "Client details loaded", client);
    }

    public static void disconnect(String user, ConnectionToClient client) {
        ccc = InstanceManager.getClientConnectedController();

        ccc.loadClientDetails("null", "Disconnected");
        MessageUtils.sendResponseToClient(user, "Print", "Server disconnected", client);
    }
   
}

