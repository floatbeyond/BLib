package gui;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
public class ReturnBookIdController {
    @FXML
    private TextField bookIDField; //book id field after scanning
    public ReturnBookController returnBookController; //

    public void setReturnBookController(ReturnBookController returnBookController) {
        this.returnBookController = returnBookController;
    }

    @FXML
    private void handleSubmit() {
        String bookID = bookIDField.getText();
        // Handle the book ID (e.g., send it to the server or process it)
        System.out.println("Book ID: " + bookID);
        // Close the window
        Stage stage = (Stage) bookIDField.getScene().getWindow();
        stage.close();
    }
    
}
