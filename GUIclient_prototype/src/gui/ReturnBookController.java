package gui;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import client.ClientUI;
import client.SharedController;
<<<<<<< HEAD
import common.Subscriber;
=======
>>>>>>> 025af54 (changed)
import common.BorrowingRecord;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ReturnBookController {
    @FXML private Button btnback;
    @FXML private Button btnscanbook;
    @FXML private TextField txtid;
    @FXML private DatePicker actualReturnDate;
    @FXML private ChoiceBox<String> btnreturnorfreeze;
    @FXML private Label messageLabel;
<<<<<<< HEAD
    private String bookID;
=======
    private String copyID;
    private BorrowingRecord foundReturnRecord;
>>>>>>> 025af54 (changed)

    private String getID() { //get id from textfield
		return txtid.getText();
	}

<<<<<<< HEAD
    public void setBookID(String bookID) { //set bookID from ReturnBookIdController
        this.bookID = bookID;
    }

    public void FindSubscriber(ActionEvent event) throws Exception { //handle when librarian clicks find subscriber button
=======
    public void setcopyID(String copyID) { //set bookID from ReturnBookIdController
        this.copyID = copyID;
    }
    //handle when librarian clicks find subscriber button
    //show librarian return date and if return date has passed, freeze subscriber
    public void FindSubscriber(ActionEvent event) throws Exception { 
>>>>>>> 025af54 (changed)
		try {
            ClientUI.cc.accept("connect");
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                String id = getID();
                System.out.println("ID entered: " + id);
                if (id.trim().isEmpty()) {
                    System.out.println("Must enter an id number");
                    displayMessage("Must enter an id number");
                } else if (!id.matches("\\d+")) {
                    displayMessage("ID must be numbers");
                } else if (id.length() > 9) {
                    displayMessage("ID number too long");
<<<<<<< HEAD
                } else if (bookID == null) {
=======
                } else if (copyID == null) {
>>>>>>> 025af54 (changed)
                    displayMessage("Please scan book first");
                }
                else {
                    System.out.println("Calling ClientUI.cc.accept");
<<<<<<< HEAD
                    ClientUI.cc.accept("returnSubscriber " + id + " " + bookID);
                    BorrowingRecord foundReturnRecord= SharedController.getBorrowingRecord();
=======
                    ClientUI.cc.accept("returnSubscriber " + id + " " + copyID);
                    foundReturnRecord= SharedController.getBorrowingRecord();
>>>>>>> 025af54 (changed)
                    if(foundReturnRecord == null) {
                        System.out.println("Return Record Not Found");
                        displayMessage("Return Record Not Found");
                    } 
                    else {
                        StringBuilder messageBuilder = new StringBuilder();
                       // Display the return date in the label
                        Date returnDate = foundReturnRecord.getExpectedReturnDate();
                        if (returnDate != null) {
                            messageBuilder.append("Return Date Until: ").append(returnDate.toString()).append("\n");
                        } else {
                            messageBuilder.append("Return Date not available\n");
                        }
    
                        // Check if the return date has passed
                        Date currentDate = new Date();
                        if (returnDate != null && returnDate.before(currentDate)) {
                            messageBuilder.append("Return date passed, Please freeze subscriber");
                            
                        }
    
                        displayMessage(messageBuilder.toString());
                    }
                }
	        } else {
                System.out.println("No server connection");
                displayMessage("No server connection");
                return;
            }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
<<<<<<< HEAD
    
    public void scanBook(ActionEvent event) throws Exception  { //scan book button
=======

    //scan book button and save bookID
    public void scanBook(ActionEvent event) throws Exception  { 
>>>>>>> 025af54 (changed)
        try {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("ReturnBookId.fxml"));
        Parent root = fxmlLoader.load();
        
        // Get the controller of the new window
        ReturnBookIdController controller = fxmlLoader.getController();
        // Pass the reference of ReturnBookController to the new controller
        controller.setReturnBookController(this);
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Collect Book ID");
        stage.setScene(new Scene(root));
        stage.showAndWait();
        
        // After the window is closed, you can use the bookID
<<<<<<< HEAD
        System.out.println("Collected Book ID: " + bookID);
=======
        System.out.println("Collected copy ID: " + copyID);
>>>>>>> 025af54 (changed)
    } catch (Exception e) {
        e.printStackTrace();
    }
    }

<<<<<<< HEAD
      public void handleReturnDateSubmit(ActionEvent event) { //handle when librarian clicks submit return date button
=======
    //handle when librarian clicks submit return date button with actual return date
      public void handleReturnDateSubmit(ActionEvent event) { 
>>>>>>> 025af54 (changed)
        try {
            ClientUI.cc.accept("connect");
            if (ClientUI.cc.getConnectionStatusFlag() == 1){
            LocalDate selectedDate = actualReturnDate.getValue();
            if (selectedDate == null) {
                displayMessage("Please select a return date.");
                return;
            }

            LocalDate currentDate = LocalDate.now();
            if (selectedDate.isAfter(currentDate)) {
                displayMessage("Return date cannot be in the future.");
                return;
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = selectedDate.format(formatter);

            // Process the return date
            System.out.println("Selected return date: " + selectedDate);
            BorrowingRecord br= SharedController.getBorrowingRecord();
            if (br == null) {
                displayMessage("No borrowing record found");
                return;
            }
            ClientUI.cc.accept("updateActualReturnDate " + br.getCopyId() + "" + formattedDate + "" + br.getSubId() ); //update actual return date
            displayMessage("Return date set to: " + formattedDate);
        } 
        else {
            System.out.println("No server connection");
            displayMessage("No server connection");
            return;
        }
        } catch (Exception e) {
        e.printStackTrace();
        }
    }
<<<<<<< HEAD

=======
    //handle when librarian clicks on return or freeze button
>>>>>>> 025af54 (changed)
    public void handleReturnOrFreeze(ActionEvent event) {
        String choice = btnreturnorfreeze.getValue();
        String id = getID();
        if (choice == null) {
            displayMessage("Please select an action.");
            return;
        }
        if (choice.equals("Freeze")) {
            freezeSubscriber(id);
        } else if (choice.equals("Return")) {
            returnBook(id);
        }
    }
<<<<<<< HEAD

    private void freezeSubscriber(String subscriberId) {
        // Implement the logic to freeze the subscriber
        // For example, you can send a command to the server to freeze the subscriber
        ClientUI.cc.accept("freezeSubscriber " + subscriberId);
        System.out.println("Subscriber " + subscriberId + " has been frozen.");
    }

    private void returnBook(String subscriberId) {
        // Implement the logic to return the book
        // For example, you can send a command to the server to return the book
        ClientUI.cc.accept("returnBook " + subscriberId);
=======
    //freeze subscriber
    private void freezeSubscriber(String subscriberId) {
        ClientUI.cc.accept("freezeSubscriber " + subscriberId);
        System.out.println("Subscriber " + subscriberId + " has been frozen.");
    }
    //return book from subscriber
    private void returnBook(String subscriberId) {
        ClientUI.cc.accept("returnBook " + copyID);
>>>>>>> 025af54 (changed)
        System.out.println("Book for subscriber " + subscriberId + " has been returned.");
    }

    public void displayMessage(String message) { //display message to user in label
        messageLabel.setText(message);
    }
}
