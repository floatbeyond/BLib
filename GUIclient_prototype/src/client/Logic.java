package client;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.ArrayList;
import java.util.List;
import javafx.application.Platform;

// import gui.SubscribersTableController;
// import gui.ClientPortController;
// import gui.LandingWindowController;
// import gui.SubscriberFormController;
import common.Subscriber;
import gui.PersonalDetailsController;
import common.BookCopy;
import common.Librarian;

public class Logic {
    // public static SubscribersTableController stc = SharedController.getSubscribersTableController();
    // public static ClientPortController cpc = SharedController.getClientPortController();
    // public static SubscriberFormController sfc = SharedController.getSubscriberFormController();
    // public static LandingWindowController lwc = SharedController.getLandingWindowController();

    // Login

    public static void parseLogin(Object user) {
        if (user instanceof Librarian) {
            System.out.println("User is a librarian");
            SharedController.setLibrarian((Librarian) user);
            SharedController.logwc.userType = "Librarian";
        } else if (user instanceof Subscriber) {
            System.out.println("User is a subscriber");
            SharedController.setSubscriber((Subscriber) user);
            SharedController.logwc.userType = "Subscriber";
        } else {
            System.out.println("User not found");
            SharedController.logwc.userType = "NotFound";
        }
    }

    // Librarian

    public static void parseLibrarian(Librarian receivedLibrarian) {
        SharedController.setLibrarian(receivedLibrarian);
        System.out.println("Received Librarian: " + receivedLibrarian);
    }

    // Subscriber

    public static void parseSubscriber(Subscriber receivedSubscriber) {
        SharedController.setSubscriber(receivedSubscriber);
        System.out.println("Received Subscriber: " + receivedSubscriber);
    }

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

    public static void updateSubscriberStatus(String status) {
        SharedController.sfc.displayMessage(status);
    }

    // Method to handle the response for books in order count
    public static void BooksInOrderCountResponse(int count) {
        PersonalDetailsController controller = SharedController.getPersonalDetailsController();
        if (controller != null) {
            controller.handleBooksInOrderCountResponse(count);
            System.out.println("Books in order count: " + count);
        } else {
            System.out.println("PersonalDetailsController is null");
        }
    }

    // Method to handle the response for books in borrow count
    public static void BooksInBorrowCountResponse(int count) {
        PersonalDetailsController controller = SharedController.getPersonalDetailsController();
        if (controller != null) {
            controller.handleBooksInBorrowCountResponse(count);
            System.out.println("Books in borrow count: " + count);
        } else {
            System.out.println("PersonalDetailsController is null");
        }
    }

  
    // Book

    public static void parseBookCopy(String user, BookCopy bookCopy) {
        System.out.println("Book copy found");
        SharedController.setBookCopy(bookCopy);
    }

    public static void parseBookList(String user, List<Object> list) {
        // print book list
        System.out.println("parseBookList method called");
        
        // Print the size of the list
        System.out.println("List size: " + list.size());

        Platform.runLater(() -> {
            if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Object)) {
                if (user.equals("user")) {
                    System.out.println("Loading table");
                    if (SharedController.lwc != null) {
                        System.out.println("SearchBookTable is initialized");
                        SharedController.lwc.loadBookDetails(list);
                    } else {
                        System.out.println("SearchBookTable is null");
                    }
                } else if (user.equals("subscriber")) {
                        System.out.println("Loading subscriber table");
                        if (SharedController.smfc != null) {
                            System.out.println("SubscriberTable is initialized");
                            SharedController.smfc.loadBookDetails(list);
                        } else {
                            System.out.println("SubscriberTable is null");
                        }
                }
            } else {
                System.out.println("Error in parsing book list");
                SharedController.lwc.noBooksFound();
            }
        });
    }


    // Prints

    public static void print(String message) {
        System.out.println("Received message: " + message);
    }

    public static void printError(String errorMessage) {
        if (errorMessage.equals("Subscriber ID Not Found")) {
            System.out.println("Subscriber not found");
            SharedController.setSubscriber(null);
        } else if (errorMessage.equals("Invalid command")) {
            System.out.println("Invalid command");
        }
        System.out.println("Received error message: " + errorMessage);
    }



}