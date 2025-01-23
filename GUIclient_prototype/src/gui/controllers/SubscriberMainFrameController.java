package gui.controllers;

import client.ClientUI;
import client.SharedController;
import common.Book;
import common.BookCopy;
import common.BorrowingRecord;
import common.MessageUtils;
import common.OrderRecordDTO;
import common.Subscriber;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import common.OrderResponse;
import common.Notification;


import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;


public class SubscriberMainFrameController implements Initializable {

    @FXML private MenuButton menuButton;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    @FXML private ImageView searchIcon;
    @FXML private Button btnLogOut = null;
    @FXML private Button btnLogs = null;
    @FXML private Button btnPersonalDetails = null;
    @FXML private Button btnActiveOrders = null;
    @FXML private Button btnNotifications = null;
    @FXML private Button btnCloseNotifications = null;

    @FXML private SplitPane notificationSplitPane;
    @FXML private ListView<String> notificationListView;

    @FXML private TableView<Book> bookTable;
    @FXML private TableColumn<Book, String> bookNameColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> genreColumn;
    @FXML private TableColumn<Book, String> descriptionColumn;
    @FXML private TableColumn<Book, String> copiesColumn;
    @FXML private TableColumn<Book, Void> actionColumn;

    @FXML private Label messageLabel;

    private static Stage activeOrdersStage = null;
    private static Stage activeBorrowsStage = null; // Add implementation
    private Map<Integer, Stage> openDialogs = new HashMap<>(); // Track open dialogs
    private Subscriber s;
    private List<OrderRecordDTO> orderRecords = new ArrayList<>();
    private ObservableList<String> notifications = FXCollections.observableArrayList();

