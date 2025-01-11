package gui;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class ReturnBookIdController {
    @FXML
<<<<<<< HEAD
    private TextField bookIDField; //book id field after scanning
=======
    private TextField copyIDField; //book id field after scanning
>>>>>>> 025af54 (changed)
    public ReturnBookController returnBookController; //

    public void setReturnBookController(ReturnBookController returnBookController) {
        this.returnBookController = returnBookController;
    }

    @FXML
    private void handleSubmit() {
<<<<<<< HEAD
        String bookID = bookIDField.getText();
        // Handle the book ID (e.g., send it to the server or process it)
        System.out.println("Book ID: " + bookID);
        // Close the window
        Stage stage = (Stage) bookIDField.getScene().getWindow();
=======
        String copyID = copyIDField.getText();
        // Handle the book ID (e.g., send it to the server or process it)
        returnBookController.setcopyID(copyID);
        System.out.println("copy ID: " + copyID);
        // Close the window
        Stage stage = (Stage) copyIDField.getScene().getWindow();
>>>>>>> 025af54 (changed)
        stage.close();
    }
    
}
