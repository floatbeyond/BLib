package gui.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.control.Button;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.css.PseudoClass;
import client.ClientUI;
import client.NotificationScheduler;
import common.Book;
import common.BookCopy;
import common.BorrowingRecord;
import common.Librarian;
import common.MessageUtils;
import common.Notification;
import client.SharedController;

public class LibrarianMainFrameController {
    
    @FXML private Button btnDataReports;
    @FXML private Button btnShowSubscribers;
    @FXML private Button btnBorrow;
    @FXML private Button btnReaderCard;
    @FXML private Button btnReturnBook;

    @FXML private Label messageLabel;

    @FXML private Button btnNotifications = null;
    @FXML private Button btnCloseNotifications = null;
    @FXML private SplitPane notificationSplitPane;
    @FXML private ListView<Object> notificationListView;
    private ObservableList<Object> notificationItems = FXCollections.observableArrayList();
    private Set<String> notificationMessages = new HashSet<>();

    @FXML private MenuButton menuButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private ImageView searchIcon;

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> bookNameColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, String> descriptionColumn;
    @FXML private TableColumn<Book, String> copiesColumn;
    @FXML private TableColumn<Book, Void> actionColumn;

    private static final PseudoClass DATE_HEADER = PseudoClass.getPseudoClass("date-header");
    private Librarian librarian;
    private Map<Integer, Stage> openDialogs = new HashMap<>(); // Track open dialogs


    private String getSearch() { return searchField.getText(); }
    private String getMenu() { return menuButton.getText(); }

    @FXML
    public void initialize() {
        librarian = SharedController.getLibrarian();
        setupButtonWidth();
        setupColumns();
        setupSearch();
        SharedController.setLibrarianMainFrameController(this);
        NotificationScheduler.start("librarian", librarian.getLibrarian_id());
        setupNotificationListView();
    }

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

    private void setupSearch() {
        searchButton.setOnAction(e -> handleSearchAction(e));
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                handleSearchAction(null);
            }
        });
    }

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
            controller.setLibrarianView(true);

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

    private void closeAllCopiesDialogs() {
        openDialogs.values().forEach(Stage::close);
        openDialogs.clear();
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

    public void handleSearchAction(ActionEvent event) {
        String searchText = getSearch();
        String selectedMenu = getMenu();
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (searchText.trim().isEmpty()) {
                    displayMessage("Please enter a search term");
                } else { 
                    System.out.println("Searching for: " + searchText);
                    System.out.println("Selected menu: " + selectedMenu);
                    MessageUtils.sendMessage(ClientUI.cc, "librarian",  "sendSearchedBooks", selectedMenu + ":" + searchText);
                }
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public void noBooksFound() {
        bookTable.setVisible(false);
        displayMessage("No books found");
    }

    private void setupNotificationListView() {
        notificationListView.setCellFactory(new Callback<ListView<Object>, ListCell<Object>>() {
            @Override
            public ListCell<Object> call(ListView<Object> listView) {
                return new ListCell<Object>() {
                    @Override
                    protected void updateItem(Object item, boolean empty) {
                        super.updateItem(item, empty);
                        pseudoClassStateChanged(DATE_HEADER, item instanceof String);
                        if (item == null || empty) {
                            setGraphic(null);
                        } else if (item instanceof String) {
                            // Date header
                            Text headerText = new Text((String) item);
                            headerText.getStyleClass().add("date-header");
                            setGraphic(headerText);
                        } else if (item instanceof Notification) {
                            // Notification item
                            Notification notification = (Notification) item;
                            VBox vbox = new VBox();
                            Text messageText = new Text(notification.getMessage());
                            Text timeText = new Text(formatTimeSince(notification.getTimestamp().toLocalDateTime()));
                            timeText.getStyleClass().add("time-text");
                            vbox.getChildren().addAll(messageText, timeText);
                            setGraphic(vbox);
                        }
                    }
                };
            }
        });
    }

    private String formatTimeSince(LocalDateTime timestamp) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(timestamp, now);

        if (duration.toMinutes() < 1) {
            return "now";
        } else if (duration.toHours() < 1) {
            return duration.toMinutes() + " mins ago";
        } else if (duration.toDays() < 1) {
            return duration.toHours() + " hours ago";
        } else {
            return (duration.toDays()) + " days ago";
        }
    }

    public void addNotifications(List<Notification> newNotifications) {
        for (Notification notification : newNotifications) {
            if (!notificationMessages.contains(notification.getMessage())) {
                LocalDate notificationDate = notification.getTimestamp().toLocalDateTime().toLocalDate();
                String dateHeader = notificationDate.format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
                boolean headerExists = false;
                
                // Find existing header or create new one
                for (int i = 0; i < notificationItems.size(); i++) {
                    if (notificationItems.get(i) instanceof String && 
                        notificationItems.get(i).equals(dateHeader)) {
                        notificationItems.add(i + 1, notification);
                        headerExists = true;
                        break;
                    }
                }
                
                // Create new header if needed
                if (!headerExists) {
                    notificationItems.add(0, dateHeader);
                    notificationItems.add(1, notification);
                }
                
                notificationMessages.add(notification.getMessage());
            }
        }
        notificationListView.setItems(notificationItems);
    }

    @FXML
    private void showNotifications(ActionEvent event) {
        notificationSplitPane.setVisible(true);
    }

    @FXML
    private void closeNotifications(ActionEvent event) {
        notificationSplitPane.setVisible(false);
    }

    @FXML
    private void goShowSubscribers(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/SubscribersTableForm.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setSubscribersTableController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Show Subscribers");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goDataReports(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/DataReports.fxml"));
                Parent root = fxmlLoader.load();
                SharedController.setDataReportsController(fxmlLoader.getController());

                // Fetch and set the reports data after the controller is initialized
                MessageUtils.sendMessage(ClientUI.cc, "librarian", "fetchAllReports", null);

                Stage stage = new Stage();
                stage.setTitle("Data Reports");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goBorrow(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/BorrowForm.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setBorrowFormController(fxmlLoader.getController());
                NotificationScheduler.stop();
                Stage stage = new Stage();
                stage.setTitle("Borrow");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goReaderCard(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/ReaderCard.fxml"));
                Parent root = fxmlLoader.load();

                // SharedController.setReaderCardController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Reads Card");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else { 
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goReturnBook(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/ReturnBookFrame.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setReturnBookController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Return Book");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else { 
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goAddSubscriber(ActionEvent event) {
        try {
            MessageUtils.sendMessage(ClientUI.cc,"librarian", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/fxml/AddSubscriber.fxml"));
                Parent root = fxmlLoader.load();

                SharedController.setAddSubscriberController(fxmlLoader.getController());
                Stage stage = new Stage();
                stage.setTitle("Return Book");
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                ((Node) event.getSource()).getScene().getWindow().hide();
                stage.setResizable(false);
                stage.show();
            } else { 
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goLogOut(ActionEvent event) {
        closeAllCopiesDialogs();
        try {
            if (SharedController.getSubscribersTableController() != null) {
                SharedController.getSubscribersTableController().closeAllReaderCards();
            } else System.out.println("SubscribersTableController is null");
            NotificationScheduler.stop();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LandingWindow.fxml"));
            Pane root = loader.load();
            
            Stage stage = new Stage();
            Scene scene = new Scene(root);			
            stage.setTitle("Landing Window");
            stage.setScene(scene);
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                try {
                    if (ClientUI.chat != null) {
                        MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , null);
                        ClientUI.chat.quit();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });    
            ((Node)event.getSource()).getScene().getWindow().hide();
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void displayMessage(String message) {
        messageLabel.setText(message);
    }
}