    private String getSearch() { return searchField.getText(); }
    private String getMenu() { return menuButton.getText(); }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtonWidth();
        setupColumns();
        setupSearch();
        SharedController.setSubscriberMainFrameController(this);
        s = SharedController.getSubscriber();
        // NotificationScheduler.start(s.getSub_id());
        MessageUtils.sendMessage(ClientUI.cc, "subscriber", "userOrders", s.getSub_id());
        MessageUtils.sendMessage(ClientUI.cc, "subscriber", "fetchNotifications", s.getSub_id());
    }

    public void addNotifications(List<Notification> newNotifications) {
        for (Notification notification : newNotifications) {
            notifications.add(notification.getMessage());
        }
    }

    @FXML
    private void showNotifications(ActionEvent event) {
        notificationSplitPane.setVisible(true);
        notificationListView.setItems(notifications);
    }

    @FXML
    private void closeNotifications(ActionEvent event) {
        notificationSplitPane.setVisible(false);
    }

    public void handleOrderResponse(OrderResponse response) {
        String status = response.getStatus();
        Book orderedBook = response.getBook();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Order");
        alert.setHeaderText(null);
        alert.setContentText(status);
        alert.showAndWait();

        if (orderedBook != null && status.contains("success")) {
            System.out.println("Order successful for book: " + orderedBook.getTitle());
            Platform.runLater(() -> {
                for (Book book : bookTable.getItems()) {
                    if (book.getBookId() == orderedBook.getBookId()) {
                        book.setOrdered(true);
                        break;
                    }
                }
                bookTable.refresh();
            });
        } else {
            Platform.runLater(() -> {
                bookTable.refresh();
            });
        }
    }

    private void orderBook(Book book) {
        // Implement the logic to order the book
        System.out.println("Ordering book: " + book.getTitle());

        // Send a request to the server to order the book
        MessageUtils.sendMessage(ClientUI.cc, "subscriber", "newOrder", book.getBookId() + ":" + s.getSub_id());
    }

    public void setOrderRecords(List<OrderRecordDTO> list) {
        this.orderRecords = list;
        loadUserOrders();
    }

    public static void setActiveOrdersStage(Stage stage) {
        activeOrdersStage = stage;
    }

    private void loadUserOrders() {
        List<String> orderedBookTitles = new ArrayList<>();
        for (OrderRecordDTO order : orderRecords) {
            orderedBookTitles.add(order.getBookTitle());
        }
        for (Book book : bookTable.getItems()) {
            if (orderedBookTitles.contains(book.getTitle())) {
                book.setOrdered(true);
            }
        }
        bookTable.refresh();
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
        private final Button orderButton = new Button("Order");

        {
            copiesButton.setOnAction(event -> {
                Book book = getTableView().getItems().get(getIndex());
                showCopiesDialog(book);
            });

            orderButton.setOnAction(event -> {
                Book book = getTableView().getItems().get(getIndex());
                orderBook(book);
            });
        }

        @Override
        protected void updateItem(Void item, boolean empty) {
            super.updateItem(item, empty);
            if (empty) {
                setGraphic(null);
            }else {
                Book book = getTableView().getItems().get(getIndex());
                try {
                    int copyCount = book.getAvailableCopies().size();
                    if (copyCount > 0) {
                        setGraphic(copiesButton);
                    } else {
                        setGraphic(orderButton);
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Invalid copy count: " + book.getCopyCount());
                    setGraphic(null);
                }
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
        // Implement your search logic here
        try {
            MessageUtils.sendMessage(ClientUI.cc, "subscriber", "connect" , null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                if (searchText.trim().isEmpty()) {
                    displayMessage("Please enter a search term");
                } else { 
                    System.out.println("Searching for: " + searchText);
                    System.out.println("Selected menu: " + selectedMenu);
                    MessageUtils.sendMessage(ClientUI.cc,"subscriber",  "sendSearchedBooks", selectedMenu + ":" + searchText);
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

    public void logoutBtn(ActionEvent event) throws Exception {
        closeActiveOrders(); // Add this line

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LandingWindow.fxml"));
        Pane root = loader.load();
        // NotificationScheduler.stop();
        
        Stage primaryStage = new Stage();
        Scene scene = new Scene(root);			
        primaryStage.setTitle("Landing Window");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    MessageUtils.sendMessage(ClientUI.cc, "user",  "disconnect" , null);
                    ClientUI.chat.quit();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        ((Node)event.getSource()).getScene().getWindow().hide();
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public void logsBtn(ActionEvent event) throws Exception {
        try {
            MessageUtils.sendMessage(ClientUI.cc, "subscriber", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                int sub_id = SharedController.getSubscriber().getSub_id();
                MessageUtils.sendMessage(ClientUI.cc, "subscriber", "userLogs", sub_id);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/SubscriberLogs.fxml"));
                Pane root = loader.load();
                
                SharedController.setSubscriberLogsController(loader.getController());
                Stage primaryStage = new Stage();
                Scene scene = new Scene(root);			
                primaryStage.setTitle("Subscriber Logs");
                primaryStage.setScene(scene);
                primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "user",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                
                ((Node)event.getSource()).getScene().getWindow().hide();
                primaryStage.setResizable(false);
                primaryStage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void personalDetailsBtn(ActionEvent event) throws Exception {

        try {
            MessageUtils.sendMessage(ClientUI.cc, "subscriber", "connect", null);
            if (ClientUI.cc.getConnectionStatusFlag() == 1) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/PersonalDetails.fxml"));
                Pane root = loader.load();
                
                SharedController.setPersonalDetailsController(loader.getController());
                Stage primaryStage = new Stage();
                Scene scene = new Scene(root);			
                primaryStage.setTitle("Personal Details");
                primaryStage.setScene(scene);
                primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                    try {
                        if (ClientUI.chat != null) {
                            MessageUtils.sendMessage(ClientUI.cc, "user",  "disconnect" , null);
                            ClientUI.chat.quit();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                
                ((Node)event.getSource()).getScene().getWindow().hide();
                primaryStage.setResizable(false);
                SharedController.pdc.loadSubscriber(s);
                primaryStage.show();
            } else {
                displayMessage("No server connection");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleActiveOrders(ActionEvent event) throws Exception {
        MessageUtils.sendMessage(ClientUI.cc, "subscriber", "connect", null);
        if (ClientUI.cc.getConnectionStatusFlag() == 1) {
            MessageUtils.sendMessage(ClientUI.cc, "subscriber", "userOrders", s.getSub_id());
            Platform.runLater(() -> {
                try {
                    if (orderRecords.size() == 0) {
                        displayMessage("No active orders");
                        return;
                    }
                    if (activeOrdersStage != null && activeOrdersStage.isShowing()) {
                        activeOrdersStage.toFront();
                        return;
                    }        

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ActiveOrders.fxml"));
                    AnchorPane pane = loader.load();

                    ActiveOrdersController controller = loader.getController();
                    SharedController.setActiveOrdersController(controller);
                    controller.setOrdersData(FXCollections.observableArrayList(orderRecords));

                    activeOrdersStage = new Stage();
                    Scene scene = new Scene(pane);
                    activeOrdersStage.setScene(scene);
                    activeOrdersStage.setTitle("Orders");
                    activeOrdersStage.setResizable(false);
                    activeOrdersStage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            displayMessage("No server connection");
        }
    }

    private void closeActiveOrders() {
        if (activeOrdersStage != null && activeOrdersStage.isShowing()) {
            activeOrdersStage.close();
            activeOrdersStage = null;
        }
    }
    
    public void displayMessage(String message) {
        messageLabel.setText(message);
    }

}
