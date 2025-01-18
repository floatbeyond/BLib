package gui.controllers;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

import client.ClientUI;
import client.SharedController;
import common.MessageUtils;
import common.Subscriber;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

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
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		System.out.println("Initializing controller...");
	

		// Initialize columns
		setupColumns();

		// Enable cell selection
		subscriberTable.setEditable(false);
	
        // Handle double-click on a row
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
		
		// Load initial data
		Platform.runLater(() -> {
			System.out.println("Loading initial data...");
			MessageUtils.sendMessage(ClientUI.cc, "librarian", "showSubscribersTable", null);
		});
	}

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
    }

	public void parseSubscriberList(ObservableList<Subscriber> subscribers) {
		if (subscribers == null) return;
		subscriberTable.setItems(subscribers);
		System.out.println("Loaded " + subscribers.size() + " subscribers");
	}

    private void showReaderCard(Subscriber subscriber) {
        try {
            SharedController.setSubscriber(subscriber);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/ReaderCard.fxml"));
            Pane root = loader.load();

            ReaderCardController controller = loader.getController();
            SharedController.setReaderCardController(controller);

            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setTitle("Subscriber Details");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleRefresh(ActionEvent event) {
        System.out.println("Refresh button clicked");
        MessageUtils.sendMessage(ClientUI.cc, "librarian", "showSubscribersTable", null);
    }

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
}