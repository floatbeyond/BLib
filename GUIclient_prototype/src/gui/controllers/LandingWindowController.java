package gui.controllers;

import client.ClientUI;
import client.SharedController;
import common.Book;
import common.BookCopy;
import common.BorrowingRecord;
import common.MessageUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.event.ActionEvent;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the controller for the LandingWindow.fxml file. It is responsible for handling the user input and
 * displaying the books in a table view. It is also responsible for displaying the main window of the application.
 */
public class LandingWindowController implements Initializable {

    @FXML private MenuButton menuButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private ImageView searchIcon;
    @FXML private Button loginButton;
    @FXML private Button exitButton;

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> bookNameColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, String> descriptionColumn;
    @FXML private TableColumn<Book, String> copiesColumn;
    @FXML private TableColumn<Book, Void> actionColumn;

    @FXML private Label messageLabel;

    private Map<Integer, Stage> openDialogs = new HashMap<>(); // Track open dialogs

    private String getSearch() { return searchField.getText(); }
    private String getMenu() { return menuButton.getText(); }

    /**
     * This method is called when the LandingWindow.fxml file is loaded.
     * It is responsible for initializing the table view and setting the cell value factories for the columns.
     * It is also responsible for setting the controller instance in the SharedController class.
     * It is also responsible for setting the width of the menu button based on the text.
     * It is also responsible for setting the search action for the search button and the search field.
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtonWidth();
        setupColumns();
        setupSearch();

        // Set the controller in SharedController
        SharedController.setLandingWindowController(this);
    }

    /**
     * This method is called when the LandingWindow.fxml file is loaded.
     * It is responsible for setting the width of the menu button based on the text.
     * It is also responsible for adding a text change listener to the menu button.
     * It is also responsible for adding a width property listener to the menu button.
     * It is also responsible for adding a text change listener that triggers width adjustment.
     */
    private void setupButtonWidth() {
        double totalWidth = searchField.getLayoutX() + searchField.getPrefWidth();  // Total width to maintain

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
    }

