package server;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javafx.application.Platform;
import java.time.LocalDate;
import common.OrderRecord;
import common.Notification;
import common.BorrowingRecord;
import common.DataLogs;
import common.MessageUtils;
import common.Subscriber;
import common.SubscriberReport;
import common.Book;
import common.BookCopy;
import common.BorrowRecordDTO;
import common.BorrowReport;
import common.OrderRecordDTO;
import ocsf.server.ConnectionToClient;
import gui.ClientConnectedController;
import common.OrderResponse;


/**
 * The Logic class contains the core business logic for handling various operations
 * such as user login, notifications, subscriber management, book management, and report generation.
 */
public class Logic {
    private static final Logger logger = Logger.getLogger(Logic.class.getName());
    private static Connection conn = InstanceManager.getDbConnection();
    private static ClientConnectedController ccc;
    private static Subscriber s;
    private static BookCopy bc;
    private static BorrowingRecord br;

    /**
     * Handles user login by fetching user information from the database and sending it to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param userId the user ID
     * @param client the client connection
     */
    public static void userLogin(String user, int userId, ConnectionToClient client) {
        Object userInfo = mysqlConnection.userLogin(conn, userId);
        MessageUtils.sendResponseToClient(user, "LoginStatus", userInfo, client);
    }

    /**
     * Fetches new notifications for a user and sends them to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the user ID
     * @param client the client connection
     */
    public static void fetchNotifications(String user, Object data, ConnectionToClient client) {
        int userId = (int) data;
        List<Notification> notifications = mysqlConnection.getNewNotifications(conn, userId, user);
        try {
            MessageUtils.sendResponseToClient(user, "NewNotifications", notifications, client);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error serializing notifications: " + e.getMessage());
        }
    }

    /**
     * Adds a new subscriber to the database and sends the generated ID to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param newSubscriber the new subscriber object
     * @param client the client connection
     */
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

