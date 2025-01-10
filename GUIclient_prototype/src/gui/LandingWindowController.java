package gui;

import client.ClientUI;
import client.SharedController;
import common.Book;
import common.BookCopy;
import common.BookDetails;
import common.MessageUtils;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import javafx.event.ActionEvent;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

@SuppressWarnings("rawtypes")
public class LandingWindowController implements Initializable {

    @FXML private MenuButton menuButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private ImageView searchIcon;

    @FXML private TableView<BookDetails> bookTable;
    @FXML private TableColumn<BookDetails, String> bookNameColumn;
    @FXML private TableColumn<BookDetails, String> authorColumn;
    @FXML private TableColumn<BookDetails, String> genreColumn;
    @FXML private TableColumn<BookDetails, String> descriptionColumn;
    @FXML private TableColumn<BookDetails, String> locationColumn;
    @FXML private TableColumn<BookDetails, String> availabilityColumn;

    @FXML private Label copiedLabel;

    @FXML private Label idLabel;
    @FXML private TextField idField;

    @FXML private Button loginButton;

    @FXML private Label messageLabel;

    private String getID() {
        return idField.getText();
    }

    private String getSearch() {
        return searchField.getText();
    }

    private String getMenu() {
        return menuButton.getText();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        double totalWidth = searchField.getLayoutX() + searchField.getPrefWidth();  // Total width to maintain
        // Adjust the width of the MenuButton based on the initial text
        //adjustMenuButtonWidth(menuButton.getText());

        // Only add text change listener for dynamic updates
        menuButton.textProperty().addListener((obs, oldVal, newVal) -> {
        if (!oldVal.equals(newVal)) {  // Only adjust if text actually changed
            adjustMenuButtonWidth(newVal);
        }
    });

        // Add width property listener to menuButton
        menuButton.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            double newMenuWidth = newWidth.doubleValue();
            searchField.setLayoutX(menuButton.getLayoutX() + newMenuWidth);
            searchField.setPrefWidth(totalWidth - searchField.getLayoutX());
        });

        // Add text change listener that triggers width adjustment
        menuButton.textProperty().addListener((obs, oldVal, newVal) -> {
            adjustMenuButtonWidth(newVal);
        });
        // Verify FXML injection
        assert bookTable != null : "fx:id=\"bookTable\" was not injected";
        assert bookNameColumn != null : "fx:id=\"bookNameColumn\" was not injected";
        assert authorColumn != null : "fx:id=\"authorColumn\" was not injected";
        assert genreColumn != null : "fx:id=\"genreColumn\" was not injected";
        assert descriptionColumn != null : "fx:id=\"descriptionColumn\" was not injected";
        assert locationColumn != null : "fx:id=\"locationColumn\" was not injected";
        assert availabilityColumn != null : "fx:id=\"availabilityColumn\" was not injected";

        // Initialize columns
        setupColumns();

        // Enable cell selection
        bookTable.getSelectionModel().setCellSelectionEnabled(true);
        bookTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        bookTable.setEditable(false);

        // Copy functionality - Ctrl + C
        bookTable.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.C && event.isControlDown()) {
                copySelectionToClipboard();
            }
        });

        // Copy functionality - Mouse press
        bookTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                copySelectionToClipboard();
            }
        });

        // Set the controller in SharedController
        SharedController.setLandingWindowController(this);
    }

    private void setupColumns() {
        if (bookNameColumn == null || bookTable == null) {
            System.err.println("ERROR: Table components not properly injected");
            return;
        }

        // Set value factories
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("bookName"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("availability"));

        // Set up non-editable cell factories
        bookNameColumn.setCellFactory(column -> createNonEditableCell());
        authorColumn.setCellFactory(column -> createNonEditableCell());
        genreColumn.setCellFactory(column -> createNonEditableCell());
        descriptionColumn.setCellFactory(column -> createNonEditableCell());
        locationColumn.setCellFactory(column -> createNonEditableCell());
        availabilityColumn.setCellFactory(column -> createNonEditableCell());
    }

    private <T> TableCell<BookDetails, T> createNonEditableCell() {
        return new TableCell<BookDetails, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.toString());
                }
                setStyle("-fx-selection-bar: -fx-accent; -fx-selection-bar-non-focused: -fx-accent;");
            }
        };
    }

    private void copySelectionToClipboard() {
        StringBuilder clipboardString = new StringBuilder();
        ObservableList<TablePosition> positionList = bookTable.getSelectionModel().getSelectedCells();

        int prevRow = -1;
        for (TablePosition position : positionList) {
            if (prevRow != -1 && prevRow != position.getRow()) {
                clipboardString.append('\n');
            }
            Object cell = bookTable.getColumns().get(position.getColumn())
                    .getCellData(position.getRow());
            clipboardString.append(cell == null ? "" : cell.toString()).append('\t');
            prevRow = position.getRow();
        }

        final ClipboardContent content = new ClipboardContent();
        content.putString(clipboardString.toString());
        Clipboard.getSystemClipboard().setContent(content);

        showCopiedMessage();
    }

    private void showCopiedMessage() {
        copiedLabel.setVisible(true);
        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> copiedLabel.setVisible(false));
        pause.play();
    }

    @FXML
    private void handleMenuItemAction(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        menuButton.setText(menuItem.getText());
    }

    private void adjustMenuButtonWidth(String text) {
        double width = computeTextWidth(menuButton.getFont(), text) + 50; // Add some flexible padding
        menuButton.setPrefWidth(width);
    }

    private double computeTextWidth(Font font, String text) {
        Text tempText = new Text(text);
        tempText.setFont(font);
        return tempText.getLayoutBounds().getWidth();
    }

    public void loadBookDetails(List<Object> list) {
        // print
        System.out.println("Loading book details...");
        Platform.runLater(() -> {
            displayMessage("");
            System.err.println("list size in landing window: " + list.size());
            ObservableList<BookDetails> bookDetailsList = FXCollections.observableArrayList();
            Book currentBook = null;
            for (Object item : list) {
                if (item instanceof Book) {
                    currentBook = (Book) item;
                } else if (item instanceof BookCopy && currentBook != null) {
                    BookCopy bookCopy = (BookCopy) item;
                    BookDetails bookDetails = new BookDetails(
                            currentBook.getTitle(),
                            currentBook.getAuthor(),
                            currentBook.getGenre(),
                            currentBook.getDescription(),
                            bookCopy.getLocation(),
                            bookCopy.getStatus()
                    );
                    bookDetailsList.add(bookDetails);
                }
            }

            bookTable.setItems(bookDetailsList);
            bookTable.setVisible(true);
        });
    }

    public void handleSearchAction(ActionEvent event) {
        String searchText = getSearch();
        String selectedMenu = getMenu();
        // Implement your search logic here
        try {
            MessageUtils.sendMessage(ClientUI.cc, "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (searchText.trim().isEmpty()) {
                    displayMessage("Please enter a search term");
                } else { 
                    System.out.println("Searching for: " + searchText);
                    System.out.println("Selected menu: " + selectedMenu);
                    MessageUtils.sendMessage(ClientUI.cc, "sendSearchedBooks", selectedMenu + ":" + searchText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public void noBooksFound() {
        bookTable.setVisible(false);
        displayMessage("No books found");
    }

    public void handleLoginAction(ActionEvent event) {
        String idText = searchField.getText();
        try {
            MessageUtils.sendMessage(ClientUI.cc, "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (idText.trim().isEmpty()) {
                    displayMessage("Please enter an ID");
                } else if (idText.length() > 9 || !idText.matches("\\d+")){
                    displayMessage("Please enter valid ID");
                } else {
                    System.out.println("Logging in with ID: " + idText);
                    MessageUtils.sendMessage(ClientUI.cc, "login", idText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Implement your search logic here
        // displayMessage(searchText);
        System.out.println("Searching for: " + idText);
    }

    public void noMatchingId() {
        displayMessage("No matching ID found");
    }


    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
