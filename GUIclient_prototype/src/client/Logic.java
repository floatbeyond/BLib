package client;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.application.Platform;

import common.MessageUtils;
import common.Notification;
import common.Subscriber;
import common.SubscriberReport;
import gui.controllers.LandingWindowController;
import gui.controllers.SubscriberMainFrameController;
import common.BookCopy;
import common.BorrowRecordDTO;
import common.BorrowReport;
import common.Librarian;
import common.OrderRecordDTO;
import common.OrderResponse;

/**
 * This class is responsible for handling the logic of the client side of the application.
 * It is responsible for parsing the data received from the server and updating the UI accordingly.
 */
public class Logic {
    
    private static Map<Integer, List<Notification>> notificationsMap = new HashMap<>();

    /**
     * This method is responsible for parsing the login response from the server.
     * It checks if the user is a librarian or a subscriber and updates the UI accordingly.
     * @param user
     */
    public static void parseLogin(Object user) {
        if (user instanceof Librarian) {
            System.out.println("User is a librarian");
            SharedController.setLibrarian((Librarian) user);
            // SharedController.logwc.setUserStatus(user);
        } else if (user instanceof Subscriber) {
            System.out.println("User is a subscriber");
            SharedController.setSubscriber((Subscriber) user);
        } else {
            System.out.println("User not found");
        }
        SharedController.logwc.setUserStatus(user);
    }

