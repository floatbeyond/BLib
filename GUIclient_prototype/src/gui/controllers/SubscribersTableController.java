package gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import common.Subscriber;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class SubscribersTableController implements Initializable {

    @FXML private TableView<Subscriber> subscriberTable;
    @FXML private TableColumn<Subscriber, Integer> colID;
    @FXML private TableColumn<Subscriber, String> colName;
    @FXML private TableColumn<Subscriber, String> colStatus;
    @FXML private TableColumn<Subscriber, String> colPhone;
    @FXML private TableColumn<Subscriber, String> colEmail;
    @FXML private TableColumn<Subscriber, Integer> colPenalties;
    @FXML private TableColumn<Subscriber, LocalDate> colfrozenUntil;
    @FXML private TableColumn<Subscriber, LocalDate> coljoinDate;
    @FXML private TableColumn<Subscriber, LocalDate> colexpDate;
    @FXML private TableColumn<Subscriber, LocalDate> colBorrows;
    @FXML private TableColumn<Subscriber, LocalDate> colOrders;

    @FXML private Button btnBack = null;
    @FXML private Button btnRefresh;
    @FXML private Button searchButton;
    @FXML private TextField searchField;
    
    private Map<Integer, Stage> readerCardStages = new HashMap<>();
    private ObservableList<Subscriber> allSubscribers; // Add this field
	
    /**
     * Initializes the controller class.
     * This method is automatically called after the fxml file has been loaded.
     * Setups the columns and search function.
     * Loads the subscriber data from the server.
     * @param location
     * @param resources
     */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
        setupColumns();
        setupSearchFunction();
        loadData();	
	}

    /**
     * Setups the columns of the table.
     * Sets the value factories for each column.
     * Adds a double click event to show the reader card of the selected subscriber.
     */
    private void setupColumns() {
        if (colID == null || subscriberTable == null) {
            System.err.println("ERROR: Table components not properly injected");
            return;
        }

        // Set value factories
        colID.setCellValueFactory(new PropertyValueFactory<>("sub_id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("sub_name"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("sub_status"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("sub_phone_num"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("sub_email"));
        colPenalties.setCellValueFactory(new PropertyValueFactory<>("sub_penalties"));
        colfrozenUntil.setCellValueFactory(new PropertyValueFactory<>("sub_freeze"));
        coljoinDate.setCellValueFactory(new PropertyValueFactory<>("sub_joined"));
        colexpDate.setCellValueFactory(new PropertyValueFactory<>("sub_expiration"));
        colBorrows.setCellValueFactory(new PropertyValueFactory<>("currentlyBorrowed"));
        colOrders.setCellValueFactory(new PropertyValueFactory<>("currentlyOrdered"));

        subscriberTable.setEditable(false);

        subscriberTable.setRowFactory(tv -> {
            TableRow<Subscriber> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Subscriber rowData = row.getItem();
                    showReaderCard(rowData);
                }
            });
            return row;
        });
    }

    /**
     * Setups the search function.
     * Adds an action event to the search button.
     * Adds an enter key handler for the search field.
     */
    private void setupSearchFunction() {
        searchButton.setOnAction(event -> handleSearchAction());
        // Add enter key handler for search field
        searchField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleSearchAction();
            }
        });
    }

    /**
     * Loads the subscriber data from the server.
     * Sends a message to the server to show the subscribers table.
     */
    private void loadData() {
        Platform.runLater(() -> {
			System.out.println("Loading initial data...");
			MessageUtils.sendMessage(ClientUI.cc, "librarian", "showSubscribersTable", null);
		});
    }

    /**
     * Parses the subscriber list received from the server.
     * Sets the subscriber list to the table.
     * Stores the original data in a field.
     * @param subscribers
     */
	public void parseSubscriberList(ObservableList<Subscriber> subscribers) {
		if (subscribers == null) return;
        allSubscribers = subscribers;  // Store original data
		subscriberTable.setItems(subscribers);
		System.out.println("Loaded " + subscribers.size() + " subscribers");
	}

    /**
     * Shows the reader card of the selected subscriber.
     * If the reader card is already open, brings it to front.
     * @param subscriber
     */
    private void showReaderCard(Subscriber subscriber) {
        try {
            // Check if window for this subscriber exists
            Stage existingStage = readerCardStages.get(subscriber.getSub_id());
            if (existingStage != null && existingStage.isShowing()) {
                SharedController.setSubscriber(subscriber);
                // SharedController.getReaderCardController().updateSubscriberInfo(subscriber);
                existingStage.toFront();
                return;
            }

            SharedController.setSubscriber(subscriber);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ReaderCard.fxml"));
            Pane root = loader.load();

            ReaderCardController controller = loader.getController();
            SharedController.setReaderCardController(controller);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Subscriber Details");
            stage.setScene(scene);
            // Add to map before showing
            readerCardStages.put(subscriber.getSub_id(), stage);            
            stage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
                readerCardStages.remove(subscriber.getSub_id());
            });
            stage.setResizable(false);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the refresh button click event.
     * Closes all reader cards and clears the subscriber table.
     * Sends a message to the server to show the subscribers table.
     * @param event
     */
    public void handleRefresh(ActionEvent event) {
        closeAllReaderCards();
        subscriberTable.getItems().clear();
        MessageUtils.sendMessage(ClientUI.cc, "librarian", "showSubscribersTable", null);
        subscriberTable.refresh();
    }

    /**
     * Closes all reader cards.
     */
    public void closeAllReaderCards() {
        readerCardStages.values().forEach(Stage::close);
        readerCardStages.clear();
    }

    /**
     * Closes the reader card of the specified subscriber.
     * @param subId
     */
    public void closeReaderCard(int subId) {
        Stage stage = readerCardStages.get(subId);
        if (stage != null) {
            stage.close();
            readerCardStages.remove(subId);
        }
    }

    /**
     * Handles the search action.
     * Filters the subscriber list based on the search text.
     * Updates the table with the filtered results.
     */
    @FXML 
    private void handleSearchAction() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            // Reset to full list if search is empty
            subscriberTable.setItems(allSubscribers);
            return;
        }
        
        // Filter subscribers
        List<Subscriber> filteredList = allSubscribers.stream()
            .filter(subscriber -> 
                subscriber.getSub_name().toLowerCase().contains(searchText) ||
                String.valueOf(subscriber.getSub_id()).contains(searchText))
            .collect(Collectors.toList());
        
        // Update table with filtered results    
        subscriberTable.setItems(FXCollections.observableArrayList(filteredList));
    }

    /**
     * Handles the back button click event.
     * Closes the current window and opens the librarian main frame.
     * @param event
     * @throws Exception
     */
    public void goBackBtn(ActionEvent event) throws Exception {
        System.out.println("goBackBtn clicked");
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LibrarianMainFrame.fxml"));
		Pane root = loader.load();
		
		Stage primaryStage = new Stage();
		Scene scene = new Scene(root);			
		primaryStage.setTitle("Library Tool");
		primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest((WindowEvent xWindowEvent) -> {
            try {
                if (ClientUI.chat != null) {
                    MessageUtils.sendMessage(ClientUI.cc, "librarian",  "disconnect" , SharedController.getLibrarian().getLibrarian_id());
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
}