    /**
     * Fetches a specific subscriber by ID and sends the subscriber details to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param subscriberId the subscriber ID
     * @param client the client connection
     */
    public static void specificSubscriber(String user, int subscriberId, ConnectionToClient client) {
        if ((s = mysqlConnection.findSubscriber(conn, subscriberId)) != null) {
            MessageUtils.sendResponseToClient(user, "foundSubscriber", s, client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Subscriber ID Not Found", client);
        }
    }

    /**
     * Updates subscriber details such as phone number and email.
     * Sends a success message if the subscriber is updated, or an error message if the subscriber is not found.
     * Data is in the format "subscriberId:phoneNumber:email".
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the subscriber details in the format "subscriberId:phoneNumber:email"
     * @param client the client connection
     */
    public static void updateSubscriberDetails(String user, Object data, ConnectionToClient client) {
        String subscriberDetails = (String) data;
        String[] parts = subscriberDetails.split(":", 3);
        int subscriberId = Integer.parseInt(parts[0]);
        String phoneNumber = parts[1];
        String email = parts[2];
            
        Object responseFromDB = mysqlConnection.updateSubscriber(conn, subscriberId, phoneNumber, email);
        MessageUtils.sendResponseToClient(user, "UpdateStatus", responseFromDB, client);
	}

    /**
     * Reactivates a subscriber account.
     * Sends a success message if the subscriber is reactivated, or an error message if the subscriber is not found.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the subscriber ID and librarian name in the format "subscriberId:librarianName"
     * @param client the client connection
     */
    public static void reactivateSubscriber(String user, Object data, ConnectionToClient client) {
        String[] parts = ((String) data).split(":", 2);
        int subscriberId = Integer.parseInt(parts[0]);
        String librarianName = parts[1];
        boolean success = mysqlConnection.reactivateSubscriber(conn, subscriberId, librarianName);
        MessageUtils.sendResponseToClient(user, "SubReactivated", success ? "Subscriber reactivated" : "ERROR: Subscriber not found", client);
    }

    /**
     * Fetches and sends the list of all subscribers to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param client the client connection
     */
    public static void showSubscribersTable(String user, ConnectionToClient client) {
        ArrayList<Subscriber> table = mysqlConnection.getSubscribers(conn);
        MessageUtils.sendResponseToClient(user, "SubscriberList", table, client);
    }
    
    /**
     * Fetches and sends the data logs for a specific subscriber to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param sub_id the subscriber ID
     * @param client the client connection
     */
    public static void sendDataLogs(String user, int sub_id, ConnectionToClient client) {
        ArrayList<DataLogs> dataLogs = mysqlConnection.getDataLogs(conn, sub_id);
        Platform.runLater(() -> {
            MessageUtils.sendResponseToClient(user, "DataLogsList", dataLogs, client);
        });
        
    }

    /**
     * Fetches and sends the list of borrow records for a specific subscriber to the client.
     * Borrow records are in type BorrowRecordDTO
     * 
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the subscriber ID
     * @param client the client connection
     */
    public static void sendUserBorrows(String user, Object data, ConnectionToClient client) {
        int subId = (int) data;
        List<BorrowRecordDTO> borrows = mysqlConnection.getUserBorrows(conn, subId);
        MessageUtils.sendResponseToClient(user, "UserBorrowsList", borrows, client);
    }

    /**
     * Fetches and sends the list of order records for a specific subscriber to the client.
     * Order records are in type OrderRecordDTO
     * 
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the subscriber ID
     * @param client the client connection
     */
    public static void sendUserOrders(String user, Object data, ConnectionToClient client) {
        int subId = (int) data;
        List<OrderRecordDTO> orders = mysqlConnection.getUserOrders(conn, user, subId);
        // print list of orders being sent
        System.out.println(orders);
        MessageUtils.sendResponseToClient(user, "UserOrdersList", orders, client);
    }

    /**
     * Searches for books based on the given criteria and sends the results to the client.
     * Sends the list of books found in the search.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the search criteria in the format "searchType:searchText"
     * @param client the client connection
     */
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

    /**
     * Adds a new borrowing record to the database and sends the status to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param newborrow the new borrowing record object
     * @param client the client connection
     */
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

    /**
     * Processes the return of a lost book and sends the status to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the return details in the format "subscriberId:copyId:returnDate"
     * @param client the client connection
     */
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

    /**
     * Processes the return of a book and sends the status to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the return details in the format "subscriberId:copyId:returnDate"
     * @param client the client connection
     */
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

    /**
     * Marks a book as lost and sends the status to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the borrowing record ID
     * @param client the client connection
     */
    public static void lostBook(String user, Object data, ConnectionToClient client) {
        int borrowId = (int) data;
        boolean success = mysqlConnection.markBookAsLost(conn, borrowId);

        MessageUtils.sendResponseToClient(user, "LostStatus", success ? "Book declared lost" : "ERROR: Couldn't find matching record", client);
    }

    /**
     * Adds a new order for a book and sends the status to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param newOrder the new order details in the format "bookId:subscriberId"
     * @param client the client connection
     */
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

    /**
     * Cancels an order and sends the status to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param order the order ID
     * @param client the client connection
     */
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

    /**
     * Checks an order and processes an extension if no other orders exist for the book.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param data the order details in the format "subscriberId:borrowId:extensionDate:librarianName"
     * @param client the client connection
     */
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

    /**
     * Scans a book copy or subscriber and sends the details to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param msg the scan message
     * @param unparsedId the unparsed ID
     * @param client the client connection
     */
    public static void scan(String user, String msg, int unparsedId, ConnectionToClient client) {
        if ((bc = mysqlConnection.findBookCopy(conn, unparsedId)) != null) {
            MessageUtils.sendResponseToClient(user, "BookCopy", bc, client);
        } else if ((s = mysqlConnection.findSubscriber(conn, unparsedId)) != null) {
            MessageUtils.sendResponseToClient(user, "foundSubscriber", s, client);
        } else {
            MessageUtils.sendResponseToClient(user, "Error", "Scan failed", client);
        }
    }

    /**
     * Generates monthly reports for a given date.
     *
     * @param reportDate the date for which to generate the reports
     */
    public static void generateMonthlyReports(LocalDate reportDate) {
        try {
            mysqlConnection.generateMonthlyReports(conn, reportDate);
        } catch (Exception e) {
            logger.severe("Failed to generate monthly reports: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Fetches monthly reports for a given user and sends them to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param client the client connection
     */
    public static void fetchMonthlyReports(String user, ConnectionToClient client) {
        try {
            List<String> reportMonths = ReportSaver.getReportMonths();
            System.out.println("Processing " + reportMonths.size() + " months");
            
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

    /**
     * Prints a message to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param message the message to print
     * @param client the client connection
     */
    public static void connect(String user, ConnectionToClient client) {
        String clientInfo = client.toString();
        String connectionStatus = client.isAlive() ? "Connected" : "Disconnected";
        ccc = InstanceManager.getClientConnectedController();

        System.out.println("Client info: " + clientInfo);
        ccc.loadClientDetails(clientInfo, connectionStatus);
        MessageUtils.sendResponseToClient(user, "Print", "Client details loaded", client);
    }

    /**
     * Prints a message to the client.
     *
     * @param user the user type (e.g., "subscriber", "librarian")
     * @param message the message to print
     * @param client the client connection
     */
    public static void disconnect(String user, ConnectionToClient client) {
        ccc = InstanceManager.getClientConnectedController();

        ccc.loadClientDetails("null", "Disconnected");
        MessageUtils.sendResponseToClient(user, "Print", "Server disconnected", client);
    }
   
}