    /**
     * Fetches the notifications for a specific subscriber or librarian.
     * 
     * @param user The user type (subscriber or librarian)
     * @param userId The user id of the subscriber or librarian 
     */
    public static void fetchNotifications(String user, int userId) {
        try {
            MessageUtils.sendMessage(ClientUI.cc, user, "fetchNotifications", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is responsible for handling the notifications received from the server.
     * It updates the notifications map and the UI accordingly.
     * 
     * @param notifications The list of notifications received from the server
     */
    public static void handleNotifications(List<Notification> notifications) {
        if (notifications.isEmpty()) {
            System.out.println("No notifications found");
            return;
        }

        int subId = notifications.get(0).getUserId();
        notificationsMap.putIfAbsent(subId, new ArrayList<>());
        notificationsMap.get(subId).addAll(notifications);

        Platform.runLater(() -> {
            if (SharedController.smfc != null) {
                SharedController.smfc.addNotifications(notifications);
            }
            if (SharedController.lmfc != null) {
                SharedController.lmfc.addNotifications(notifications);
            }
        });
    }

    /**
     * This method is responsible for updating the notifications map and the UI accordingly.
     * 
     * @param receivedLibrarian The librarian object received from the server
     */
    public static void parseLibrarian(Librarian receivedLibrarian) {
        SharedController.setLibrarian(receivedLibrarian);
        System.out.println("Received Librarian: " + receivedLibrarian);
    }

    /**
     * This method is responsible for updating the notifications map and the UI accordingly.
     * 
     * @param receivedSubscriber The subscriber object received from the server
     */
    public static void newSubscriber(int subId) {
        String message;
        if (subId > 0) {
            message = "Added Subscriber: " + subId;
        } else if (subId == 0) {
            message = "Couldnt add subscriber";
        } else {
            message = "Duplicate entry for PhoneNumber or Email";
        }
        Platform.runLater(() -> {
            SharedController.asc.newSubAdded(message);
        });
    }

    /**
     * This method is responsible for updating the subscriber object and the UI accordingly.
     * 
     * @param receivedSubscriber The subscriber object received from the server
     */
    public static void parseSubscriber(Subscriber receivedSubscriber) {
        SharedController.setSubscriber(receivedSubscriber);
        System.out.println("Received Subscriber: " + receivedSubscriber);
    }

    /**
     * This method is responsible for updating the subscriber list and the UI accordingly.
     * 
     * @param list The list of subscribers received from the server
     */
    public static void parseSubscriberList(ArrayList<Subscriber> list) {
        if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Subscriber)) {
            System.out.println("Loading table");
            List<Subscriber> subscriberList = (List<Subscriber>) list;
            // Handle the subscriber list
            ObservableList<Subscriber> subscribers = FXCollections.observableArrayList(subscriberList);
            if (SharedController.stc != null) {
                System.out.println("subscribersTableController is initialized");
                SharedController.stc.parseSubscriberList(subscribers);
            } else {
                System.out.println("subscribersTableController is null");
            }
        }
    }

    /**
     * This method is responsible for updating the subscriber status and the UI accordingly.
     * 
     * @param data The subscriber object or a string message received from the server
     */
    public static void updateSubscriberStatus(Object data) {
        Platform.runLater(() -> {
            if (data instanceof String) {
                SharedController.pdc.updateSubscriberStatus((String) data);
            } else {
                SharedController.setSubscriber((Subscriber) data);
                SharedController.pdc.updateSubscriberStatus("Subscriber details updated");
            }
        });
    }

    /**
     * This method is responsible for reactivating the subscriber status and the UI accordingly.
     * 
     * @param status The status message received from the server
     */
    public static void reactivateSubscriberStatus(String status) {
        Platform.runLater(() -> {
            SharedController.rcc.subscriberReactivated(status);
        });
    }

    /**
     * This method is responsible for parsing the data logs list and updating the UI accordingly.
     * 
     * @param user The user type (subscriber or librarian)
     * @param list The list of data logs received from the server
     */
    public static void parseDataLogsList(String user, ArrayList<Object> list) {
        System.out.println("parseDataLogsList method called");
        Platform.runLater(() -> {
            if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Object)) {
                System.out.println("Loading logs");
                // Handle the data logs list
                if (user.equals("subscriber")) {
                    if (SharedController.slc != null) {
                        System.out.println("subscribersLogsController is initialized");
                        SharedController.slc.setAllDataLogs(list);
                        SharedController.slc.showDataLogs(list);
                    } else {
                        System.out.println("subscribersLogsController is null");
                    }
                } else if (user.equals("librarian")) {
                    if (SharedController.rcc != null) {
                        System.out.println("ReaderCardController is initialized");
                        SharedController.rcc.setAllDataLogs(list);
                    } else {
                        System.out.println("ReaderCardController is null");
                    }
                }
            }
        });
    }

    /**
     * This method is responsible for parsing the user borrows list and updating the UI accordingly.
     * 
     * @param user The user type (subscriber or librarian)
     * @param list The list of borrow records received from the server as BorrowRecordDTO objects
     */
    public static void parseUserBorrowsList(String user, List<BorrowRecordDTO> list) {
        System.out.println("parseUserBorrowsList method called");
        Platform.runLater(() -> {
            if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof BorrowRecordDTO)) {
                System.out.println("Loading borrows");
                // Handle the user borrows list
                if (user.equals("librarian")) {
                    if (SharedController.rcc != null) {
                        System.out.println("ReaderCardController is initialized");
                        SharedController.rcc.setBorrowRecords(list);
                    } else {
                        System.out.println("ReaderCardController is null");
                    }
                } else if (user.equals("subscriber")) {
                    if (SharedController.smfc != null) {
                        System.out.println("SubscriberMainFrame is initialized");
                        SharedController.smfc.setBorrowRecords(list);
                    } else {
                        System.out.println("SubscriberMainFrame is null");
                    }
                }
            } else {
                System.out.println("No orders found");
                if (user.equals("subscriber") && SharedController.smfc != null) {
                    SharedController.smfc.setBorrowRecords(FXCollections.observableArrayList());
                }
            }
        });
    }

    /**
     * This method is responsible for displaying the status of the borrow extension.
     * 
     * @param status
     */
    public static void extendBorrowStatus(String status) {
        Platform.runLater(() -> {
            SharedController.ewc.successfulExtend(status);
        });
    }

    /**
     * This method is responsible for displaying the status of the lost book.
     * 
     * @param status
     */
    public static void lostBookStatus(String status) {
        Platform.runLater(() -> {
            SharedController.rcc.lostMessage(status);
        });
    }

    /**
     * This method is responsible for parsing the user orders list and updating the UI accordingly.
     * 
     * @param user The user type (subscriber or librarian)
     * @param list The list of order records received from the server as OrderRecordDTO objects
     */
    public static void parseUserOrdersList(String user, List<OrderRecordDTO> list) {
        System.out.println("parseUserOrdersList method called");
        Platform.runLater(() -> {
            if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Object)) {
                System.out.println("Loading orders");
                // Handle the user orders list
                if (user.equals("librarian")) {
                    if (SharedController.rcc != null) {
                        System.out.println("ReaderCardController is initialized");
                        SharedController.rcc.setOrderRecords(list);
                    } else {
                        System.out.println("ReaderCardController is null");
                    }
                } else if (user.equals("subscriber")) {
                    if (SharedController.smfc != null) {
                        System.out.println("SubscriberMainFrame is initialized");
                        SharedController.smfc.setOrderRecords(list);
                    } else {
                        System.out.println("SubscriberMainFrame is null");
                    }
                }
            } else {
                System.out.println("No orders found");
                if (user.equals("subscriber") && SharedController.smfc != null) {
                    SharedController.smfc.setOrderRecords(FXCollections.observableArrayList());
                }
            }
        });
    }
  
    /**
     * This method is responsible for parsing the book copy and updating the UI accordingly.
     * 
     * @param user The user type (subscriber or librarian)
     * @param bookCopy The book copy received from the server as a BookCopy object
     */
    public static void parseBookCopy(String user, BookCopy bookCopy) {
        System.out.println("Book copy found");
        SharedController.setBookCopy(bookCopy);
    }

    /**
     * This method is responsible for parsing the book list and updating the UI accordingly.
     * 
     * @param user The user type (subscriber or librarian)
     * @param list The list of books received from the server
     */
    public static void parseBookList(String user, List<Object> list) {
        // print book list
        System.out.println("parseBookList method called with list size: " + list.size());

        Platform.runLater(() -> {
            if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Object)) {
                    System.out.println("Loading table");
                    if (user.equals("user")) {
                        LandingWindowController controller = SharedController.lwc;
                        System.out.println("Loading user table");
                        if (controller != null) {
                            System.out.println("LandingWindowController is initialized");
                            controller.loadBookDetails(list);
                        } else {
                            System.out.println("LandingWindowController is null");
                        }
                    } else if (user.equals("librarian")) {
                        System.out.println("Loading librarian table");
                        if (SharedController.lmfc != null) {
                            System.out.println("LibrarianMainFrameController is initialized");
                            SharedController.lmfc.loadBookDetails(list);
                        } else {
                            System.out.println("LibrarianMainFrameController is null");
                        }
                    } else if (user.equals("subscriber")) {
                        SubscriberMainFrameController controller = SharedController.smfc;
                        System.out.println("Loading subscriber table");
                        if (controller != null) {
                            System.out.println("SubscriberTable is initialized");
                            controller.loadBookDetails(list);
                        } else {
                            System.out.println("SubscriberTable is null");
                        }
                    }
            } else {
                System.out.println("Error in parsing book list");
                if (user.equals("user")) {
                    SharedController.lwc.noBooksFound();
                } else if (user.equals("subscriber")) {
                    SharedController.smfc.noBooksFound();
                } else if (user.equals("librarian")) {
                    SharedController.lmfc.noBooksFound();
                }
            }
        });
    }

    /**
     * This method is responsible for displaying the status of the new borrow.
     * 
     * @param status
     */
    public static void newBorrowStatus(String status) {
        Platform.runLater(() -> {
            SharedController.bfc.successfulBorrow(status);
        });
        
    }

    /**
     * This method is responsible for displaying the status of the return book.
     * 
     * @param status
     */
    public static void returnBookStatus(String status) {
        Platform.runLater(() -> {
            SharedController.rbc.returnMessage(status);
        });
    }

    /**
     * This method is responsible for displaying the status of the new order.
     * 
     * @param status
     */
    public static void newOrderStatus(Object status) {
        OrderResponse response = (OrderResponse) status;
        // print the book
        System.out.println("Book: " + response.getBook());
        Platform.runLater(() -> {
            SharedController.smfc.handleOrderResponse(response);
        });
    }

    /**
     * This method is responsible for displaying the status of the cancel order.
     * 
     * @param status
     */
    public static void cancelOrderStatus(String status) {
        Platform.runLater(() -> {
            SharedController.aoc.alertMessage(status);
        });
    }

    /**
     * This method is responsible for parsing all borrow reports and updating the UI accordingly.
     * 
     * @param reports The map of borrow reports received from the server as a Map object with String keys and List values
     */
    public static void parseAllBorrowReports(Map<String, List<BorrowReport>> reports) {
        System.out.println("Received all borrow times reports");
        Platform.runLater(() -> {
            if (SharedController.drc != null) {
                SharedController.drc.setAllBorrowReports(reports);
            } else {
                // print its null
                System.out.println("DataReportsController is null");
            }
        });
    }

    /**
     * This method is responsible for parsing all subscriber reports and updating the UI accordingly.
     * 
     * @param reports The map of subscriber reports received from the server as a Map object with String keys and List values
     */
    public static void parseAllSubscriberReports(Map<String, List<SubscriberReport>> reports) {
        System.out.println("Received all subscriber status reports");
        Platform.runLater(() -> {
            if (SharedController.drc != null) {
                SharedController.drc.setAllSubscriberReports(reports);
            } else {
                // print its null
                System.out.println("DataReportsController is null");
            }
        });
    }

    /**
     * This method is responsible for printing a message to the console.
     * 
     * @param message The message to be printed
     */
    public static void print(String message) {
        System.out.println("Received message: " + message);
    }

    /**
     * This method is responsible for printing an error message to the console.
     * 
     * @param errorMessage The error message to be printed
     */
    public static void printError(String errorMessage) {
        if (errorMessage.equals("Subscriber ID Not Found")) {
            SharedController.setSubscriber(null);
        } else {
            System.out.println("Error: " + errorMessage);
        }
    }



}