    /**
     * This method is called when the LandingWindow.fxml file is loaded.
     * It is responsible for setting the search action for the search button and the search field.
     * It is also responsible for adding a key pressed (Enter) listener to the search field.
     */
    private void setupSearch() {
        searchButton.setOnAction(e -> handleSearchAction(e));
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSearchAction(null);
            }
        });
    }

    /**
     * This method is called when the LandingWindow.fxml file is loaded.
     * It is responsible for setting the cell value factories for the columns.
     * It is also responsible for applying a custom cell factory to wrap text and adjust cell height.
     * It is also responsible for adding a button to the action column that shows the copies dialog.
     */
    private void setupColumns() {
        if (bookNameColumn == null || bookTable == null) {
            System.err.println("ERROR: Table components not properly injected");
            return;
        }

        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        copiesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCopyCount()));

        // Apply custom cell factory to wrap text and adjust cell height
        applyWrappingCellFactory(bookNameColumn);
        applyWrappingCellFactory(authorColumn);
        applyWrappingCellFactory(genreColumn);
        applyWrappingCellFactory(descriptionColumn);
        applyWrappingCellFactory(copiesColumn);

        actionColumn.setCellFactory(param -> new TableCell<Book, Void>() {
            private final Button copiesButton = new Button("Show Copies");
            {
                copiesButton.setOnAction(event -> {
                    Book book = getTableView().getItems().get(getIndex());
                    showCopiesDialog(book);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(copiesButton);
                }
            }
        });
    }

    /**
     * This method is called when the LandingWindow.fxml file is loaded.
     * It is responsible for applying a custom cell factory to wrap text and adjust cell height.
     * @param column
     */
    private void applyWrappingCellFactory(TableColumn<Book, String> column) {
        column.setCellFactory(new Callback<TableColumn<Book, String>, TableCell<Book, String>>() {
            @Override
            public TableCell<Book, String> call(TableColumn<Book, String> param) {
                return new TableCell<Book, String>() {
                    private final Text text;

                    {
                        text = new Text();
                        text.wrappingWidthProperty().bind(param.widthProperty());
                        text.setStyle("-fx-padding: 5px;");
                    }

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            text.setText(item);
                            setGraphic(text);
                            setPrefHeight(text.getLayoutBounds().getHeight() + 10); // Adjust height as needed
                        }
                    }
                };
            }
        });
    }

    /**
     * This method is called when the user clicks the Show Copies button.
     * It is responsible for showing the copies dialog for the selected book.
     * @param book
     */
    private void showCopiesDialog(Book book) {
        if (openDialogs.containsKey(book.getBookId())) {
            // Bring the existing dialog to the front
            Stage existingDialog = openDialogs.get(book.getBookId());
            existingDialog.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/CopiesDialog.fxml"));
            AnchorPane pane = loader.load();

            CopiesDialogController controller = loader.getController();
            controller.setCopiesData(FXCollections.observableArrayList(book.getAllCopies()));
            controller.setLibrarianView(false);

            Stage dialog = new Stage();
            Scene scene = new Scene(pane);
            dialog.setScene(scene);
            dialog.setTitle("Copies of: " + book.getTitle());
            dialog.setResizable(false);

            // Add to open dialogs map
            openDialogs.put(book.getBookId(), dialog);

            // Remove from map when closed
            dialog.setOnCloseRequest(event -> openDialogs.remove(book.getBookId()));

            dialog.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * This method is called when the user clicks the Exit button.
     * It is responsible for exiting the application.
     * @param event
     */
    private void closeAllCopiesDialogs() {
        openDialogs.values().forEach(Stage::close);
        openDialogs.clear();
    }

    /**
     * This method is called when the user clicks the Exit button.
     * It is responsible for exiting the application.
     * @param event
     */
    @FXML
    private void handleMenuItemAction(ActionEvent event) {
        MenuItem menuItem = (MenuItem) event.getSource();
        menuButton.setText(menuItem.getText());
    }

    /**
     * This method adjusts the width of the menu button based on the text.
     * @param text
     */
    private void adjustMenuButtonWidth(String text) {
        double width = computeTextWidth(menuButton.getFont(), text) + 50; // Add some flexible padding
        menuButton.setPrefWidth(width);
    }

    /**
     * This method computes the width of the text based on the font.
     * @param font
     * @param text
     * @return
     */
    private double computeTextWidth(Font font, String text) {
        Text tempText = new Text(text);
        tempText.setFont(font);
        return tempText.getLayoutBounds().getWidth();
    }

    /**
     * This method is called to load the book details into the table view.
     * @param list
     */
    public void loadBookDetails(List<Object> list) {
        Platform.runLater(() -> {
            displayMessage("");
            ObservableList<Book> books = FXCollections.observableArrayList();
            Map<Integer, Book> bookMap = new HashMap<>();
            Map<Integer, List<BookCopy>> copyMap = new HashMap<>();
            Map<Integer, BorrowingRecord> borrowingRecordMap = new HashMap<>();

            
            for (Object item : list) {
                if (item instanceof Book) {
                    Book book = (Book) item;
                    bookMap.put(book.getBookId(), book);
                    copyMap.put(book.getBookId(), new ArrayList<>());
                } else if (item instanceof BookCopy) {
                    BookCopy copy = (BookCopy) item;
                    copyMap.get(copy.getBookId()).add(copy);
                } else if (item instanceof BorrowingRecord) {
                    BorrowingRecord record = (BorrowingRecord) item;
                    borrowingRecordMap.put(record.getCopyId(), record);
                }
            }
            
            for (Map.Entry<Integer, Book> entry : bookMap.entrySet()) {
                Book book = entry.getValue();
                List<BookCopy> copies = copyMap.get(book.getBookId());
                for (BookCopy copy : copies) {
                    if (borrowingRecordMap.containsKey(copy.getCopyId())) {
                        copy.setBorrowingRecord(borrowingRecordMap.get(copy.getCopyId()));
                    }
                }
                book.setCopies(copies);
                books.add(book);
            }
    
            bookTable.setItems(books);
            bookTable.setVisible(true);
        });
    }

    /**
     * This method is called when the user clicks the Search button.
     * It is responsible for sending the search request to the server.
     * @param event
     */
    public void handleSearchAction(ActionEvent event) {
        String searchText = getSearch();
        String selectedMenu = getMenu();
        try {
            MessageUtils.sendMessage(ClientUI.cc,"user", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (searchText.trim().isEmpty()) {
                    displayMessage("Please enter a search term");
                } else { 
                    System.out.println("Searching for: " + searchText);
                    System.out.println("Selected menu: " + selectedMenu);
                    MessageUtils.sendMessage(ClientUI.cc, "user",  "sendSearchedBooks", selectedMenu + ":" + searchText);
                }
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    /**
     * This method is called when no books are found.
     * It is responsible for hiding the book table and displaying a message.
     */
    public void noBooksFound() {
        bookTable.setVisible(false);
        displayMessage("No books found");
    }

    /**
     * This method is called when the user clicks the Login button.
     * It is responsible for sending the login request to the server.
     * It is also responsible for closing all copies dialogs.
     * @param event
     */
    public void handleLoginAction(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc, "user",  "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                closeAllCopiesDialogs();
                ((Node) event.getSource()).getScene().getWindow().hide();
                // Load and display MainFrame GUI
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LoginWindow.fxml"));
                Pane root = loader.load();
                Stage stage = new Stage();
                Scene scene = new Scene(root);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "user",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                stage.setScene(scene);
                stage.setTitle("User login");
                stage.setResizable(false);
                stage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
            displayMessage("Error: " + e.getMessage());
        }
    }

    /**
     * This method is called when the user clicks the Exit button.
     * It is responsible for exiting the application.
     * @param event
     */
    public void handleExitAction(ActionEvent event) {
        try {
            if (ClientUI.chat != null) {
                MessageUtils.sendMessage(ClientUI.cc, "user",  "disconnect" , null);
                ClientUI.chat.quit();
            }
            Platform.exit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This method is called when the user clicks the Exit button.
     * It is responsible for exiting the application.
     * @param event
     */
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
