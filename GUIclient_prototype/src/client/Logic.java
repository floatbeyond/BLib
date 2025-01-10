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

public class Logic {
    // public static SubscribersTableController stc = SharedController.getSubscribersTableController();
    // public static ClientPortController cpc = SharedController.getClientPortController();
    // public static SubscriberFormController sfc = SharedController.getSubscriberFormController();
    // public static LandingWindowController lwc = SharedController.getLandingWindowController();

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


    // Book

    public static void parseBookList(List<Object> list) {
        // print book list
        System.out.println("parseBookList method called");
        
        // Print the size of the list
        System.out.println("List size: " + list.size());

        Platform.runLater(() -> {
            if (!list.isEmpty() && list.stream().allMatch(item -> item instanceof Object)) {
                System.out.println("Loading table");
                if (SharedController.lwc != null) {
                    System.out.println("SearchBookTable is initialized");
                    SharedController.lwc.loadBookDetails(list);
                } else {
                    System.out.println("SearchBookTable is null");
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